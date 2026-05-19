package org.example.sudoku;

public class SudokuBoard {
    public static final int SIZE = 9;
    public static final int BOX_SIZE = 3;
    public static final int EMPTY = 0;
    private int[][] grid;

    public SudokuBoard() {
        this.grid = new int[SIZE][SIZE];
        // Los valores int se inicializan con 0 por defecto
    }

    public SudokuBoard(int[][] initial) {
        if (initial == null || initial.length != SIZE) {
            throw new IllegalArgumentException(
                    "La matriz inicial debe ser 9x9, se recibió " +
                            (initial == null ? "null" : initial.length + "x?")
            );
        }

        for (int i = 0; i < SIZE; i++) {
            if (initial[i] == null || initial[i].length != SIZE) {
                throw new IllegalArgumentException(
                    "La fila " + i + " no tiene 9 columnas"
                );
            }

            for (int j = 0; j < SIZE; j++) {
                int value = initial[i][j];
                if (value < 0 || value > 9) {
                    throw new IllegalArgumentException(
                            "Valor inválido en posición (" + i + ", " + j + "): " + value +
                                    ". Los valores deben estar en [0, 9]"
                    );
                }
            }
        }

        // Copiar la matriz inicial
        this.grid = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                this.grid[i][j] = initial[i][j];
            }
        }
    }

    public int get(int row, int col) {
        validateIndices(row, col);
        return grid[row][col];
    }

    /**
     * Asigna {@code value} en la celda. No verifica reglas del Sudoku;
     * el llamador debe usar {@link #isValidPlacement} antes si requiere validar.
     * Esto es intencional para que el solver pueda colocar valores tentativos.
     */
    public void set(int row, int col, int value) {
        validateIndices(row, col);
        if (value < 0 || value > SIZE) {
            throw new IllegalArgumentException(
                    "El valor debe estar en [0, 9], se recibió: " + value
            );
        }
        grid[row][col] = value;
    }

    public boolean isEmpty(int row, int col) {
        validateIndices(row, col);
        return grid[row][col] == 0;
    }

    public boolean isValidPlacement(int row, int col, int value) {
        validateIndices(row, col);
        if (value < 0 || value > 9) {
            throw new IllegalArgumentException(
                    "El valor debe estar en [0, 9], se recibió: " + value
            );
        }

        // Si el valor es 0, siempre es válido (vaciar una celda)
        if (value == 0) {
            return true;
        }

        // Verificar en la fila
        for (int c = 0; c < SIZE; c++) {
            if (c != col && grid[row][c] == value) {
                return false;
            }
        }

        // Verificar en la columna
        for (int r = 0; r < SIZE; r++) {
            if (r != row && grid[r][col] == value) {
                return false;
            }
        }

        // Verificar en la subcuadrícula 3x3
        int boxRow = (row / BOX_SIZE) * BOX_SIZE;
        int boxCol = (col / BOX_SIZE) * BOX_SIZE;

        for (int r = boxRow; r < boxRow + BOX_SIZE; r++) {
            for (int c = boxCol; c < boxCol + BOX_SIZE; c++) {
                if ((r != row || c != col) && grid[r][c] == value) {
                    return false;
                }
            }
        }

        return true;
    }

    public int[][] snapshot() {
        int[][] copy = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                copy[i][j] = grid[i][j];
            }
        }
        return copy;
    }

    public boolean isComplete() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (grid[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verifica que el tablero actual no contenga conflictos entre los valores
     * ya colocados (fila/columna/subcuadrícula). Útil para validar la entrada
     * del usuario antes de invocar al solver: si retorna {@code false}, el
     * problema es la entrada, no que el puzzle sea irresoluble.
     */
    public boolean isInitialStateValid() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int v = grid[r][c];
                if (v != EMPTY && !isValidPlacement(r, c, v)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int boxIndex(int row, int col) {
        int boxRow = row / BOX_SIZE;
        int boxCol = col / BOX_SIZE;
        return boxRow * BOX_SIZE + boxCol;
    }

    private void validateIndices(int row, int col) {
        if (row < 0 || row >= SIZE || col < 0 || col >= SIZE) {
            throw new IllegalArgumentException(
                    "Índices fuera de rango: (" + row + ", " + col + "). " +
                            "Deben estar en [0, 8]"
            );
        }
    }
}

