package hexgame.ai;

import hexgame.core.Board;
import hexgame.core.Player;


// Abstract AI Class
/**
 * The AI class serves as an abstract base class for implementing different AI strategies.
 * It provides common functionalities and data members needed by subclasses.
 */
/**
 * class method.
 */
public abstract class AI {

    protected Board board;

    /**
     * Constructor
     */
    public AI(Board board) {
        this.board = board;
    }

    /**
     * method to override
     */
    public abstract int getNextMove();
}