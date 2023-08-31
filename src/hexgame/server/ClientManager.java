package hexgame.server;

import hexgame.core.Game;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {

    private Set<ClientSession> clients;
    private Queue<ClientSession> waitingQueue;
    private Map<Game, Pair<ClientSession, ClientSession>> activeGames;

    public ClientManager() {
        clients = Collections.newSetFromMap(new ConcurrentHashMap<>());
        waitingQueue = new LinkedList<>();
        activeGames = new HashMap<>();
    }

    public void addClient(ClientSession client) {
        clients.add(client);
    }

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

        System.out.print("New client added with username: "+ username);

        client.setUsername(username);
        addClient(client);
        return true;
    }

    public void removeClient(ClientSession client) {
        clients.remove(client);
    }

    public void addToQueue(ClientSession client) {
        waitingQueue.add(client);
        checkQueueAndStartGame();
    }

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

    public void broadcastToGame(Game game, String message) {
        Pair<ClientSession, ClientSession> players = activeGames.get(game);
        players.first().sendMessage(message);
        players.second().sendMessage(message);
    }

    public void endGame(Game game) {
        activeGames.remove(game);
    }

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

    public synchronized List<String> listClients() {
        ArrayList list = new ArrayList<>();
        for (ClientSession c : clients) {
            list.add(c.getUsername() + "*");
        }
        return list;
    }

}
