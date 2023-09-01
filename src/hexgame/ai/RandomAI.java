package hexgame.ai;

import hexgame.core.Board;
import hexgame.core.Player;

import java.util.List;
import java.util.Random;

public class RandomAI extends AI {
    private Random random;

    public RandomAI(Board board) {
        super(board);
        this.random = new Random();
    }

    @Override
    public int getNextMove() {
        List<Integer> availableMoves = board.getAvailableMoves();
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }
}