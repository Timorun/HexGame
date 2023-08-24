package hexgame.core;

public class Player {
    private String name;
    private char color;
    private boolean isTurn;

    public Player(String name, char color) {
        this.name = name;
        this.color = color;
        this.isTurn = false;
    }

    public String getName() {
        return name;
    }

    public char getColor() {
        return color;
    }

    public boolean isPlayerTurn() {
        return isTurn;
    }

    public void toggleTurn() {
        isTurn = !isTurn;
    }
}
