# Estructuras - Workshop 02 - Sudoku 9x9

**OnlySudoku** — resolución de un tablero de Sudoku 9x9 con **backtracking**, envuelta en una interfaz de JavaFX con estética de red social (feed) y una capa "ambiente" inquietante inspirada en FNAF: Sister Location.

La parte académica (algoritmo + modelo) está intacta y separada en su propio paquete `sudoku/`; el resto es la presentación: pantalla de carga con glitch, feed con posts, generador de puzzles, visualización paso a paso del solver, overlays de éxito/fracaso y eventos ambientales aleatorios.

## Enunciado

Construir un programa que resuelva un tablero de Sudoku 9x9. El tablero debe completarse con números del 1 al 9 cumpliendo las reglas del juego:

- Cada **fila** contiene los números del 1 al 9 sin repetir.
- Cada **columna** contiene los números del 1 al 9 sin repetir.
- Cada **subcuadrícula 3x3** contiene los números del 1 al 9 sin repetir.

### Modos de inicio

1. **Tablero vacío:** el algoritmo construye una solución válida desde cero.
2. **Tablero con valores iniciales:** el usuario ingresa una configuración parcial (a mano o usando los botones Fácil / Medio / Difícil) y el algoritmo la completa respetando los valores dados.

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

## Preguntas orientadoras (para la sustentación)

- **¿Cómo se identifica una celda vacía?** Una celda con valor `0` en la matriz `int[9][9]`. El método `SudokuBoard.isEmpty(r, c)` lo expone.
- **¿Cómo se valida si un número puede colocarse?** Con `isValidPlacement(r, c, v)`: recorre la fila, la columna y la subcuadrícula 3x3, ignorando la celda destino para no compararla contra sí misma.
- **¿Cuál es el caso base?** `findEmptyCell` retorna `null` → no quedan celdas vacías → solución encontrada (retorna `true`).
- **¿Qué pasa si ningún número sirve?** Se retrocede (backtrack): se vacía la celda (`set(r, c, 0)`) y se retorna `false` para que la llamada previa pruebe el siguiente valor.
- **¿Y si el tablero no tiene solución?** El backtrack llega hasta la raíz sin que ningún valor funcione y `solve` retorna `false`. La UI muestra el overlay de fracaso.
- **¿Complejidad?** Peor caso `O(9^m)` con `m` = celdas vacías. La poda por `isValidPlacement` reduce drásticamente el árbol en la práctica.

---

# División del trabajo (4 personas)

| # | Rol | Responsable      | Entregable |
|---|---|------------------|---|
| 1 | Presentación + documentación | Samuel Tabares   | Diapositivas, sustentación, rúbrica |
| 2 | Interfaz gráfica (JavaFX) | Alejandro Urrego | App completa: feed, splash, overlays, ambiente, audio |
| 3 | Modelo del tablero | Valeria Gomez    | `SudokuBoard.java` |
| 4 | Algoritmo de backtracking | Samuel Lopez     | `BacktrackingSolver.java` |

---

## Persona A — Presentación

Responsable de que el trabajo se entienda y se defienda bien. **35% de la nota es sustentación**, así que esta parte pesa.

### Entregables

1. **Diapositivas** (Google Slides / PowerPoint / Canva), máximo ~10 slides:
   - Portada (integrantes, materia, fecha).
   - Enunciado del problema en una frase.
   - Reglas del Sudoku + las 3 restricciones (fila / columna / 3x3).
   - Qué es backtracking, en lenguaje humano (analogía: laberinto, deshacer pasos).
   - Pseudocódigo (copiar el de este README).
   - Diagrama de flujo del algoritmo (1 slide visual).
   - Demo en vivo de la UI (slide de transición; la demo la hace Persona B).
   - Respuestas a las preguntas orientadoras (slide con bullets).
   - Conclusiones y aprendizajes.
2. **Guión de sustentación** (1 página): qué dice cada quien, en qué orden, cuánto dura.
3. **Repaso de la rúbrica** antes de presentar: revisar que todos los criterios estén cubiertos.

### Rúbrica de evaluación (la del enunciado)

| Criterio | Peso |
|---|---|
| Comprensión del problema | 10% |
| Uso correcto de backtracking | 25% |
| Pseudocódigo | 5% |
| Implementación | 20% |
| Interfaz gráfica | 5% |
| **Sustentación y claridad** | **35%** |

### Tips para la sustentación

- Explica backtracking **sin mirar código**, solo con palabras. La analogía del laberinto funciona: "pruebo un camino, si no lleva a ningún lado regreso al último cruce y pruebo otro".
- Ten preparada *"¿qué pasa si el tablero no tiene solución?"* → el algoritmo retorna `false` desde la raíz; la UI distingue ese caso del de "entrada contradictoria" usando `isInitialStateValid`.
- Ten preparada *"¿cuál es la complejidad?"* → `O(9^m)` peor caso, mucho mejor en la práctica gracias a la poda.
- Ten preparada *"¿por qué `set` no valida las reglas?"* → para que el solver pueda colocar valores tentativos y deshacerlos sin pelearse con el modelo. La validación es responsabilidad del llamador vía `isValidPlacement`.

---

## Persona B — Interfaz gráfica con JavaFX (Alejandro)

La UI no es solo la cuadrícula: es una app completa con tema **OnlySudoku** (parodia de feed social) y una capa ambiental persistente. Está dividida en varios paquetes y respeta el contrato con el solver:

- Lee/escribe el tablero como `int[9][9]` (0 = vacío, 1–9 = valor) a través de `SudokuBoard`.
- Llama a `new BacktrackingSolver(listener).solve(board)` en un hilo de fondo y refleja cada `onPlace` / `onUnplace` en la UI.
- Distingue visualmente valores iniciales del usuario (`.user`) vs valores agregados por el algoritmo (`.solver`) mediante clases CSS en `app.css`.

### Lo que se ve en pantalla

- **Splash inicial** (`loading-view.fxml` + `LoadingController`) con animación de carga; al terminar dispara un efecto de glitch (`GlitchEffect`) que transiciona a la escena principal.
- **Feed** (`app-view.fxml` + `AppController`) con un post superior interactivo de Sudoku y varios posts de animatronics de FNAF Sister Location.
- **Post de Sudoku** (`sudoku-post.fxml` + `SudokuPostController`):
  - Cuadrícula 9x9 editable (bordes más gruesos cada 3 celdas para distinguir las subcuadrículas).
  - Panel **GENERAR PUZZLE** con botones Fácil / Medio / Difícil (usa `SudokuGenerator`).
  - Slider de **Velocidad** (0–50 ms) que controla la pausa entre pasos del solver para visualizar el backtracking.
  - Botones **Resolver** y **Limpiar**.
- **Posts ambientales** (`AnimatronicPost.build`) construidos desde `animatronic-post.fxml`.
- **Overlays** centralizados por `OverlayManager`:
  - `SuccessOverlay` — confeti (`ConfettiCanvas`) y `success.mp3`.
  - `FailureOverlay` — webp animado de jumpscare (`AnimatedWebpPlayer`), estática (`StaticCanvas`) y `jumpscare.mp3`.
- **Capa ambiente** (`Ambiance`) persistente: viñeta (`VignetteOverlay`), fantasmas del cursor (`CursorGhostLayer`), parpadeos en celdas, micro-glitches y "posts fantasma" que aparecen y se borran solos.
- **Audio** (`AmbiancePlayer`, `AudioCue`): `ambient_roomtone.mp3` y `heartbeat.mp3` en loop de fondo; cues puntuales para éxito/fracaso.

### Por qué el solver corre en hilo aparte

`SudokuPostController.onSolve()` envuelve la llamada al solver en un `javafx.concurrent.Task` que se ejecuta en un thread daemon. La razón es doble:

1. El backtracking puede ser largo y bloquearía el hilo de la UI de JavaFX, congelando toda la interfaz.
2. El `SolveListener` que pasamos al solver duerme con `Thread.sleep(speedSlider.getValue())` entre cada paso para mostrar el timelapse. Dormir el hilo de la UI sería catastrófico.

Los efectos visuales (`Platform.runLater(...)`) sí vuelven al hilo de la UI porque JavaFX exige que toda mutación del scene graph ocurra ahí.

---

## Persona C — Modelo del tablero (`SudokuBoard.java`)

Paquete: `org.example.sudoku.sudoku`. Es la única estructura de datos del proyecto.

### Estado interno

```java
public static final int SIZE = 9;
public static final int BOX_SIZE = 3;
public static final int EMPTY = 0;
private int[][] grid;   // 9x9, 0 = vacío, 1..9 = valor
```

`int[][]` plano y directo. No hay sets de "valores ya usados por fila/columna/caja" porque el algoritmo cabe en milisegundos sin esa optimización y el código queda mucho más legible.

### API pública

| Método | Qué hace | Cuándo lanza |
|---|---|---|
| `SudokuBoard()` | Crea tablero 9x9 vacío. | — |
| `SudokuBoard(int[][] initial)` | Copia los valores iniciales (defensiva). | Dimensión ≠ 9x9 o valor fuera de [0, 9]. |
| `int get(int r, int c)` | Lee la celda. | Índices fuera de rango. |
| `void set(int r, int c, int v)` | Asigna. **No valida reglas del Sudoku** — solo rango del valor e índices. | Valor fuera de [0, 9] o índices inválidos. |
| `boolean isEmpty(int r, int c)` | `true` si la celda vale 0. | — |
| `boolean isValidPlacement(int r, int c, int v)` | `true` si colocar `v` en `(r, c)` no viola fila/columna/caja. | Valor fuera de [0, 9]. |
| `int[][] snapshot()` | Devuelve una **copia** del grid. | — |
| `boolean isComplete()` | `true` si no quedan ceros. | — |
| `boolean isInitialStateValid()` | `true` si los valores ya colocados no se contradicen entre sí. | — |
| `static int boxIndex(int r, int c)` | Índice 0..8 de la subcuadrícula que contiene `(r, c)`. | — |

### Decisiones de diseño que hay que poder defender

1. **`set` no aplica reglas del Sudoku.** Es intencional. El solver necesita poder colocar valores tentativos durante el backtracking y luego deshacerlos; si `set` rechazara colocaciones inválidas, el solver tendría que hacer ese chequeo dos veces o trabajar con una API más complicada. La regla del proyecto es: *quien quiera validar antes de escribir, llama `isValidPlacement` primero*.

2. **`isValidPlacement` ignora la celda destino.** Cuando se valida si el valor `v` cabe en `(r, c)`, el barrido de la fila/columna/caja salta `(r, c)` mismo. Sin esto, validar una celda contra su propio valor ya colocado daría falso. Esto importa cuando la UI re-valida una entrada del usuario.

3. **`isInitialStateValid` separa dos casos.** Si la entrada del usuario tiene dos "5" en la misma fila, no es que el puzzle no tenga solución: es que ya nació roto. Distinguirlo permite mensajes de error claros y evita que el solver gaste tiempo en algo imposible por construcción.

4. **`snapshot` devuelve copia, no la referencia.** La UI nunca debe poder mutar el grid sin pasar por `set`. Devolver la referencia interna era una bomba esperando explotar.

5. **Constantes públicas (`SIZE`, `BOX_SIZE`, `EMPTY`).** El solver y los tests las usan en lugar de números mágicos. Si algún día se quisiera generalizar a 4x4 o 16x16, solo hay que tocarlas aquí.

### Cómo funciona `isValidPlacement`

```java
// 1) Si v == 0, vaciar siempre es válido.
// 2) Recorre la fila r: ningún otro c' tiene grid[r][c'] == v.
// 3) Recorre la columna c: ningún otro r' tiene grid[r'][c] == v.
// 4) Calcula la esquina superior izquierda de la caja 3x3:
//      boxRow = (r / 3) * 3
//      boxCol = (c / 3) * 3
//    y recorre las 9 celdas de la caja, excluyendo (r, c).
```

El truco aritmético `(r / 3) * 3` aprovecha la división entera para "redondear hacia abajo al múltiplo de 3 más cercano". Vale la pena mostrarlo en la sustentación: explica de un golpe cómo se localiza la subcuadrícula.

---

## Persona D — Algoritmo de backtracking (`BacktrackingSolver.java`)

Paquete: `org.example.sudoku.sudoku`. Depende solo de la API pública de `SudokuBoard`.

### Estructura

```java
public class BacktrackingSolver {
    private final SolveListener listener;   // opcional (null permitido)
    private long steps;                      // contador para "pasos del último solve"

    public BacktrackingSolver() { this(null); }
    public BacktrackingSolver(SolveListener l) { this.listener = l; }

    public boolean solve(SudokuBoard board) { steps = 0; return solveRecursive(board); }
    public long getSteps() { return steps; }
}
```

### El método clave

```java
private boolean solveRecursive(SudokuBoard board) {
    int[] empty = findEmptyCell(board);
    if (empty == null) return true;            // caso base: tablero completo

    int row = empty[0], col = empty[1];
    for (int v = 1; v <= SudokuBoard.SIZE; v++) {
        if (board.isValidPlacement(row, col, v)) {
            board.set(row, col, v);
            steps++;
            if (listener != null) listener.onPlace(row, col, v);

            if (solveRecursive(board)) return true;   // éxito → propagar arriba

            board.set(row, col, SudokuBoard.EMPTY);   // deshacer
            if (listener != null) listener.onUnplace(row, col);
        }
    }
    return false;                              // ningún v funcionó → backtrack
}
```

`findEmptyCell` simplemente recorre el tablero fila por fila y devuelve la primera celda en 0, o `null` si no hay.

### Cómo defender el algoritmo

- **Es una recursión con tres ingredientes:** caso base, paso de prueba, deshacer.
  - Caso base: no quedan celdas vacías → retorna `true`. La recursión termina.
  - Paso de prueba: para la primera celda vacía, prueba 1..9 y descarta los que `isValidPlacement` rechace.
  - Deshacer: si el valor elegido lleva a un callejón sin salida en alguna llamada recursiva inferior, se vacía la celda y se prueba el siguiente.

- **¿Cómo sabe el solver que "no hay solución"?** Cuando agota el `for` de la raíz sin que ninguna rama retorne `true`, retorna `false` hasta arriba. La UI lo recibe como `Boolean.FALSE` del `Task` y muestra el `FailureOverlay`.

- **¿Por qué fila-por-fila y no la celda "más restringida primero"?** Por simplicidad y legibilidad. Una heurística MRV (most-constrained variable) acelera el peor caso pero complica el código. Para 9x9 el orden ingenuo basta.

- **¿Por qué la `steps` y para qué sirve?** Cuenta cuántas asignaciones `set(..., 1..9)` hizo el último `solve`. Es útil como métrica didáctica: un puzzle "fácil" se resuelve en cientos de pasos, uno "patológico" en miles. Se reinicia a 0 al inicio de cada `solve` para que `getSteps()` siempre refleje la última ejecución.

- **El `SolveListener`** (interfaz separada en `SolveListener.java`) recibe un evento por cada `set` con valor 1..9 (`onPlace`) y otro por cada deshacer (`onUnplace`). La UI usa esto para animar el avance y el retroceso en tiempo real. Como el listener corre en el hilo del solver, puede `Thread.sleep` para controlar la velocidad.

### Trazado mínimo para la sustentación

Tablero parcial:

```
. 3 .          (r=0, c=0) está vacía.
. . .          Prueba v=1: ¿alguna fila/col/caja lo contiene? No → set.
. . .          Recurre: ahora (0, 2) está vacía. Prueba v=1, falla por fila.
               Prueba v=2, etc.
               Si llega a un punto sin solución, deshace y vuelve a (0, 0) con v=2.
```

Mostrar 2–3 niveles de recursión en una transparencia ayuda muchísimo.

### Complejidad

- **Peor caso teórico:** `O(9^m)` con `m` celdas vacías. Imagina que ningún chequeo poda nada.
- **Práctico:** `isValidPlacement` corta ramas enteras tempranamente. Un Sudoku de dificultad media (~34 pistas) se resuelve en milisegundos.
- **Espacio:** `O(m)` por la pila de recursión.

### Tests

Los tests viven en `src/test/java/org/example/sudoku/sudoku/BacktrackingSolverTest.java` y cubren:

- Tablero vacío: `solve` retorna `true` y el resultado es un Sudoku válido (cada fila/columna/caja contiene 1..9 sin repetir).
- Sudoku fácil clásico (`5 3 0 0 7 0 …`): `solve` retorna `true`, los valores iniciales se preservan, y el resultado coincide con la solución conocida.
- Tablero contradictorio (dos 5 en la misma fila): `solve` retorna `false`.
- `getSteps() > 0` después de resolver un tablero no trivial.

```bash
./mvnw test
```

---

## Generador de puzzles (`SudokuGenerator`)

Para alimentar los botones Fácil / Medio / Difícil existe un generador independiente.

```java
int[][] puzzle = new SudokuGenerator().generate(cluesToKeep);
```

Estrategia:

1. Construye un tablero completamente resuelto con backtracking, pero con **orden de candidatos aleatorio** en cada celda (no probar siempre 1..9 en orden). Esto produce una solución distinta cada vez.
2. Borra celdas al azar hasta dejar `cluesToKeep` pistas.

**Garantía:** el puzzle es resoluble por construcción (existía una solución antes de borrar celdas).
**No garantiza** unicidad de la solución — para esa propiedad habría que verificar después de cada borrado que el puzzle siga teniendo exactamente una solución, lo cual sale del alcance del taller.

Dificultades sugeridas: ~48 fácil, ~34 medio, ~24 difícil.

---

## Estructura del proyecto

```
src/main/java/org/example/sudoku/
├── Launcher.java                       # entry point (workaround módulo JavaFX)
├── Main.java                           # splash → preload → glitch → app
│
├── sudoku/                             # núcleo algorítmico (Personas C y D)
│   ├── SudokuBoard.java                # tablero + validación
│   ├── BacktrackingSolver.java         # backtracking + contador de pasos
│   ├── SolveListener.java              # callbacks onPlace / onUnplace
│   └── SudokuGenerator.java            # genera puzzles con solución
│
├── controllers/                        # controladores JavaFX
│   ├── AppController.java              # feed principal
│   ├── LoadingController.java          # splash inicial
│   ├── SudokuPostController.java       # post interactivo del Sudoku
│   └── AnimatronicPost.java            # factory de posts ambientales
│
├── overlay/                            # modales y overlays centralizados
│   ├── OverlayManager.java
│   ├── ModalView.java
│   ├── SuccessOverlay.java
│   └── FailureOverlay.java
│
├── fx/                                 # efectos visuales reutilizables
│   ├── GlitchEffect.java
│   ├── VignetteOverlay.java
│   ├── CursorGhostLayer.java
│   ├── ConfettiCanvas.java
│   ├── StaticCanvas.java
│   └── AnimatedWebpPlayer.java
│
├── ambiance/
│   └── Ambiance.java                   # capa persistente de "horror ambiental"
│
└── audio/
    ├── AmbiancePlayer.java             # loops de fondo
    └── AudioCue.java                   # cues puntuales

src/main/resources/org/example/sudoku/
├── loading-view.fxml
├── app-view.fxml
├── sudoku-post.fxml
├── animatronic-post.fxml
├── app.css
└── assets/
    ├── onlySudoku_logo.png
    ├── audioCues/   (ambient_roomtone, heartbeat, success, jumpscare)
    └── jumpscares/  (1..5 .webp)

src/test/java/org/example/sudoku/sudoku/
└── BacktrackingSolverTest.java
```

> Nota sobre el módulo: `module-info.java` debe hacer `opens` para todo paquete que use FXML por reflexión (controladores). Si agregas un paquete nuevo con `@FXML`, recuerda abrirlo a `javafx.fxml`.

## Requisitos del entorno

- **JDK 21** (JavaFX 21 lo exige).
- Maven (incluido el wrapper `./mvnw`).

## Build & run

```bash
./mvnw clean javafx:run                       # ejecutar la app
./mvnw test                                   # correr los tests
./mvnw compile                                # solo compilar
./mvnw test -Dtest=BacktrackingSolverTest     # un test class
```

En Windows usar `mvnw.cmd` en lugar de `./mvnw`. Desde IntelliJ: ejecutar la clase `Launcher` (no `Main` directamente — la JVM se queja del módulo JavaFX si arrancas `Main` sin el plugin).

La aplicación arranca en **pantalla completa**. Usa `Esc` para salir del modo fullscreen.

## Checklist final (antes de entregar)

- [ ] `SudokuBoard.java` implementado y con tests en verde (Persona C).
- [ ] `BacktrackingSolver.java` implementado y con tests en verde (Persona D).
- [ ] Interfaz funcional: splash → feed → cuadrícula editable, generador, slider de velocidad, resolver/limpiar, overlays de éxito/fracaso (Persona B).
- [ ] Diapositivas listas y guión de sustentación ensayado (Persona A).
- [ ] Todos pueden responder las preguntas orientadoras.
- [ ] `./mvnw clean javafx:run` ejecuta sin errores.