package hexgame;
import hexgame.core.*;

public class Main {
    public static void main(String[] args) {
        Player player1 = new Player("Alice", Board.Cell.RED);
        Player player2 = new Player("Bob", Board.Cell.BLUE);
        GameEngine game = new GameEngine(11, player1, player2);

        // Sample moves for testing
        game.makeMove(5, 5);
        game.makeMove(5, 6);
        // Add more moves and logic to simulate a game session

        // In a complete version, you'd integrate this with a UI and handle player input.
    }
}