package hexgame.ai;

import hexgame.core.Board;
import hexgame.core.Player;

import java.util.List;
import java.util.Random;

/**
 * The RandomAI class implements a random strategy to make moves in the Hex game.
 * It extends the abstract AI class and overrides its methods.
 */

public class RandomAI extends AI {

    private Random random;

    /**
     * Constructor for the RandomAI class.
     */
    public RandomAI(Board board) {
        super(board);
        this.random = new Random();
    }

    @Override
    /**
     * getNextMove randomly from available moves.
     */
    public int getNextMove() {
        List<Integer> availableMoves = board.getAvailableMoves();
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }
}