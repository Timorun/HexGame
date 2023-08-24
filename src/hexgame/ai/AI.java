package hexgame.ai;

import hexgame.core.Board;

import java.util.ArrayList;
import java.util.List;

public abstract class AI {
    protected Board board;
    protected char color;

    public AI(Board board, char color) {
        this.board = board;
        this.color = color;
    }

    // This method would be overridden by each specific AI strategy
    public abstract int getMove();

    protected List<Integer> getAvailableMoves() {
        List<Integer> availableMoves = new ArrayList<>();
        int size = 9;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == Board.EMPTY) {
                    availableMoves.add(i * size + j);
                }
            }
        }
        return availableMoves;
    }
}