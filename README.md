# Estructuras - Workshop 02 - Sudoku 9x9

Resolución de un tablero de Sudoku 9x9 usando **backtracking**, con interfaz gráfica en JavaFX.

## Enunciado

Construir un programa que resuelva un tablero de Sudoku 9x9. El tablero debe completarse con números del 1 al 9 cumpliendo las reglas del juego:

- Cada **fila** contiene los números del 1 al 9 sin repetir.
- Cada **columna** contiene los números del 1 al 9 sin repetir.
- Cada **subcuadrícula 3x3** contiene los números del 1 al 9 sin repetir.

### Modos de inicio

1. **Tablero vacío:** el algoritmo construye una solución válida desde cero.
2. **Tablero con valores iniciales:** el usuario ingresa una configuración parcial y el algoritmo la completa respetando los valores dados.

## Análisis de restricciones

Para cada celda `(r, c)` con valor `v`:

- `v` no aparece en ninguna otra celda de la fila `r`.
- `v` no aparece en ninguna otra celda de la columna `c`.
- `v` no aparece en la subcuadrícula 3x3 que contiene a `(r, c)`.

Estas tres condiciones se verifican en `SudokuBoard.isValidPlacement(row, col, value)`.

## Pseudocódigo (backtracking)

```
funcion resolver(tablero):
    para cada celda (r, c) del tablero:
        si la celda está vacía:
            para v desde 1 hasta 9:
                si esValido(tablero, r, c, v):
                    tablero[r][c] = v
                    si resolver(tablero): retornar verdadero
                    tablero[r][c] = vacio
            retornar falso        // ningún valor sirve, retroceder
    retornar verdadero            // ya no hay celdas vacías
```

## Estructura del proyecto

```
src/main/java/org/example/sudoku/
├── Launcher.java              # entry point (workaround módulo JavaFX)
├── Main.java                  # carga la escena FXML
├── SudokuController.java      # maneja eventos de la UI
├── SudokuBoard.java           # representación del tablero + validación
└── BacktrackingSolver.java    # algoritmo de backtracking

src/main/resources/org/example/sudoku/
└── sudoku-view.fxml           # vista principal
```

## Requisitos

- **JDK 21** (JavaFX 21 lo exige).
- Maven (incluido el wrapper `./mvnw`).

## Cómo ejecutar

```bash
./mvnw clean javafx:run
```

En Windows usar `mvnw.cmd` en lugar de `./mvnw`.

Desde IntelliJ: ejecutar la clase `Launcher` (no `Main` directamente — `Main` extiende `Application` y necesita un launcher separado para evitar problemas con el módulo `javafx.graphics`).

## Convenciones para el equipo

- Toda la lógica del Sudoku va en `SudokuBoard` / `BacktrackingSolver`. La UI **no** debe contener reglas del juego.
- El controlador (`SudokuController`) solo orquesta: lee el tablero de la UI, llama al solver, escribe la solución de vuelta.
- Distinguir visualmente los valores iniciales (entrada del usuario) de los valores agregados por el algoritmo.
- Tests con JUnit 5 en `src/test/java/...` (mismo paquete).

## Entregables del workshop

- [x] Explicación del problema y análisis de restricciones (este README).
- [x] Pseudocódigo del algoritmo (este README).
- [ ] Implementación con backtracking (`BacktrackingSolver` — stub listo).
- [ ] Interfaz gráfica que muestre tablero, valores iniciales, valores agregados y solución.
