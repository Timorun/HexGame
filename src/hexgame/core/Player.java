package hexgame.core;

/**
 * The Player class represents a player in the Hex game.
 * Contains information about the player's name and color
 *
 * @invariant name != null && (color == 'R' || color == 'B')
 */
public class Player {
    private String name;
    private char color;
    private boolean isTurn;

    /**
     * Constructor for the Player class.
     *
     * @param name  The name of the player.
     * @param color The color of the player ('R' for red, 'B' for blue).
     * @precondition name != null && (color == 'R' || color == 'B')
     * @postcondition this.name == name && this.color == color && !this.isTurn
     */
    public Player(String name, char color) {
        this.name = name;
        this.color = color;
        this.isTurn = false;
    }

    /**
     * Get the name of the player.
     *
     * @return The name of the player.
     * @postcondition result != null
     */
    public String getName() {
        return name;
    }

    /**
     * Get the color of the player.
     *
     * @return The color of the player ('R' for red, 'B' for blue).
     * @postcondition result == 'R' || result == 'B'
     */
    public char getColor() {
        return color;
    }
}