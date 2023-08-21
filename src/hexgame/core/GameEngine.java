package hexgame.core;

public class GameEngine {
    private final Board board;
    private final Player player1;
    private final Player player2;
    private Player currentPlayer;

    public GameEngine(int boardSize, Player p1, Player p2) {
        this.board = new Board(boardSize);
        this.player1 = p1;
        this.player2 = p2;
        this.currentPlayer = p1;  // Player1 starts
    }

    public boolean makeMove(int x, int y) {
        if (board.placePiece(x, y, currentPlayer.getColor())) {
            if (board.hasWinner()) {
                System.out.println(currentPlayer.getName() + " wins!");
                return true;  // Game over
            }
            switchTurn();
            return true;  // Move successful
        }
        return false;  // Invalid move
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == player1) ? player2 : player1;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
}
