package hexgame.core;

public class Game {
    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;
    private int previousMove;
    private boolean canSwap = true;

    public Game(String redPlayerName, String bluePlayerName) {
        this.board = new Board();
        this.playerRed = new Player(redPlayerName, 'R');
        this.playerBlue = new Player(bluePlayerName, 'B');
        this.currentPlayer = playerRed;  // Red starts
    }

    public void initializeGame() {
        board = new Board();
        currentPlayer = playerRed;
    }

    private void swapPlayers() {
        if (currentPlayer == playerRed) {
            currentPlayer = playerBlue;
        } else {
            currentPlayer = playerRed;
        }
    }

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

    public boolean isGameOver() {
        swapPlayers();
        boolean win = board.checkWin(currentPlayer.getColor());
        swapPlayers();
        return win;
    }

    public void printBoard() {
        char[][] boardState = board.getCurrentFields();
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                System.out.print(boardState[i][j] + " ");
            }
            System.out.println();
        }
    }

    public String getCurrentPlayer() {
        return currentPlayer.getName();
    }


    public String getGameOverReason() {
        //need to handle DC
        for (int i = 0; i < 81; i++) {
            if (board.isValidMove(i)){
                return "VICTORY";
            }
        }
        return "DRAW";
    }
}
