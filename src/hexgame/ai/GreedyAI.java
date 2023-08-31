package hexgame.ai;

import hexgame.core.Board;
import hexgame.core.Player;

import java.util.List;

public class GreedyAI extends AI {

    public GreedyAI(Board board, Player player) {
        super(board, player);
    }

    @Override
    public int getNextMove() {
        List<Integer> availableMoves = board.getAvailableMoves();
        int bestMove = -1;
        int maxGain = Integer.MIN_VALUE;

        for (int move : availableMoves) {
            Board cloneBoard = null;
            try {
                cloneBoard = board.cloneBoard();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            cloneBoard.placePiece(move, player.getColor());

            int gain = evaluateBoard(cloneBoard);

            if (gain > maxGain) {
                maxGain = gain;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int evaluateBoard(Board board) {
        // Evaluate the board based on the strength of the longest path
        // and the number of pieces the player has on the board.

        int longestpathred = board.findLongestPath(board.RED);
        int longestpathblue = board.findLongestPath(board.BLUE);

        int myLongestPath;
        int opponentLongestPath;

        if (player.getColor() == board.RED) {
            myLongestPath = longestpathred;
            opponentLongestPath = longestpathblue;
        } else {
            myLongestPath = longestpathblue;
            opponentLongestPath = longestpathred;
        }

        int myPieces = board.countPieces(player);

        // For this example, we'll just sum these up, but you can apply
        // any formula you find suitable.
        return myLongestPath * 2 + myPieces - opponentLongestPath;
    }
}
