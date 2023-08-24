package hexgame.core;

public class Game {
    private Board board;
    private Player playerRed;
    private Player playerBlue;
    private Player currentPlayer;

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
        if (board.isValidMove(move)) {
            board.placePiece(move, currentPlayer.getColor());
            if (checkWin()) {
                System.out.println(currentPlayer.getName() + " wins!");
                return true;
            }
            swapPlayers();
        } else {
            System.out.println("Invalid move. Try again.");
        }
        return false;
    }

    public boolean checkWin() {
        return board.checkWin(currentPlayer.getColor());
    }

    public void swapRule(int move) {
        board.swapMove(move);
        swapPlayers();
    }

    public void printBoard() {
        char[][] boardState = board.getCurrentState();
        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState[i].length; j++) {
                System.out.print(boardState[i][j] + " ");
            }
            System.out.println();
        }
    }
}
