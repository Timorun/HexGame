package hexgame.network;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 8080;
    private Socket socket;

    public Client() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        // TODO: Implement communication with the server, game UI, etc.
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
