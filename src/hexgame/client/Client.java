package hexgame.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Arrays;

import static java.lang.System.exit;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private boolean inGame = false;
    boolean swapmove = false;

    public Client(String ipAddress, int port) throws Exception {
        socket = new Socket(ipAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        scanner = new Scanner(System.in);
    }

    String username;
    boolean loggedin = false;
    boolean queued = false;
    final Object lock = new Object();
    public void start() throws Exception {
        // Main loop for server commands
        new Thread(() -> {
            try {
                while (true) {
                    String serverCommand = in.readLine();
                    handleServerCommand(serverCommand);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Handshake
        System.out.println("Enter your client description: ");
        String clientDescription = scanner.nextLine();
        sendCommand("HELLO~" + clientDescription);


        // Login
        System.out.println("Enter your username: ");
        username = scanner.nextLine();
        sendCommand("LOGIN~" + username);
        while (!loggedin) {
            username = scanner.nextLine();
            sendCommand("LOGIN~" + username);
        }



        // Main loop for user input
        while (true) {
            scanner = new Scanner(System.in);
            if (inGame && myturn) {
                System.out.println("Enter your move: ");
                try {
                    int move = Integer.parseInt(scanner.nextLine());  // Convert string to integer
                    if (isMoveValid(move)) {
                        sendCommand("MOVE~" + move);
                        swapmove = false;
                    } else {
                        System.out.println("Invalid move. Please try again.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input a number");
                }

            } else if (!queued) {
                String command = scanner.nextLine().toUpperCase();
                if ("QUEUE".equals(command)) {
                    queued = true;
                    System.out.println("Waiting for other users to queue up...");
                    sendCommand(command);
                } else if ("LIST".equals(command)) {
                    sendCommand(command);
                } else if ("HELP".equals(command)) {
                    System.out.println("Enter 'QUEUE' to queue up for a game or 'LIST' to see online users");
                } else {
                    System.out.println("Invalid command");
                }
            }
        }
    }

    public boolean isMoveValid(int move) {
        if (move == 81) {
            return swapmove;
        }
        if (move < 0 || move > 80) {
            return false;
        }
        int row = move / 9;
        int col = move % 9;
        return board[row][col] == '.';
    }


    boolean myturn = false;
    char mycolor = 'B';
    char opcolor = 'R';
    int lastmove = 0; //to know incase swap move
    private void handleServerCommand(String command) throws Exception {
        System.out.println(command);
        if (command == null) {
            System.out.println("Error");
            exit(0);
        } else {

            String[] parts = command.split("~");
            switch (parts[0]) {
                case "HELLO":
                    System.out.println("Received HELLO from server: " + parts[1]);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
                case "LOGIN":
                    System.out.println("Successfully logged in.");
                    System.out.println("Enter 'QUEUE' to queue up for a game or 'LIST' to see online users");
                    loggedin = true;
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
                case "ALREADYLOGGEDIN":
                    System.out.println("Username already used. Try a different username: ");
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
                case "LIST":
                    System.out.print("List of online users: " + parts[1]);
                    for (int i = 2; i < parts.length; i++) {
                        System.out.print(", " + parts[i]);
                    }
                    System.out.println(" ");
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
                case "NEWGAME":
                    inGame = true;
                    initBoard();
                    System.out.println("New game started. Players " + parts[1] + " vs " + parts[2]);
                    if (parts[1].equals(username)) {
                        myturn = true;
                        mycolor = 'R';
                        opcolor = 'B';
                        printBoard();
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    } else {
                        swapmove = true;
                        printBoard();
                        System.out.println("Wait for opponent's move");
                    }
                    break;
                case "MOVE":
                    System.out.println("Move made: " + parts[1]);
                    int move = Integer.parseInt(parts[1]);
                    char color;
                    if (myturn) {
                        myturn = false;
                        color = mycolor;
                        updateBoard(move, color);
                        lastmove = move;
                        printBoard();
                    } else {
                        myturn = true;
                        color = opcolor;
                        updateBoard(move, color);
                        lastmove = move;
                        printBoard();
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                    break;
                case "GAMEOVER":
                    String reason = parts[1];
                    String winner = parts.length > 2 ? parts[2] : "None";
                    System.out.println("Game over. Reason: " + reason + ", Winner: " + winner);
                    inGame = false;
                    myturn = false;
                    mycolor = 'B';
                    opcolor = 'R';
                    queued = false;
                    System.out.println("Enter 'QUEUE' to queue up for a game or 'LIST' to see online users");
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
                case "ERROR":
                    System.out.println("Error: " + parts[1]);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
                default:
                    System.out.println("Unknown command received: " + command);
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                    break;
            }
        }
    }

    private char[][] board;
    public void initBoard() {
        board = new char[9][9];
        for (char[] row : board) {
            Arrays.fill(row, '.'); // Assuming '.' represents an empty cell
        }
    }

    public void printBoard() {
        int cellNumber = 0;
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    System.out.print("(" + (cellNumber < 10 ? "0" : "") + cellNumber + ") ");
                } else {
                    System.out.print("  "+board[row][col]+"  ");
                }
                cellNumber++;
            }
            System.out.println();
        }
    }

    public void updateBoard(int move, char color) {
        if (move == 81) {
            move = lastmove;
            int row = move / 9;
            int col = move % 9;
            board[row][col] = '.';
            board[col][row] = 'B';
        } else {
            int row = move / 9;
            int col = move % 9;
            board[row][col] = color;
        }
    }


    private void sendCommand(String command) throws InterruptedException {
        out.println(command);
        synchronized (lock) {
            lock.wait();
        }
    }

    public static void main(String[] args) {
        // Ref server: 130.89.253.64 port 44445
        // local:  localhost port 8888

        try {
            Client client = new Client("130.89.253.64", 44445);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
