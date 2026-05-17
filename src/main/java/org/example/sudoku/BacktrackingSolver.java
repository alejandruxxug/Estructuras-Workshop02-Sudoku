package org.example.sudoku;

public class BacktrackingSolver {

    private long steps;

    public boolean solve(SudokuBoard board) {
        steps = 0;
        return solveRecursive(board);
    }

    public long getSteps() {
        return steps;
    }

    private boolean solveRecursive(SudokuBoard board) {
        int[] emptyCell = findEmptyCell(board);

        if (emptyCell == null) {
            return board.isComplete();
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int value = 1; value <= 9; value++) {
            if (board.isValidPlacement(row, col, value)) {
                board.set(row, col, value);
                steps++;

                if (solveRecursive(board)) {
                    return true;
                }

                board.set(row, col, 0);
            }
        }

        return false;
    }

    private int[] findEmptyCell(SudokuBoard board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.isEmpty(row, col)) {
                    return new int[]{row, col};
                }
            }
        }

        return null;
    }
}