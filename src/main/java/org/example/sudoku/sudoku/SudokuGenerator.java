package org.example.sudoku.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Genera puzzles de Sudoku con solución garantizada. La estrategia es:
 *   1) Construir un tablero completamente resuelto mediante backtracking
 *      con orden de candidatos aleatorio en cada celda.
 *   2) Borrar un número de celdas escogidas al azar para crear el puzzle.
 *
 * El resultado es siempre resoluble (existía una solución antes de borrar
 * celdas). No se garantiza unicidad de la solución.
 */
public final class SudokuGenerator {

    private static final int SIZE = SudokuBoard.SIZE;

    private final Random rng;

    /** Generador con semilla aleatoria. */
    public SudokuGenerator() {
        this(new Random());
    }

    /**
     * Generador con RNG inyectado. Útil para tests deterministas:
     * pasar un {@code new Random(semilla)} reproduce el mismo puzzle.
     */
    public SudokuGenerator(Random rng) {
        this.rng = rng;
    }

    /**
     * Devuelve una matriz 9x9 con un puzzle parcial y solucionable.
     * {@code cluesToKeep} es el número aproximado de pistas que quedan
     * después de borrar (entre 17 y 81). Valores bajos producen puzzles
     * más difíciles; usar 30–40 para dificultad media.
     */
    public int[][] generate(int cluesToKeep) {
        int[][] grid = new int[SIZE][SIZE];
        fillRandomly(grid);

        int target = Math.max(17, Math.min(81, cluesToKeep));
        int toRemove = SIZE * SIZE - target;

        List<int[]> positions = new ArrayList<>(SIZE * SIZE);
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                positions.add(new int[]{r, c});
            }
        }
        Collections.shuffle(positions, rng);

        for (int i = 0; i < toRemove && i < positions.size(); i++) {
            int[] p = positions.get(i);
            grid[p[0]][p[1]] = SudokuBoard.EMPTY;
        }
        return grid;
    }

    /**
     * Backtracking idéntico al de {@link BacktrackingSolver} pero con el
     * orden de candidatos barajado en cada celda — así cada ejecución
     * produce una solución distinta en lugar de la misma canónica.
     */
    private boolean fillRandomly(int[][] grid) {
        int[] empty = findEmpty(grid);
        if (empty == null) return true;

        int row = empty[0];
        int col = empty[1];

        List<Integer> candidates = new ArrayList<>(SIZE);
        for (int v = 1; v <= SIZE; v++) candidates.add(v);
        Collections.shuffle(candidates, rng);

        for (int v : candidates) {
            if (isSafe(grid, row, col, v)) {
                grid[row][col] = v;
                if (fillRandomly(grid)) return true;
                grid[row][col] = SudokuBoard.EMPTY;
            }
        }
        return false;
    }

    /** Primera celda en {@link SudokuBoard#EMPTY}, o {@code null} si no hay. */
    private int[] findEmpty(int[][] grid) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (grid[r][c] == SudokuBoard.EMPTY) return new int[]{r, c};
            }
        }
        return null;
    }

    /**
     * Equivalente local a {@link SudokuBoard#isValidPlacement} sobre el
     * {@code int[][]} crudo, para evitar el overhead de envolver el grid
     * en un {@link SudokuBoard} durante la generación.
     */
    private boolean isSafe(int[][] grid, int row, int col, int v) {
        for (int i = 0; i < SIZE; i++) {
            if (grid[row][i] == v) return false;
            if (grid[i][col] == v) return false;
        }
        int boxR = (row / 3) * 3;
        int boxC = (col / 3) * 3;
        for (int r = boxR; r < boxR + 3; r++) {
            for (int c = boxC; c < boxC + 3; c++) {
                if (grid[r][c] == v) return false;
            }
        }
        return true;
    }
}
