package org.example.sudoku.sudoku;

/**
 * Recibe eventos del solver durante el backtracking. Permite a la UI mostrar
 * el progreso en vivo sin que el solver tenga que almacenar pasos.
 *
 * Las implementaciones de la UI pueden bloquear el hilo del solver (por
 * ejemplo con {@link Thread#sleep(long)}) para controlar el ritmo del
 * timelapse desde un control deslizante.
 */
public interface SolveListener {

    void onPlace(int row, int col, int value);

    void onUnplace(int row, int col);
}
