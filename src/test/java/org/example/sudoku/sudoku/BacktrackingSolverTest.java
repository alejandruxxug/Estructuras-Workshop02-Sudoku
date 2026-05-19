package org.example.sudoku.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BacktrackingSolverTest {

    @Test
    void solveEmptyBoardReturnsTrueAndProducesValidSudoku() {
        SudokuBoard board = new SudokuBoard();
        BacktrackingSolver solver = new BacktrackingSolver();

        boolean solved = solver.solve(board);

        assertTrue(solved);
        assertValidSudoku(board);
    }

    @Test
    void solveEasySudokuReturnsTruePreservesInitialValuesAndMatchesExpectedSolution() {
        int[][] puzzle = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        int[][] expected = {
                {5, 3, 4, 6, 7, 8, 9, 1, 2},
                {6, 7, 2, 1, 9, 5, 3, 4, 8},
                {1, 9, 8, 3, 4, 2, 5, 6, 7},
                {8, 5, 9, 7, 6, 1, 4, 2, 3},
                {4, 2, 6, 8, 5, 3, 7, 9, 1},
                {7, 1, 3, 9, 2, 4, 8, 5, 6},
                {9, 6, 1, 5, 3, 7, 2, 8, 4},
                {2, 8, 7, 4, 1, 9, 6, 3, 5},
                {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };

        SudokuBoard board = createBoard(puzzle);
        BacktrackingSolver solver = new BacktrackingSolver();

        boolean solved = solver.solve(board);

        assertTrue(solved);
        assertInitialValuesPreserved(puzzle, board);
        assertBoardEquals(expected, board);
    }

    @Test
    void solveUnsolvableBoardReturnsFalse() {
        int[][] puzzle = {
                {5, 5, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        SudokuBoard board = createBoard(puzzle);
        BacktrackingSolver solver = new BacktrackingSolver();

        boolean solved = solver.solve(board);

        assertFalse(solved);
    }

    @Test
    void getStepsIsGreaterThanZeroAfterSolvingNonTrivialBoard() {
        int[][] puzzle = {
                {5, 3, 0, 0, 7, 0, 0, 0, 0},
                {6, 0, 0, 1, 9, 5, 0, 0, 0},
                {0, 9, 8, 0, 0, 0, 0, 6, 0},
                {8, 0, 0, 0, 6, 0, 0, 0, 3},
                {4, 0, 0, 8, 0, 3, 0, 0, 1},
                {7, 0, 0, 0, 2, 0, 0, 0, 6},
                {0, 6, 0, 0, 0, 0, 2, 8, 0},
                {0, 0, 0, 4, 1, 9, 0, 0, 5},
                {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };

        SudokuBoard board = createBoard(puzzle);
        BacktrackingSolver solver = new BacktrackingSolver();

        solver.solve(board);

        assertTrue(solver.getSteps() > 0);
    }

    private SudokuBoard createBoard(int[][] values) {
        SudokuBoard board = new SudokuBoard();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board.set(row, col, values[row][col]);
            }
        }

        return board;
    }

    private void assertInitialValuesPreserved(int[][] original, SudokuBoard board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (original[row][col] != 0) {
                    assertEquals(original[row][col], board.get(row, col));
                }
            }
        }
    }

    private void assertBoardEquals(int[][] expected, SudokuBoard board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                assertEquals(expected[row][col], board.get(row, col));
            }
        }
    }

    private void assertValidSudoku(SudokuBoard board) {
        for (int row = 0; row < 9; row++) {
            assertValidGroup(getRow(board, row));
        }

        for (int col = 0; col < 9; col++) {
            assertValidGroup(getColumn(board, col));
        }

        for (int startRow = 0; startRow < 9; startRow += 3) {
            for (int startCol = 0; startCol < 9; startCol += 3) {
                assertValidGroup(getBox(board, startRow, startCol));
            }
        }
    }

    private int[] getRow(SudokuBoard board, int row) {
        int[] values = new int[9];

        for (int col = 0; col < 9; col++) {
            values[col] = board.get(row, col);
        }

        return values;
    }

    private int[] getColumn(SudokuBoard board, int col) {
        int[] values = new int[9];

        for (int row = 0; row < 9; row++) {
            values[row] = board.get(row, col);
        }

        return values;
    }

    private int[] getBox(SudokuBoard board, int startRow, int startCol) {
        int[] values = new int[9];
        int index = 0;

        for (int row = startRow; row < startRow + 3; row++) {
            for (int col = startCol; col < startCol + 3; col++) {
                values[index] = board.get(row, col);
                index++;
            }
        }

        return values;
    }

    private void assertValidGroup(int[] values) {
        boolean[] seen = new boolean[10];

        for (int value : values) {
            assertTrue(value >= 1 && value <= 9);
            assertFalse(seen[value]);
            seen[value] = true;
        }
    }
}