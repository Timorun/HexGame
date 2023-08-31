package hexgame.ai;

import hexgame.core.Board;
import hexgame.core.Player;


// Abstract AI Class
public abstract class AI {
    protected Board board;
    protected Player player;

    public AI(Board board, Player player) {
        this.board = board;
        this.player = player;
    }

    public abstract int getNextMove();
}