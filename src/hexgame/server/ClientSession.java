package hexgame.server;

import hexgame.core.Game;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientSession implements Runnable {

    private Socket clientSocket;
    private ClientManager clientManager;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Game currentGame;

    public ClientSession(Socket clientSocket, ClientManager manager) {
        this.clientSocket = clientSocket;
        this.clientManager  = manager;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            String input;
            while ((input = in.readLine()) != null) {
                String[] tokens = input.split("~");
                String command = tokens[0];

                switch (command) {
                    case "HELLO":
                        handleHello(tokens);
                        break;
                    case "LOGIN":
                        handleLogin(tokens);
                        break;
                    case "LIST":
                        handleList();
                        break;
                    case "QUEUE":
                        handleQueue();
                        break;
                    case "MOVE":
                        handleMove(tokens);
                        break;
                    // ... handle other commands
                    default:
                        sendError("Unknown command");
                        break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleHello(String[] parts) {
        sendMessage("HELLO~Server by Assistant~CHAT~RANK");
    }

    private void handleLogin(String[] parts) {
        this.username = parts[1];
        if (clientManager.registerClient(this, username)) {
            sendMessage("LOGIN");
        } else {
            sendMessage("ALREADYLOGGEDIN");
        }
    }

    private void handleList() {
        List<String> clients = clientManager.listClients();
        sendMessage("LIST~" + String.join("~", clients));
    }

    private void handleQueue() {
        clientManager.addToQueue(this);
    }
    
    

    private void handleMove(String[] tokens) {
        if (currentGame == null) {
            sendError("No active game");
            return;
        }

        int move;
        try {
            move = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            sendError("Invalid move format");
            return;
        }

        if (currentGame.getCurrentPlayer().equals(username)) {
            if (currentGame.isValidMove(move)) {
                currentGame.makeMove(move);

                if (currentGame.isGameOver()) {
                    clientManager.broadcastToGame(currentGame, "GAMEOVER~" + currentGame.getGameOverReason() + "~" + username);
                    clientManager.endGame(currentGame);
                    currentGame = null;
                } else {
                    clientManager.broadcastToGame(currentGame, "MOVE~" + move);
                }
            } else {
                sendError("Illegal move");
            }
        } else {
            sendError("Not your turn");
        }
    }

    private void sendError(String description) {
        out.println("ERROR~" + description);
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}