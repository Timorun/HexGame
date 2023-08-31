package hexgame.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

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

    public void start() throws Exception {
        // Handshake
        System.out.println("Enter your client description: ");
        String clientDescription = scanner.nextLine();
        sendCommand("HELLO~" + clientDescription);

        // Login
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        sendCommand("LOGIN~" + username);

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

        // Main loop for user input
        while (true) {
            if (inGame) {
                System.out.println("Enter your move: ");
                String move = scanner.nextLine();
                sendCommand("MOVE~" + move);
            } else {
                System.out.println("Enter 'QUEUE' to join a game or 'LIST' to see online users: ");
                String command = scanner.nextLine().toUpperCase();
                if ("QUEUE".equals(command) || "LIST".equals(command)) {
                    sendCommand(command);
                }
            }
        }
    }

    private void handleServerCommand(String command) throws Exception {
        String[] parts = command.split("~");
        switch (parts[0]) {
            case "HELLO":
                System.out.println("Received HELLO from server: " + parts[1]);
                break;
            case "LOGIN":
                System.out.println("Successfully logged in.");
                break;
            case "ALREADYLOGGEDIN":
                System.out.println("Username already logged in. Try again.");
                break;
            case "LIST":
                System.out.println("List of online users: " + String.join(", ", parts[1].split("~")));
                break;
            case "NEWGAME":
                System.out.println("New game started. Players: " + parts[1] + ", " + parts[2]);
                inGame = true;
                break;
            case "MOVE":
                System.out.println("Move made: " + parts[1]);
                break;
            case "GAMEOVER":
                String reason = parts[1];
                String winner = parts.length > 2 ? parts[2] : "None";
                System.out.println("Game over. Reason: " + reason + ", Winner: " + winner);
                inGame = false;
                break;
            case "ERROR":
                System.out.println("Error: " + parts[1]);
                break;
            default:
                System.out.println("Unknown command received: " + command);
                break;
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
