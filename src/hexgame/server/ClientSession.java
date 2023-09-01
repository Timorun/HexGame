package hexgame.server;

import hexgame.core.Game;

import java.io.*;
import java.net.*;
import java.util.List;

/**
 * Represents a client session in the Hex game server.
 * Handles client commands and communicates with the ClientManager.
 */
public class ClientSession implements Runnable {

    private Socket clientSocket;
    private ClientManager clientManager;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Game currentGame;
    private boolean isConnected;

    /**
     * Constructs a new ClientSession.
     *
     * @param clientSocket The socket associated with the client.
     * @param manager      The ClientManager managing this session.
     */
    public ClientSession(Socket clientSocket, ClientManager manager) {
        this.clientSocket = clientSocket;
        this.clientManager = manager;
        this.isConnected = true;
    }

    /**
     * The main run loop for handling client commands.
     */
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
            System.out.println("Client Disconnected");
            if (currentGame != null) {
                ClientSession opclient = clientManager.getOpponent(currentGame,this);
                opclient.sendMessage("GAMEOVER~" + "DISCONNECT" + "~" + opclient.getUsername());
                clientManager.endGame(currentGame);
                currentGame = null;
            }
            clientManager.removeClient(this);
        }
    }

    /**
     * Handles the HELLO command from the client.
     *
     * @param parts The command parts.
     */
    private void handleHello(String[] parts) {
        sendMessage("HELLO~Server by Timo&GPT");
    }

    /**
     * Handles the LOGIN command from the client.
     *
     * @param parts The command parts.
     */
    private void handleLogin(String[] parts) {
        if (parts.length == 1) {
            sendError("Unknown command");
        } else if (clientManager.registerClient(this, parts[1])) {
            sendMessage("LOGIN");
        } else {
            sendMessage("ALREADYLOGGEDIN");
        }
    }

    /**
     * Handles the LIST command from the client.
     */
    private void handleList() {
        List<String> clients = clientManager.listClients();
        sendMessage("LIST~" + String.join("~", clients));
    }

    /**
     * Handles the QUEUE command from the client.
     */
    private void handleQueue() {
        clientManager.addToQueue(this);
    }

    /**
     * Handles the MOVE command from the client.
     *
     * @param tokens The command tokens.
     */
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
            if (currentGame.makeMove(move)) {

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

    /**
     * Sends an error message to the client.
     *
     * @param description The error description.
     */
    private void sendError(String description) {
        out.println("ERROR~" + description);
    }

    /**
     * Sets the current game for this client session.
     *
     * @param game The game to set.
     */
    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Gets the username associated with this client session.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for this client session.
     *
     * @param username The username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
