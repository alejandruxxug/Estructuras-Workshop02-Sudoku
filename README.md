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

## Preguntas orientadoras (para la sustentación)

- **¿Cómo se identifica una celda vacía?** Una celda con valor `0` en la matriz `int[9][9]`.
- **¿Cómo se valida si un número puede colocarse?** Con `isValidPlacement(r, c, v)`: revisa fila, columna y subcuadrícula 3x3.
- **¿Cuál es el caso base?** No quedan celdas vacías → solución encontrada (retorna `true`).
- **¿Qué pasa si ningún número sirve?** Se retrocede (backtrack): se vacía la celda y se retorna `false` para que la llamada previa pruebe otro valor.

---

# División del trabajo (4 personas)

| # | Rol | Responsable      | Entregable |
|---|---|------------------|---|
| 1 | Presentación + documentación | Samuel Tabares   | Diapositivas, sustentación, rúbrica |
| 2 | Interfaz gráfica (JavaFX) | Alejandro Urrego | `SudokuController` + FXML + estilos |
| 3 | Modelo del tablero | Valeria Gomez    | `SudokuBoard.java` |
| 4 | Algoritmo de backtracking | Samuel Lopez     | `BacktrackingSolver.java` |

> Personas C y D: cada especificación de abajo está escrita para que la peguen tal cual en una IA (ChatGPT/Claude) y obtengan código que compila y pasa los tests. **No cambien las firmas de métodos** ni los nombres de archivos — la UI y el solver dependen de ellos.

---

## Persona A — Presentación 

Eres responsable de que el trabajo se entienda y se defienda bien. **35% de la nota es sustentación**, así que esta parte pesa.

### Entregables

1. **Diapositivas** (Google Slides / PowerPoint / Canva), máximo ~10 slides:
   - Portada (integrantes, materia, fecha).
   - Enunciado del problema en una frase.
   - Reglas del Sudoku + las 3 restricciones (fila / columna / 3x3).
   - Qué es backtracking, en lenguaje humano (analogía: laberinto, deshacer pasos).
   - Pseudocódigo (copiar el de este README).
   - Diagrama de flujo del algoritmo (1 slide visual).
   - Demo en vivo de la UI (slide de transición; la demo la hace Persona B).
   - Respuestas a las 4 preguntas orientadoras (slide con bullets).
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

- Practica explicar backtracking **sin mirar código**, solo con palabras.
- Ten preparada la respuesta a: *"¿qué pasa si el tablero no tiene solución?"* → el algoritmo retorna `false` desde la raíz.
- Ten preparada: *"¿cuál es la complejidad?"* → en el peor caso `O(9^m)` donde `m` es el número de celdas vacías; en la práctica la poda por validación lo hace mucho más rápido.

---

## Persona B — Interfaz gráfica con JavaFX (Alejandro)

**Libertad creativa.** Solo respeta el contrato con el solver:

- Lee/escribe el tablero como `int[9][9]` (0 = vacío, 1-9 = valor).
- Llama a `new BacktrackingSolver().solve(board)` donde `board` es un `SudokuBoard`.
- Distingue **visualmente** valores iniciales del usuario vs valores agregados por el algoritmo (color, negrita, fondo, lo que quieras).

### Lo que debe verse en pantalla

- Cuadrícula 9x9 editable (las subcuadrículas 3x3 deben notarse — bordes más gruesos, separación, o lo que prefieras).
- Botón **Resolver** y botón **Limpiar**.
- Estado / mensajes (ej: "Tablero inválido", "Sin solución", "Resuelto en X ms").
- Bonus si quieres: cargar ejemplo, validar en vivo, animación del backtracking, tema oscuro, etc.

### Archivos a tocar

- `src/main/resources/org/example/sudoku/sudoku-view.fxml` — la vista.
- `src/main/java/org/example/sudoku/SudokuController.java` — eventos.
- (Opcional) un `.css` para estilos.

---

## Persona C — Modelo del tablero (`SudokuBoard.java`)

> **Instrucciones para ti:** copia toda esta sección (desde "PROMPT PARA IA" hasta el final del bloque) y pégala en ChatGPT/Claude. El código que genere lo guardas en `src/main/java/org/example/sudoku/SudokuBoard.java`. Después corres `./mvnw test` y debe pasar.

```
Instrucciones de la clase:

Genera una clase Java llamada `SudokuBoard` en el paquete
`org.example.sudoku`. Requisitos exactos:

1. Atributo privado `int[9][9] grid`. El valor 0 representa celda vacía;
   los valores 1..9 representan números colocados.

2. Constructor sin argumentos: crea un tablero 9x9 lleno de ceros.

3. Constructor `SudokuBoard(int[][] initial)`: copia los valores de
   `initial` (debe ser 9x9). Lanza `IllegalArgumentException` si las
   dimensiones no son 9x9 o si algún valor está fuera de [0, 9].

4. `int get(int row, int col)` — retorna el valor en (row, col).

5. `void set(int row, int col, int value)` — asigna `value` en (row, col).
   Lanza `IllegalArgumentException` si value no está en [0, 9] o los
   índices están fuera de rango.

6. `boolean isEmpty(int row, int col)` — true si la celda vale 0.

7. `boolean isValidPlacement(int row, int col, int value)` — retorna true
   si colocar `value` en (row, col) NO viola ninguna regla del Sudoku:
   - `value` no aparece ya en la fila `row`
   - `value` no aparece ya en la columna `col`
   - `value` no aparece ya en la subcuadrícula 3x3 que contiene (row,col)
   Importante: la celda (row, col) misma se ignora durante la verificación
   (para que validar una celda contra su propio valor no de falso).
   Si value es 0, retornar true (vaciar siempre es válido).

8. `int[][] snapshot()` — retorna una COPIA del grid (no la referencia
   interna), para que la UI no pueda mutar el estado por accidente.

9. `boolean isComplete()` — true si no quedan ceros en el tablero.

10. Método estático `int boxIndex(int row, int col)` que retorna el índice
    de la subcuadrícula 3x3 (0..8) que contiene (row, col). Útil para tests.

No uses librerías externas. Solo java.util si lo necesitas. Sin
streams complicados — código directo y legible para estudiantes.
Incluye Javadoc breve en cada método público.

Además, genera tests JUnit 5 en
`src/test/java/org/example/sudoku/SudokuBoardTest.java` que cubran:
- constructor vacío produce todos ceros
- isValidPlacement detecta conflicto en fila
- isValidPlacement detecta conflicto en columna
- isValidPlacement detecta conflicto en subcuadrícula 3x3
- isValidPlacement permite el mismo valor en su propia celda
- snapshot retorna copia independiente (mutarla no afecta al original)
- isComplete falso con ceros, verdadero sin ceros
```

### Cómo verificar que funciona

```bash
./mvnw test
```

Todos los tests de `SudokuBoardTest` deben pasar en verde.

---

## Persona D — Algoritmo de backtracking (`BacktrackingSolver.java`)

> **Instrucciones para ti:** copia toda esta sección y pégala en una IA. Guarda el resultado en `src/main/java/org/example/sudoku/BacktrackingSolver.java`. Corre `./mvnw test`.
>
> **Importante:** este código DEPENDE de la clase `SudokuBoard` que hace Persona C. Si Persona C aún no terminó, pídele las firmas (los métodos públicos están listados arriba).

```
Instrucciones de la clase:

Genera una clase Java llamada `BacktrackingSolver` en el paquete
`org.example.sudoku`. Usa la clase `SudokuBoard` ya existente (mismo
paquete) que expone:

  int get(int row, int col)
  void set(int row, int col, int value)   // 0 vacía la celda
  boolean isEmpty(int row, int col)
  boolean isValidPlacement(int row, int col, int value)
  boolean isComplete()

Requisitos exactos:

1. Método público `boolean solve(SudokuBoard board)`:
   - Resuelve el tablero IN-PLACE usando backtracking.
   - Retorna true si encontró una solución, false si no existe.
   - Respeta los valores iniciales (celdas no vacías) y solo modifica
     celdas que estaban en 0.

2. Algoritmo (backtracking clásico):
   - Buscar la primera celda vacía (recorriendo fila por fila).
   - Si no hay celda vacía → tablero resuelto, retornar true.
   - Para v en 1..9:
       si board.isValidPlacement(row, col, v):
           board.set(row, col, v)
           si solve(board): retornar true
           board.set(row, col, 0)   // deshacer
   - Si ningún v sirvió, retornar false.

3. Método auxiliar privado `int[] findEmptyCell(SudokuBoard board)` que
   retorna `new int[]{row, col}` de la primera celda vacía, o `null` si
   no hay.

4. Contador público (atributo `private long steps`) que cuenta cuántas
   asignaciones (`board.set` con valor 1..9) hizo durante el último
   `solve`. Exponer con `long getSteps()`. Reiniciar a 0 al inicio de
   cada `solve`.

No uses librerías externas. Código directo, legible, con Javadoc breve.
NO uses streams ni recursión con lambdas. Recursión simple.

Además, genera tests JUnit 5 en
`src/test/java/org/example/sudoku/BacktrackingSolverTest.java` que cubran:

- Tablero vacío: solve retorna true y el resultado es un Sudoku válido
  (cada fila, columna y subcuadrícula contiene 1..9 sin repetir).

- Tablero con valores iniciales (usa este ejemplo, conocido como
  "Sudoku fácil"):
    5 3 0 | 0 7 0 | 0 0 0
    6 0 0 | 1 9 5 | 0 0 0
    0 9 8 | 0 0 0 | 0 6 0
    ------+-------+------
    8 0 0 | 0 6 0 | 0 0 3
    4 0 0 | 8 0 3 | 0 0 1
    7 0 0 | 0 2 0 | 0 0 6
    ------+-------+------
    0 6 0 | 0 0 0 | 2 8 0
    0 0 0 | 4 1 9 | 0 0 5
    0 0 0 | 0 8 0 | 0 7 9

  solve debe retornar true, los valores iniciales deben preservarse, y
  el resultado completo es:
    5 3 4 | 6 7 8 | 9 1 2
    6 7 2 | 1 9 5 | 3 4 8
    1 9 8 | 3 4 2 | 5 6 7
    ------+-------+------
    8 5 9 | 7 6 1 | 4 2 3
    4 2 6 | 8 5 3 | 7 9 1
    7 1 3 | 9 2 4 | 8 5 6
    ------+-------+------
    9 6 1 | 5 3 7 | 2 8 4
    2 8 7 | 4 1 9 | 6 3 5
    3 4 5 | 2 8 6 | 1 7 9

- Tablero sin solución (ej: dos 5 en la misma fila como valores
  iniciales): solve retorna false.

- getSteps() > 0 después de resolver un tablero no trivial.
```

### Cómo verificar

```bash
./mvnw test
```

Los tests de `BacktrackingSolverTest` deben pasar.

---

## Estructura del proyecto

```
src/main/java/org/example/sudoku/
├── Launcher.java              # entry point (workaround módulo JavaFX)
├── Main.java                  # carga la escena FXML
├── SudokuController.java      # (Persona B) eventos UI
├── SudokuBoard.java           # (Persona C) tablero + validación
└── BacktrackingSolver.java    # (Persona D) backtracking

src/main/resources/org/example/sudoku/
└── sudoku-view.fxml           # (Persona B) vista

src/test/java/org/example/sudoku/
├── SudokuBoardTest.java       # (Persona C)
└── BacktrackingSolverTest.java # (Persona D)
```

## Requisitos del entorno

- **JDK 21** (JavaFX 21 lo exige).
- Maven (incluido el wrapper `./mvnw`).

## Cómo ejecutar

```bash
./mvnw clean javafx:run
```

En Windows usar `mvnw.cmd` en lugar de `./mvnw`. Desde IntelliJ: ejecutar la clase `Launcher` (no `Main` directamente).

## Checklist final (antes de entregar)

- [ ] `SudokuBoard.java` implementado y con tests en verde (Persona C).
- [ ] `BacktrackingSolver.java` implementado y con tests en verde (Persona D).
- [ ] Interfaz gráfica funcional: muestra cuadrícula, valores iniciales distinguidos, botón resolver, botón limpiar (Persona B).
- [ ] Diapositivas listas y guión de sustentación ensayado (Persona A).
- [ ] Todos pueden responder las 4 preguntas orientadoras.
- [ ] `./mvnw clean javafx:run` ejecuta sin errores.
