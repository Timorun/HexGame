package hexgame.core;

public class Board {
    private final int size = 9;
    private char[][] board;
    private final char EMPTY = 'E';
    private final char RED = 'R';
    private final char BLUE = 'B';
    private boolean[][] visited;

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

    public boolean placePiece(int move, char player) {
        int row = move / size;
        int col = move % size;
        if (isValidMove(move)) {
            board[row][col] = player;
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

    public void swapMove(int move) {
        int row = move / size;
        int col = move % size;
        if (board[row][col] == RED) {
            board[row][col] = BLUE;
            board[col][row] = EMPTY;
        }
    }
}
