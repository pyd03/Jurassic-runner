package io.github.some_example_name;

import java.util.Random;

public class GameMap {

    public static final int FILAS = 12;
    public static final int COLUMNAS = 60;

    private final char[][] mapa;

    private final Random random;

    public GameMap() {

        mapa = new char[FILAS][COLUMNAS];

        random = new Random();

        limpiar();
    }

    public void limpiar() {

        for (int fila = 0; fila < FILAS; fila++) {

            for (int columna = 0;
                 columna < COLUMNAS;
                 columna++) {

                if (fila == FILAS - 1) {

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

    public void colocarDinosaurio(
        int fila,
        int columna) {

        if (posicionValida(fila, columna)) {

            mapa[fila][columna] = 'D';
        }
    }

    public void colocarCactus(
        int fila,
        int columna) {

        if (posicionValida(fila, columna)) {

            mapa[fila][columna] = 'C';
        }
    }

    public void colocarPajaro(
        int fila,
        int columna) {

        if (posicionValida(fila, columna)) {

            mapa[fila][columna] = 'P';
        }
    }

    private boolean posicionValida(
        int fila,
        int columna) {

        return fila >= 0
            && fila < FILAS
            && columna >= 0
            && columna < COLUMNAS;
    }
}
