package hexgame.tests;

import static org.junit.jupiter.api.Assertions.*;

import hexgame.core.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class BoardTest {

    private Board board;

    @BeforeEach
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testBoardInitialization() {
        char[][] fields = board.getCurrentFields();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals('E', fields[i][j]);
            }
        }
    }

    @Test
    public void testValidMove() {
        assertTrue(board.isValidMove(0));
        assertFalse(board.isValidMove(81));
        assertFalse(board.isValidMove(-1));
    }

    @Test
    public void testPiecePlacement() {
        assertTrue(board.placePiece(0, 'R'));
        assertFalse(board.placePiece(0, 'R'));
        assertFalse(board.placePiece(81, 'B'));
    }

    @Test
    public void testSwapMove() {
        board.placePiece(8, 'R');
        board.swapMove(8);
        char[][] fields = board.getCurrentFields();
        assertEquals('B', fields[8][0]);
        assertEquals('E', fields[0][8]);
    }

    @Test
    public void testCountPieces() {
        board.placePiece(0, 'R');
        board.placePiece(1, 'B');
        assertEquals(1, board.countPieces('R'));
        assertEquals(1, board.countPieces('B'));
    }

    @Test
    public void testAvailableMoves() {
        List<Integer> moves = board.getAvailableMoves();
        assertEquals(81, moves.size());
        board.placePiece(0, 'R');
        moves = board.getAvailableMoves();
        assertEquals(80, moves.size());
    }
}
