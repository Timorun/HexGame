package hexgame.client;

import hexgame.ai.AI;
import hexgame.ai.GreedyAI;
import hexgame.ai.RandomAI;
import hexgame.core.Board;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Arrays;

import static java.lang.System.exit;

/**
 * The Client class handles client-side functionality for the Hex game.
 * It is responsible for connecting to the server and managing game state.
 */
/**
 * Client method.
 */
public class Client {
    /**
     * socket; method.
     */
    private Socket socket;
    /**
     * out; method.
     */
    private PrintWriter out;
    /**
     * in; method.
     */
    private BufferedReader in;
    /**
     * scanner; method.
     */
    private Scanner scanner;
    /**
     * inGame method.
     */
    private boolean inGame = false;
    boolean swapmove = false;

    Board aiboard = new Board();

    /**
     * ipAddress, method.
     */
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

    boolean aichosen = false;
    boolean useai = false;
    AI ai = null;
    /**
     * start method.
     */
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
            if (inGame && !aichosen) {
                System.out.println("Do you want the AI to make moves for you?" +
                        "\nPress 1 for RandomAI, 2 for GreedyAI, or 0 for no AI");
                String input = scanner.nextLine();

                try {
                    int option = Integer.parseInt(input);
                    switch (option) {
                        case 1:
                            System.out.println("Moves will be made by RandomAI");
                            useai = true;
                            aichosen = true;
                            ai = new RandomAI(aiboard);
                            break;

                        case 2:
                            System.out.println("Moves will be made by GreedyAI");
                            useai = true;
                            aichosen = true;
                            ai = new GreedyAI(aiboard, mycolor);
                            break;

                        case 0:
                            aichosen = true;
                            System.out.println("No AI chosen");
                            break;

                        default:
                            System.out.println("Press 1 for RandomAI, 2 for GreedyAI, or 0 for no AI");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Input a number");
                }
                scanner = new Scanner(System.in);
            }

            if (inGame && myturn && aichosen) {
                if (useai) {
                    int move = ai.getNextMove();
                    sendCommand("MOVE~" + move);
                    swapmove = false;
                } else {
                    System.out.println("Enter your move or type HINT:");
                    String input = scanner.nextLine();
                    try {
                        int move = Integer.parseInt(input);  // Convert string to integer
                        if (isMoveValid(move)) {
                            sendCommand("MOVE~" + move);
                            swapmove = false;
                        } else {
                            System.out.println("Invalid move. Please try again.");
                        }
                    } catch (NumberFormatException e) {
                        String command = input.toUpperCase();
                        if ("HINT".equals(command)) {
                            int hintmove = getHintMove();
                            System.out.println("Try number:" + hintmove);
                        } else {
                            System.out.println("Input a number");
                        }
                    }
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

    /**
     * isMoveValid method to check locally if move is valid before sending to server
     */
    public boolean isMoveValid(int move) {
        if (move == 81) {
            return swapmove;
        }
        if (move < 0 || move > 80) {
            return false;
        }
        int row = move / 9;
        int col = move % 9;
        return clientboard[row][col] == '.';
    }


    boolean myturn = false;
    char mycolor = 'B';
    char opcolor = 'R';
    int lastmove = 0; //to know incase swap move
    /**
     * handleServerCommand method
     * Which is ran from the seperate thread
     * Parsing the commands received from server
     */
    private void handleServerCommand(String command) throws Exception {
//        System.out.println(command);
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
                    int move = Integer.parseInt(parts[1]);
                    if (myturn) {
                        System.out.println("Move you did: " + parts[1]);
                        myturn = false;
                        updateBoard(move, mycolor);
                        lastmove = move;
                        printBoard();
                        aiboard.placePiece(move, mycolor);
                    } else {
                        System.out.println("Move by opponent: " + parts[1]);
                        myturn = true;
                        updateBoard(move, opcolor);
                        lastmove = move;
                        printBoard();
                        aiboard.placePiece(move, opcolor);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                    break;
                case "GAMEOVER":
                    String reason = parts[1];
                    String winner = parts.length > 2 ? parts[2] : "None";
                    System.out.println("Game over. Reason: " + reason + ", Winner: " + winner);
                    if (winner.equals(username)) {
                        System.out.println("Congrats you won!");
                    } else {
                        System.out.println("You'll get em' next time!");
                    }
                    inGame = false;
                    myturn = false;
                    mycolor = 'B';
                    opcolor = 'R';
                    queued = false;
                    useai = false;
                    aichosen = false;
                    aiboard = new Board();
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


    private char[][] clientboard;

    /**
     * initBoard method to reset board
     */
    public void initBoard() {
        clientboard = new char[9][9];
        for (char[] row : clientboard) {
            Arrays.fill(row, '.'); // Assuming '.' represents an empty cell
        }
    }

    /**
     * print board to console
     */
    public void printBoard() {
        int cellNumber = 0;

        // Print the top RED border
        System.out.println("                     RED");

        // Start of the board
        String blue = "BLUE";
        for (int row = 0; row < 9; row++) {
            // Print the left BLUE border
            if (row >= 3 && row <= 6) {
                System.out.print(blue.charAt(row - 3) + " ");
            } else {
                System.out.print("  ");
            }

            // Print the board cells
            for (int col = 0; col < 9; col++) {
                if (clientboard[row][col] == '.') {
                    System.out.print("(" + (cellNumber < 10 ? "0" : "") + cellNumber + ") ");
                } else {
                    System.out.print("  " + clientboard[row][col] + "  ");
                }
                cellNumber++;
            }

            // Print the right BLUE border
            if (row >= 3 && row <= 6) {
                System.out.println(" " + blue.charAt(row - 3));
            } else {
                System.out.println();
            }
        }

        // Print the bottom RED border
        System.out.println("                     RED");
    }


    /**
     * updateBoard method to update board to display in console
     */
    public void updateBoard(int move, char color) {
        if (move == 81) {
            move = lastmove;
            int row = move / 9;
            int col = move % 9;
            clientboard[row][col] = '.';
            clientboard[col][row] = 'B';
        } else {
            int row = move / 9;
            int col = move % 9;
            clientboard[row][col] = color;
        }
    }


    /**
     * sendCommand method
     * Sends a command to out printwriter
     */
    private void sendCommand(String command) throws InterruptedException {
        out.println(command);
        synchronized (lock) {
            lock.wait();
        }
    }

    /**
     * getHintMove method providing a valid move as close to the center as possible
     */
    public int getHintMove() {
        for (int i = 0; i <= 40; i++) {
            if (isMoveValid(40-i)) {
                return 40-i;
            } else if (isMoveValid(40+i)) {
                return 40+i;
            }
        }
        return 0;
    }

    /**
     * Main method to run
     * Set IP address and port of server to connect to
     */
    public static void main(String[] args) {
        // Ref server: 130.89.253.64 port 44445
        // local:  localhost port 8888

        try {
//            Client client = new Client("localhost", 8888);
            Client client = new Client("130.89.253.64", 44445);
            client.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
