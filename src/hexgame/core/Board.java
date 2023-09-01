package hexgame.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.copyOf;

public class Board {
    private final int size = 9;
    private char[][] fields;
    public final char EMPTY = 'E';
    public final char RED = 'R';
    public final char BLUE = 'B';
    private boolean[][] visited;

    public Board() {
        fields = new char[size][size];
        visited = new boolean[size][size];
        initializeBoard();
    }

    // For use in AI to clone board
    public Board(char[][] clonefields, boolean[][] clonevisited) {
        fields = clonefields;
        visited = clonevisited;
    }



    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                fields[i][j] = EMPTY;
                visited[i][j] = false;
            }
        }
    }

    public boolean isValidMove(int move) {
        int row = move / size;
        int col = move % size;
        return row >= 0 && row < size && col >= 0 && col < size && fields[row][col] == EMPTY;
    }

    public boolean placePiece(int move, char playercol) {
        int row = move / size;
        int col = move % size;
        if (isValidMove(move)) {
            fields[row][col] = playercol;
            return true;
        }
        return false;
    }

    private boolean dfs(int x, int y, char player) {
        if (x < 0 || x >= size || y < 0 || y >= size || visited[x][y] || fields[x][y] != player) {
            return false;
        }

        if ((player == RED && x == size - 1) || (player == BLUE && y == size - 1)) {
            return true;
        }

        visited[x][y] = true;

        // Explore neighboring cells
        return dfs(x + 1, y, player) || dfs(x - 1, y, player) ||
                dfs(x, y + 1, player) || dfs(x, y - 1, player) ||
                dfs(x + 1, y - 1, player) || dfs(x - 1, y + 1, player);
    }

    public boolean checkWin(char player) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                visited[i][j] = false;
            }
        }

        if (player == RED) {
            for (int i = 0; i < size; i++) {
                if (fields[0][i] == RED && dfs(0, i, RED)) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (fields[i][0] == BLUE && dfs(i, 0, BLUE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public char[][] getCurrentFields() {
        char[][] fieldscopy = new char[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                fieldscopy[i][j] = fields[i][j];
            }
        }
        return fieldscopy;
    }

    public boolean[][] getVisited() {
        boolean[][] visitedcopy = new boolean[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                visitedcopy[i][j] = visited[i][j];
            }
        }
        return visitedcopy;
    }

    public void swapMove(int move) {
        int row = move / size;
        int col = move % size;
        if (fields[row][col] == RED) {
            fields[row][col] = EMPTY;  // Set the given move position to EMPTY
            fields[col][row] = BLUE;   // Set the mirrored position to BLUE
        }
    }

    // Helper function to check if a cell is within board and has the player's color
    private boolean isValid(int x, int y, char color) {
        return x >= 0 && y >= 0 && x < 9 && y < 9 && fields[x][y] == color;
    }

    // DFS function to find the longest path starting from (x, y)
    private int dfs(int x, int y, char color, Set<String> visited) {
        String key = x + "," + y;
        if (visited.contains(key)) {
            return 0;
        }
        visited.add(key);

        int maxLength = 1;  // Include the current cell

        // Define possible moves (hexagonal grid neighbors)
        int[][] moves = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}, {1, -1}, {-1, 1}};

        // Explore all possible moves
        for (int[] move : moves) {
            int newX = x + move[0];
            int newY = y + move[1];
            if (isValid(newX, newY, color)) {
                maxLength = Math.max(maxLength, 1 + dfs(newX, newY, color, visited));
            }
        }

        return maxLength;
    }

    // Method to find the longest path for a given color
    public int findLongestPath(char color) {
        int longestPath = 0;
        Set<String> visited = new HashSet<>();

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (fields[i][j] == color) {
                    visited.clear();
                    longestPath = Math.max(longestPath, dfs(i, j, color, visited));
                }
            }
        }

        return longestPath;
    }

    // Method to count the number of pieces for a color
    public int countPieces(char color) {
        int count = 0;
        for (int row = 0; row < fields.length; row++) {
            for (int col = 0; col < fields[0].length; col++) {
                if (fields[row][col] == color) {
                    count++;
                }
            }
        }
        return count;
    }

    public List<Integer> getAvailableMoves() {
        List<Integer> availableMoves = new ArrayList<>();
        int size = 9;
        for (int i = 0; i < 81; i++) {
            if (isValidMove(i)) {
                availableMoves.add(i);
            }
        }
        return availableMoves;
    }
}
