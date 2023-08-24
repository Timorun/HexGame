package hexgame.ai;

import java.util.List;

public class GreedyAI extends AI {

    public GreedyAI(Board board, char color) {
        super(board, color);
    }

    @Override
    public int getMove() {
        List<Integer> possibleMoves = board.getAvailableMoves();

        // First Move Strategy: If it's the first move of the game, choose the center.
        if (possibleMoves.size() == 81) {
            return 40;  // Center of a 9x9 board
        }

        // Swap Decision Logic: For the second player's first turn
        if (possibleMoves.size() == 80) {
            int normalScore = Integer.MIN_VALUE;
            int swapScore = Integer.MIN_VALUE;

            for (int move : possibleMoves) {
                int currentScore = evaluateMove(move);
                if (currentScore > normalScore) {
                    normalScore = currentScore;
                }
            }

            // Simulate the swap and evaluate its score
            board.swapMove(board.getPreviousMove());
            swapScore = evaluateBoard();
            board.swapMove(board.getPreviousMove());  // Revert the swap for further calculations

            if (swapScore > normalScore) {
                return 81;  // Return swap move
            }
        }

        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;
        for (int move : possibleMoves) {
            int score = evaluateMove(move);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private int evaluateMove(int move) {
        char opponentColor = (color == Board.RED) ? Board.BLUE : Board.RED;
        int score = 0;

        // Simulate the move
        board.placePiece(move, color);

        // Evaluate the board based on the simulated move
        score = evaluateBoard();

        // Revert the simulated move
        board.removePiece(move);

        return score;
    }

    private int evaluateBoard() {
        int score = 0;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (board.getCell(i, j) == color) {
                    score += 1;
                } else if (board.getCell(i, j) != Board.EMPTY) {
                    score -= 1;
                }
            }
        }
        return score;
    }
}
