package hexgame.core;

/**
 * The Game class manages the overall state and rules of the Hex game.
 * It provides methods to make moves, switch turns, and determine the winner.
 *
 * @invariant board != null && playerRed != null && playerBlue != null
 */

public class Game {
    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;
    private int previousMove;
    private boolean canSwap = true;


    /**
     * Constructor for the Game class.
     *
     * @precondition redPlayerName != null && bluePlayerName != null
     */
    public Game(String redPlayerName, String bluePlayerName) {
        this.board = new Board();
        this.playerRed = new Player(redPlayerName, 'R');
        this.playerBlue = new Player(bluePlayerName, 'B');
        this.currentPlayer = playerRed;  // Red starts
    }


    /**
     * swapPlayers method.
     *
     * @precondition board != null && playerRed != null && playerBlue != null
     * @postcondition true
     */
    private void swapPlayers() {
        if (currentPlayer == playerRed) {
            currentPlayer = playerBlue;
        } else {
            currentPlayer = playerRed;
        }
    }

    /**
     * makeMove method.
     * Return true or false depending if move is legal and has been made
     *
     * @precondition board != null && playerRed != null && playerBlue != null
     */
    public boolean makeMove(int move) {
        if (move == 81 && canSwap) {  // Check if the move is the special swap move and if a swap is allowed
            board.swapMove(previousMove);  // Swap the previous move
            canSwap = false;  // Set the flag to false after performing the swap
            swapPlayers();
            return true;  // Continue the game after the swap
        }

        if (board.isValidMove(move)) {
            board.placePiece(move, currentPlayer.getColor());
            previousMove = move;  // Store this move for potential swap in the next turn
            if (currentPlayer == playerBlue && canSwap) {  // If it's the second player's first move
                canSwap = false;  // Disallow further swaps after this move
            }
            swapPlayers();
            return true;
        }
        return false;
    }

    /**
     * isGameOver method.
     * Method to check if game is finished in the clientsession class
     * Swap players and back because its called after having made a move which automatically switches the players
     *
     * @precondition board != null && playerRed != null && playerBlue != null
     */
    public boolean isGameOver() {
        swapPlayers();
        boolean win = board.checkWin(currentPlayer.getColor());
        swapPlayers();
        return win;
    }

    /**
     * getCurrentPlayer method.
     * Returns currentplayer's name
     *
     * @precondition currentPlayer != null
     */
    public String getCurrentPlayer() {
        return currentPlayer.getName();
    }


    /**
     * getGameOverReason method.
     * Returns VICTORY or DRAW depending on if there are still moves possible
     *
     */
    public String getGameOverReason() {
        for (int i = 0; i < 81; i++) {
            if (board.isValidMove(i)){
                return "VICTORY";
            }
        }
        return "DRAW";
    }
}
