package hexgame.tests;

import static org.junit.jupiter.api.Assertions.*;

import hexgame.core.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlayerTest {

    private Player redPlayer;
    private Player bluePlayer;

    @BeforeEach
    public void setUp() {
        redPlayer = new Player("RedPlayer", 'R');
        bluePlayer = new Player("BluePlayer", 'B');
    }

    @Test
    public void testPlayerInitialization() {
        assertEquals("RedPlayer", redPlayer.getName());
        assertEquals('R', redPlayer.getColor());
        assertEquals("BluePlayer", bluePlayer.getName());
        assertEquals('B', bluePlayer.getColor());
    }

    @Test
    public void testGetName() {
        assertEquals("RedPlayer", redPlayer.getName());
        assertEquals("BluePlayer", bluePlayer.getName());
    }

    @Test
    public void testGetColor() {
        assertEquals('R', redPlayer.getColor());
        assertEquals('B', bluePlayer.getColor());
    }
}
