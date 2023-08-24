package hexgame.ai;

import java.util.Random;

public class RandomAI extends AI{
    private Board board;
    private char color;

    public RandomAI(Board board, char color) {
        this.board = board;
        this.color = color;
    }

    public int getMove() {
        Random random = new Random();
        int move;
        do {
            move = random.nextInt(81);  // As there are 81 possible positions on a 9x9 board
        } while (!board.isValidMove(move));
        return move;
    }
}