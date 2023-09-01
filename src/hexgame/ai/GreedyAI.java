package hexgame.ai;

import hexgame.core.Board;

import java.util.List;

public class GreedyAI extends AI {

    char color;

    public GreedyAI(Board board, char mycolor) {
        super(board);
        this.color = mycolor;
    }

    @Override
    public int getNextMove() {
        List<Integer> availableMoves = board.getAvailableMoves();
        int bestMove = -1;
        int maxGain = -1;
        char[][] fields = board.getCurrentFields();
        boolean[][] visited = board.getVisited();
        Board cloneBoard;
        

        for (int move : availableMoves) {
            cloneBoard = new Board(fields, visited);
            cloneBoard.placePiece(move, color);

            int gain = evaluateBoard(cloneBoard);

            if (gain > maxGain) {
                maxGain = gain;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int evaluateBoard(Board cloneboard) {
        // Evaluate the board based on the strength of the longest path
        // and the number of pieces the player has on the board.

        int longestpathred = cloneboard.findLongestPath(cloneboard.RED);
        int longestpathblue = cloneboard.findLongestPath(cloneboard.BLUE);

        int myLongestPath;
        int opponentLongestPath;

        if (color == 'R') {
            myLongestPath = longestpathred;
            opponentLongestPath = longestpathblue;
        } else {
            myLongestPath = longestpathblue;
            opponentLongestPath = longestpathred;
        }

        int myPieces = cloneboard.countPieces(color);

        // For this example, we'll just sum these up, but you can apply
        // any formula you find suitable.
        return myLongestPath * 2 + myPieces - opponentLongestPath;
    }
}
