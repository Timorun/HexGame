package hexgame.tests;

import static org.junit.jupiter.api.Assertions.*;

import hexgame.core.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {

    private Game game;

    @BeforeEach
    public void setUp() {
        game = new Game("Player1", "Player2");
    }

    @Test
    public void testGameInitialization() {
        assertEquals("Player1", game.getCurrentPlayer());
    }

    @Test
    public void testMakeMove() {
        assertTrue(game.makeMove(0));
        assertFalse(game.makeMove(0));
        assertEquals("Player2", game.getCurrentPlayer());
    }


    @Test
    public void testGetCurrentPlayer() {
        assertEquals("Player1", game.getCurrentPlayer());
        game.makeMove(0);
        assertEquals("Player2", game.getCurrentPlayer());
    }

    @Test
    public void testGameOverReason() {
        // In a new game, the reason should be "VICTORY" as moves are possible
        assertEquals("VICTORY", game.getGameOverReason());
    }
}
