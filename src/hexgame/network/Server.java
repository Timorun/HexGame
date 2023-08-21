package hexgame.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int PORT = 8080;
    private ServerSocket serverSocket;

    public Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());

                // Handle client in a new thread
                new Thread(() -> handleClient(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        // TODO: Implement game logic, communication with the client, etc.
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}
