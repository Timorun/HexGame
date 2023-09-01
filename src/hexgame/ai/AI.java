package hexgame.ai;

import hexgame.core.Board;
import hexgame.core.Player;


// Abstract AI Class
public abstract class AI {
    protected Board board;

    public AI(Board board) {
        this.board = board;
    }

    public abstract int getNextMove();
}