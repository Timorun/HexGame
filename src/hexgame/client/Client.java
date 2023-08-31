package hexgame.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Scanner scanner;
    private boolean inGame = false;

    public Client(String ipAddress, int port) throws Exception {
        socket = new Socket(ipAddress, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        scanner = new Scanner(System.in);
    }


    String username;
    boolean loggedin = false;
    boolean queued = false;
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

        TimeUnit.SECONDS.sleep(1);
        // Login
        System.out.println("Enter your username: ");
        username = scanner.nextLine();
        sendCommand("LOGIN~" + username);

        // Main loop for user input
        while (true) {
            TimeUnit.SECONDS.sleep(2); //give time for responses to come in
            if (inGame && myturn) {
                System.out.println("Enter your move: ");
                String move = scanner.nextLine();
                sendCommand("MOVE~" + move);
            } else if (!loggedin) {
                username = scanner.nextLine();
                sendCommand("LOGIN~" + username);
            } else if (!queued) {
                System.out.println("Enter 'QUEUE' to join a game or 'LIST' to see online users: ");
                String command = scanner.nextLine().toUpperCase();
                if ("QUEUE".equals(command)) {
                    queued = true;
                    System.out.println("Waiting for other users to queue up...");
                }
                if ("QUEUE".equals(command) || "LIST".equals(command)) {
                    sendCommand(command);
                }
            } else {
                continue;
            }
        }
    }


    boolean myturn = false;
    char mycolor = 'B';
    char opcolor = 'R';
    int lastmove = 0; //to know incase swap move
    private void handleServerCommand(String command) throws Exception {
        String[] parts = command.split("~");
        switch (parts[0]) {
            case "HELLO":
                System.out.println("Received HELLO from server: " + parts[1]);
                break;
            case "LOGIN":
                System.out.println("Successfully logged in.");
                loggedin = true;
                break;
            case "ALREADYLOGGEDIN":
                System.out.println("Username already used. Try a different username: ");
                break;
            case "LIST":
                System.out.println("List of online users: " + String.join(", ", parts[1].split("~")));
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
                } else {
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
                } else {
                    myturn = true;
                    color = opcolor;
                }
                updateBoard(move, color);
                lastmove = move;
                printBoard();
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
                break;
            case "ERROR":
                System.out.println("Error: " + parts[1]);
                break;
            default:
                System.out.println("Unknown command received: " + command);
                break;
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
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(cell + " ");
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


    private void sendCommand(String command) {
        out.println(command);
    }

    public static void main(String[] args) {
        try {
            Client client = new Client("localhost", 8888);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
