package hexgame.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Board {
    private final int size = 9;
    private char[][] board;
    public final char EMPTY = 'E';
    public final char RED = 'R';
    public final char BLUE = 'B';
    private boolean[][] visited;
    private int previousMove;
    private int numRows, numCols;

    public Board() {
        board = new char[size][size];
        visited = new boolean[size][size];
        initializeBoard();
    }

    private void initializeBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = EMPTY;
                visited[i][j] = false;
            }
        }
    }

    public boolean isValidMove(int move) {
        int row = move / size;
        int col = move % size;
        return row >= 0 && row < size && col >= 0 && col < size && board[row][col] == EMPTY;
    }

    public boolean placePiece(int move, char playercol) {
        int row = move / size;
        int col = move % size;
        if (isValidMove(move)) {
            board[row][col] = playercol;
            previousMove = move;
            return true;
        }
        return false;
    }

    private boolean dfs(int x, int y, char player) {
        if (x < 0 || x >= size || y < 0 || y >= size || visited[x][y] || board[x][y] != player) {
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
                if (board[0][i] == RED && dfs(0, i, RED)) {
                    return true;
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                if (board[i][0] == BLUE && dfs(i, 0, BLUE)) {
                    return true;
                }
            }
        }

        return false;
    }

    public char[][] getCurrentState() {
        return board.clone();
    }

    public Board cloneBoard() throws CloneNotSupportedException {
        return (Board) this.clone();
    }

    public void swapMove(int move) {
        int row = move / size;
        int col = move % size;
        if (board[row][col] == RED) {
            board[row][col] = EMPTY;  // Set the given move position to EMPTY
            board[col][row] = BLUE;   // Set the mirrored position to BLUE
        }
    }

    public int getPreviousMove(){
        return previousMove;
    }


    // Helper function to check if a cell is within board and has the player's color
    private boolean isValid(int x, int y, char color) {
        return x >= 0 && y >= 0 && x < numRows && y < numCols && board[x][y] == color;
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

    // Method to find the longest path for a given player
    public int findLongestPath(char color) {
        int longestPath = 0;
        Set<String> visited = new HashSet<>();

        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
                if (board[i][j] == color) {
                    visited.clear();
                    longestPath = Math.max(longestPath, dfs(i, j, color, visited));
                }
            }
        }

        return longestPath;
    }

    // Method to count the number of pieces for a given player
    public int countPieces(Player player) {
        int count = 0;
        for (int row = 0; row < board.length; row++) {
            for (int col = 0; col < board[0].length; col++) {
                if (board[row][col] == player.getColor()) {
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
