package org.example.sudoku.sudoku;

public class BacktrackingSolver {

    private final SolveListener listener;
    private long steps;

    public BacktrackingSolver() {
        this(null);
    }

    public BacktrackingSolver(SolveListener listener) {
        this.listener = listener;
    }

    public boolean solve(SudokuBoard board) {
        steps = 0;
        return solveRecursive(board);
    }

    public long getSteps() {
        return steps;
    }

    private boolean solveRecursive(SudokuBoard board) {
        int[] emptyCell = findEmptyCell(board);

        // Sin celdas vacías: el tablero está completo por construcción.
        if (emptyCell == null) {
            return true;
        }

        int row = emptyCell[0];
        int col = emptyCell[1];

        for (int value = 1; value <= SudokuBoard.SIZE; value++) {
            if (board.isValidPlacement(row, col, value)) {
                board.set(row, col, value);
                steps++;

                if (listener != null) {
                    listener.onPlace(row, col, value);
                }

                if (solveRecursive(board)) {
                    return true;
                }

                board.set(row, col, SudokuBoard.EMPTY);

                if (listener != null) {
                    listener.onUnplace(row, col);
                }
            }
        }

        return false;
    }

    private int[] findEmptyCell(SudokuBoard board) {
        for (int row = 0; row < SudokuBoard.SIZE; row++) {
            for (int col = 0; col < SudokuBoard.SIZE; col++) {
                if (board.isEmpty(row, col)) {
                    return new int[]{row, col};
                }
            }
        }

        return null;
    }
}
