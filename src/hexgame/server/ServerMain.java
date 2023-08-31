package hexgame.server;
import java.net.*;
import java.io.*;

public class ServerMain {
    private static final int PORT = 8888;

    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientSession clientSession = new ClientSession(clientSocket, clientManager);
                new Thread(clientSession).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

