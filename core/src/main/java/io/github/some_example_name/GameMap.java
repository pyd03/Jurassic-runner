package io.github.some_example_name;

import com.badlogic.gdx.math.Rectangle;
import java.util.Random;

public class GameMap {

    // Dimensiones de la matriz (mapa lógico)
    public static final int FILAS = 12;
    public static final int COLUMNAS = 60;

    // Tamaño de cada celda en píxeles
    public static final float CELDA_ANCHO = 15f;
    public static final float CELDA_ALTO  = 15f;

    // Fila del suelo (última fila)
    public static final int FILA_SUELO = FILAS - 1; // 11

    // Fila donde se apoya la base del dinosaurio cuando está en el suelo
    public static final int FILA_BASE_DINO = FILA_SUELO - 1; // 10

    // Altura del suelo en píxeles (debe coincidir con GameScreen.sueloY)
    public static final float SUELO_Y = 200f;

    private final char[][] mapa;
    private final Random random;

    public GameMap() {
        mapa = new char[FILAS][COLUMNAS];
        random = new Random();
        limpiar();
    }

    public void limpiar() {
        for (int fila = 0; fila < FILAS; fila++) {
            for (int columna = 0; columna < COLUMNAS; columna++) {
                if (fila == FILA_SUELO) {
                    mapa[fila][columna] = '-';
                } else {
                    mapa[fila][columna] = ' ';
                }
            }
        }
    }

    public char[][] getMapa() {
        return mapa;
    }

    // =========================================================
    // CONVERSIÓN PÍXELES <-> MATRIZ
    // =========================================================

    public static int columnaDesdeX(float x) {
        return (int) Math.floor(x / CELDA_ANCHO);
    }

    public static float xDesdeColumna(float columna) {
        return columna * CELDA_ANCHO;
    }

    public static int filaDesdeY(float y) {
        return FILA_BASE_DINO
            - (int) Math.floor((y - SUELO_Y) / CELDA_ALTO);
    }

    public static float yDesdeFila(float fila) {
        return SUELO_Y + (FILA_BASE_DINO - fila) * CELDA_ALTO;
    }

    // =========================================================
    // COLOCAR ENTIDADES
    // =========================================================

    public void colocarDinosaurio(int fila, int columna) {
        if (posicionValida(fila, columna)) {
            mapa[fila][columna] = 'D';
        }
    }

    public void colocarCactus(int fila, int columna) {
        if (posicionValida(fila, columna)) {
            mapa[fila][columna] = 'C';
        }
    }

    public void colocarPajaro(int fila, int columna) {
        if (posicionValida(fila, columna)) {
            mapa[fila][columna] = 'P';
        }
    }

    // Hoyuelo (power-up que absorbe un golpe)
    public void colocarHoyuelo(int fila, int columna) {
        if (posicionValida(fila, columna)) {
            mapa[fila][columna] = 'H';
        }
    }

    // Ollita (power-up de invulnerabilidad temporal)
    public void colocarOllita(int fila, int columna) {
        if (posicionValida(fila, columna)) {
            mapa[fila][columna] = 'O';
        }
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    public char getCelda(int fila, int columna) {
        if (!posicionValida(fila, columna)) return ' ';
        return mapa[fila][columna];
    }

    public boolean esObstaculo(int fila, int columna) {
        char c = getCelda(fila, columna);
        return c == 'C' || c == 'P';
    }

    public boolean esPowerUp(int fila, int columna) {
        char c = getCelda(fila, columna);
        return c == 'H' || c == 'O';
    }

    public boolean esDinosaurio(int fila, int columna) {
        return getCelda(fila, columna) == 'D';
    }

    public boolean posicionValida(int fila, int columna) {
        return fila >= 0 && fila < FILAS
            && columna >= 0 && columna < COLUMNAS;
    }

    // =========================================================
    // COLISIÓN BASADA EN LA MATRIZ (BROAD-PHASE)
    // =========================================================

    /**
     * Devuelve true si el rectángulo (en píxeles) toca alguna celda
     * marcada como obstáculo ('C' o 'P') en la matriz.
     */
    public boolean rectTocaObstaculo(Rectangle rect) {
        int colIni = columnaDesdeX(rect.x);
        int colFin = columnaDesdeX(rect.x + rect.width);

        int filaA = filaDesdeY(rect.y);
        int filaB = filaDesdeY(rect.y + rect.height);

        int filaIni = Math.min(filaA, filaB);
        int filaFin = Math.max(filaA, filaB);

        for (int f = filaIni; f <= filaFin; f++) {
            for (int c = colIni; c <= colFin; c++) {
                if (esObstaculo(f, c)) return true;
            }
        }
        return false;
    }

    /**
     * Devuelve true si el rectángulo (en píxeles) toca alguna celda
     * marcada como power-up ('H' u 'O') en la matriz.
     */
    public boolean rectTocaPowerUp(Rectangle rect) {
        int colIni = columnaDesdeX(rect.x);
        int colFin = columnaDesdeX(rect.x + rect.width);

        int filaA = filaDesdeY(rect.y);
        int filaB = filaDesdeY(rect.y + rect.height);

        int filaIni = Math.min(filaA, filaB);
        int filaFin = Math.max(filaA, filaB);

        for (int f = filaIni; f <= filaFin; f++) {
            for (int c = colIni; c <= colFin; c++) {
                if (esPowerUp(f, c)) return true;
            }
        }
        return false;
    }
}
