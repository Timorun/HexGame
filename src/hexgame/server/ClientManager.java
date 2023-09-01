package hexgame.server;

import hexgame.core.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages client sessions, game queues, and active games.
 */
public class ClientManager {

    private Set<ClientSession> clients;
    private Queue<ClientSession> waitingQueue;
    private Map<Game, Pair<ClientSession, ClientSession>> activeGames;

    /**
     * Initializes the ClientManager with empty sets and queues.
     */
    public ClientManager() {
        clients = Collections.newSetFromMap(new ConcurrentHashMap<>());
        waitingQueue = new LinkedList<>();
        activeGames = new HashMap<>();
    }

    /**
     * Adds a client to the set of managed clients.
     *
     * @param client The client session to add.
     */
    public void addClient(ClientSession client) {
        clients.add(client);
    }


    /**
     * Registers a client with a username.
     *
     * @param client   The client session to register.
     * @param username The username to register.
     * @return true if registration is successful, false otherwise.
     */
    public boolean registerClient(ClientSession client, String username) {
        // Check for duplicates or invalid names
        for (ClientSession c : clients) {
            if (c.getUsername().equals(username)) {
                return false; // Username already exists
            }
        }

        // Check for invalid characters
        if (username.contains("~") || username.contains("\\")) {
            return false; // Invalid username
        }

        System.out.println("New client added with username: "+ username);

        client.setUsername(username);
        addClient(client);
        return true;
    }


    /**
     * Removes a client from all managed collections.
     *
     * @param client The client session to remove.
     */
    public void removeClient(ClientSession client) {
        clients.remove(client);
        waitingQueue.remove(client);
    }

    /**
     * Adds a client to the game waiting queue.
     *
     * @param client The client session to add to the queue.
     */
    public void addToQueue(ClientSession client) {
        waitingQueue.add(client);
        checkQueueAndStartGame();
    }

    /**
     * Checks the queue and starts a new game if possible.
     */
    private void checkQueueAndStartGame() {
        if (waitingQueue.size() >= 2) {
            ClientSession player1 = waitingQueue.poll();
            ClientSession player2 = waitingQueue.poll();

            Game game = new Game(player1.getUsername(), player2.getUsername());

            activeGames.put(game, new Pair<>(player1, player2));

            player1.setCurrentGame(game);
            player2.setCurrentGame(game);

            broadcastToGame(game, "NEWGAME~" + player1.getUsername() + "~" + player2.getUsername());
        }
    }


    /**
     * Sends a message to both players in a game.
     *
     * @param game    The game to which the message should be sent.
     * @param message The message to send.
     */
    public void broadcastToGame(Game game, String message) {
        Pair<ClientSession, ClientSession> players = activeGames.get(game);
        try {
            players.first().sendMessage(message);
        }
        catch(Exception e) {
        }
        try {
            players.second().sendMessage(message);
        }
        catch(Exception e) {
        }
    }

    /**
     * Ends a game and removes it from the list of active games.
     *
     * @param game The game to end.
     */
    public void endGame(Game game) {
        activeGames.remove(game);
    }

    /**
     * Retrieves the opponent of a given client in a game.
     *
     * @param game          The game in which to find the opponent.
     * @param clientSession The client whose opponent is to be found.
     * @return The opponent client session.
     */
    public ClientSession getOpponent(Game game, ClientSession clientSession) {
        Pair<ClientSession, ClientSession> pair = activeGames.get(game);
        if (pair.first == clientSession) {
            return pair.second;
        } else {
            return pair.first;
        }
    }

    /**
     * A utility class to hold a pair of objects.
     *
     * @param <T> The type of the first object.
     * @param <U> The type of the second object.
     */
    private static class Pair<T, U> {
        private T first;
        private U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }

        public T first() {
            return first;
        }

        public U second() {
            return second;
        }
    }

    /**
     * Lists the usernames of all connected clients.
     *
     * @return A list of usernames.
     */
    public synchronized List<String> listClients() {
        ArrayList list = new ArrayList<>();
        for (ClientSession c : clients) {
            list.add(c.getUsername());
        }
        return list;
    }

}
