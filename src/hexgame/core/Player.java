package hexgame.core;

public class Player {
    private final Board.Cell color;
    private final String name;

    public Player(String name, Board.Cell color) {
        this.name = name;
        this.color = color;
    }

    public Board.Cell getColor() {
        return color;
    }

    public String getName() {
        return name;
    }
}