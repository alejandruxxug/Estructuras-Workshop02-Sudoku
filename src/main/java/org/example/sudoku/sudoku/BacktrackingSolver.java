package org.example.sudoku.sudoku;

/**
 * Resuelve un {@link SudokuBoard} in-place mediante backtracking clásico:
 * busca la primera celda vacía, prueba valores 1..9 que respeten las reglas
 * (fila / columna / subcuadrícula 3x3) y deshace si la rama no lleva a una
 * solución. Respeta los valores iniciales y solo escribe sobre celdas que
 * estaban en {@link SudokuBoard#EMPTY}.
 *
 * Opcionalmente notifica cada paso a un {@link SolveListener} para que la
 * UI pueda animar el progreso (y deshacer) en tiempo real.
 */
public class BacktrackingSolver {

    /** Observador de pasos. Puede ser {@code null} si no se requiere animación. */
    private final SolveListener listener;

    /** Asignaciones 1..9 realizadas durante la última llamada a {@link #solve}. */
    private long steps;

    /** Solver silencioso, sin notificación de pasos. */
    public BacktrackingSolver() {
        this(null);
    }

    /**
     * Solver con observador. El listener se invoca dentro del hilo del
     * solver, por lo que puede bloquearse (por ejemplo con {@code Thread.sleep})
     * para controlar la velocidad del timelapse desde la UI.
     */
    public BacktrackingSolver(SolveListener listener) {
        this.listener = listener;
    }

    /**
     * Resuelve el tablero in-place. Retorna {@code true} si encontró una
     * solución, {@code false} si el puzzle es irresoluble. Reinicia el
     * contador {@link #steps} antes de empezar.
     */
    public boolean solve(SudokuBoard board) {
        steps = 0;
        return solveRecursive(board);
    }

    /**
     * Número de asignaciones 1..9 realizadas en la última ejecución de
     * {@link #solve}. Útil como métrica didáctica (cuánto exploró el solver).
     */
    public long getSteps() {
        return steps;
    }

    /**
     * Núcleo recursivo. Caso base: no hay celdas vacías → solución hallada.
     * Caso recursivo: prueba 1..9 en la primera celda vacía, recurriendo
     * y deshaciendo si la rama falla.
     */
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
                // Colocación tentativa: registramos el paso y notificamos.
                board.set(row, col, value);
                steps++;

                if (listener != null) {
                    listener.onPlace(row, col, value);
                }

                // Si la rama de abajo encuentra solución, propagamos el éxito.
                if (solveRecursive(board)) {
                    return true;
                }

                // Backtrack: el valor no llevó a solución, vaciamos y probamos el siguiente.
                board.set(row, col, SudokuBoard.EMPTY);

                if (listener != null) {
                    listener.onUnplace(row, col);
                }
            }
        }

        // Ningún v en 1..9 funcionó: avisamos a la llamada superior para que retroceda.
        return false;
    }

    /**
     * Devuelve {@code {row, col}} de la primera celda en {@link SudokuBoard#EMPTY}
     * recorriendo fila por fila, o {@code null} si el tablero está completo.
     */
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
