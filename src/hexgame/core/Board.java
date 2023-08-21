package hexgame.core;

public class Board {
    private final int size;
    private final Cell[][] board;

    public enum Cell {
        EMPTY, RED, BLUE
    }

    public Board(int size) {
        this.size = size;
        this.board = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = Cell.EMPTY;
            }
        }
    }

    public boolean placePiece(int x, int y, Cell player) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return false;
        }
        if (board[x][y] != Cell.EMPTY) {
            return false;
        }
        board[x][y] = player;
        return true;
    }

    public Cell getCell(int x, int y) {
        if (x < 0 || x >= size || y < 0 || y >= size) {
            return null;
        }
        return board[x][y];
    }

    public boolean hasWinner() {
        for (int i = 0; i < size; i++) {
            // Check from top to bottom for BLUE
            if (dfs(i, 0, Cell.BLUE, new boolean[size][size])) {
                return true;
            }
            // Check from left to right for RED
            if (dfs(0, i, Cell.RED, new boolean[size][size])) {
                return true;
            }
        }
        return false;
    }

    private boolean dfs(int x, int y, Cell player, boolean[][] visited) {
        if (x < 0 || x >= size || y < 0 || y >= size || visited[x][y] || board[x][y] != player) {
            return false;
        }

        // If BLUE reaches the bottom or RED reaches the right, there's a winner
        if ((player == Cell.BLUE && y == size - 1) || (player == Cell.RED && x == size - 1)) {
            return true;
        }

        visited[x][y] = true;

        // Visit neighboring cells
        int[][] dirs = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}, {-1, 1}, {1, -1}};
        for (int[] dir : dirs) {
            int newX = x + dir[0];
            int newY = y + dir[1];
            if (dfs(newX, newY, player, visited)) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        Board board = new Board(11);
        board.placePiece(5, 5, Cell.RED);
        System.out.println(board.getCell(5, 5));
        // Add more test cases to check the functionality of hasWinner()
    }
}