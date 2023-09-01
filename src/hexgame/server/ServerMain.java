package hexgame.server;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ServerMain {
    private static int PORT;

    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();
        Scanner input = new Scanner(System.in);

        System.out.println("What PORT would you like to use ?");
        PORT = input.nextInt();


        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientSession clientSession = new ClientSession(clientSocket, clientManager);
                new Thread(clientSession).start();
            }
        } catch (IOException e) {
            System.out.println("Invalid Port");
        }
    }
}

