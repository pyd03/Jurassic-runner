package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class GameScreen implements Screen {

    private final Game game;
    private final int personaje;

    private SpriteBatch batch;
    private BitmapFont font;
    private GameMap gameMap;

    private Texture background;
    private Texture backgroundAtardecer;
    private Texture dino;
    private Texture cactus;
    private Texture cactusLargo;
    private Texture bird;

    private float anchoCactusLargo = 80;
    private float altoCactusLargo = 30;

    private Texture texOllita;
    private Texture texHoyueloTranquilo;
    private Texture texHoyueloEnojado;

    private Sound sonidoSalto;
    private Sound sonidoChoque;
    private Music musica;

    private static final float TAM_POWERUP = 50;
    private static final float ALTURA_POWERUP = 80;
    private static final float TAM_HOYUELO = 52;
    private static final float TAM_OLLITA = 46;

    private int hoyuelo = 0;
    private int ollita = 0;
    private boolean hoyueloAbsorbiendo = false;
    private float tiempoHoyuelo = 0;
    private float tiempoOllita = 0;

    // POWER-UP DEL MUNDO (parte de la generación natural)
    private boolean powerUpActivo = false;
    private int tipoPowerUp = 0;   // 1 = Hoyuelo, 2 = Ollita
    private float xPowerUp = -100;
    private float tiempoAnimacion = 0;
    private boolean terminado = false;
    private final Rectangle rectPowerUp = new Rectangle();

    // RESOLUCIÓN Y CÁMARA
    private static final float ANCHO = 900;
    private static final float ALTO = 500;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final float sueloY = GameMap.SUELO_Y;

    // DINO
    private final int filaDino = GameMap.FILA_BASE_DINO;
    private final int columnaDino = 7;
    private float yDino;

    // SALTO Y AGACHARSE
    private static final float PASO_FISICA = 1f / 60f;
    private static final float K = 55f / 47f;

    // GRAVEDAD_SALTO original: 0.6f * K -> salto de ~0.55s
    // Ahora: 0.3f * K -> salto de ~1.05s (aprox. +0.5s)
    private static final float GRAVEDAD_SALTO = 0.3f * K;
    private static final float VEL_SALTO_BASE = 10f * K;
    private static final float VEL_CORTE = 5f * K;
    private static final float ALTURA_MINIMA = 30f * K;
    private static final float ALTURA_MAXIMA = 63f * K;
    private static final float COEF_CAIDA_RAPIDA = 3f;
    private static final float VEL_INICIO_CAIDA = 1f * K;
    private static final float VELOCIDAD_INICIAL = 180f;
    private static final float VEL_CHROME_MIN = 6f;
    private static final float VEL_CHROME_MAX = 13f;

    private boolean saltando = false;
    private boolean agachado = false;
    private float velocidadSalto = 0;
    private boolean alturaMinimaAlcanzada = false;
    private boolean caidaRapida = false;
    private boolean espacioPrevio = false;
    private float acumuladorFisica = 0;

    // OBSTÁCULOS
    private int tipoObstaculo1 = 0;
    private int tipoObstaculo2 = 0;

    private boolean obstaculo1Activo = false;
    private boolean obstaculo2Activo = false;

    private boolean cactusLargo1 = false;
    private boolean cactusLargo2 = false;

    private float xObstaculo1 = -100;
    private float xObstaculo2 = -100;

    private float distanciaMinima = 240;

    private int puntos = 0;

    private float velocidad = VELOCIDAD_INICIAL;
    private final float velocidadMaxima = 400;
    private final float aumentoVelocidad = 10;

    private float tiempoObstaculo = 0;
    private final Random random = new Random();

    // FONDO
    private float tiempoFondo = 0;
    private final float TIEMPO_CAMBIO_FONDO = 65f;
    private final float DURACION_TRANSICION = 5f;

    // HITBOXES
    private final Rectangle rectDino = new Rectangle();
    private final Rectangle rectObstaculo1 = new Rectangle();
    private final Rectangle rectObstaculo2 = new Rectangle();

    // INSTRUCCIONES
    private final float DURACION_INSTRUCCIONES = 5f;
    private float tiempoInstrucciones = 0;
    private boolean mostrandoInstrucciones = true;

    // DEBUG MATRIZ
    private boolean mostrarMatriz = false;
    private Texture whitePixel;

    public GameScreen(Game game, int personaje) {
        this.game = game;
        this.personaje = personaje;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        gameMap = new GameMap();

        camera = new OrthographicCamera();
        viewport = new FitViewport(ANCHO, ALTO, camera);
        viewport.apply();

        camera.position.set(ANCHO / 2, ALTO / 2, 0);
        camera.update();

        yDino = sueloY;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        whitePixel = new Texture(pixmap);
        pixmap.dispose();

        cargarRecursos();
    }

    private void cargarRecursos() {
        try {
            dino = new Texture(
                personaje == 1 ? "dino1.png"
                    : personaje == 2 ? "dino2.png"
                    : "dino3.png"
            );
        } catch (Exception e) {
            dino = null;
        }

        try {
            cactus = new Texture("cactus.png");
        } catch (Exception e) {
            cactus = null;
        }

        try {
            cactusLargo = new Texture("cactus_largo.png");
            altoCactusLargo = anchoCactusLargo
                * cactusLargo.getHeight()
                / cactusLargo.getWidth();
        } catch (Exception e) {
            cactusLargo = null;
        }

        try {
            bird = new Texture("bird.png");
        } catch (Exception e) {
            bird = null;
        }

        try {
            texOllita = new Texture("Ollita.png");
        } catch (Exception e) {
            texOllita = null;
        }

        try {
            texHoyueloTranquilo = new Texture("Hoyuelo_tranquilo.png");
        } catch (Exception e) {
            texHoyueloTranquilo = null;
        }

        try {
            texHoyueloEnojado = new Texture("Hoyuelo_enojado.png");
        } catch (Exception e) {
            texHoyueloEnojado = null;
        }

        try {
            sonidoSalto = Gdx.audio.newSound(Gdx.files.internal("salto.wav"));
        } catch (Exception e) {
            sonidoSalto = null;
        }

        try {
            sonidoChoque = Gdx.audio.newSound(Gdx.files.internal("choque.wav"));
        } catch (Exception e) {
            sonidoChoque = null;
        }

        try {
            musica = Gdx.audio.newMusic(Gdx.files.internal("musica_fondo.mp3"));
            musica.setLooping(true);
            musica.setVolume(0.1f);
        } catch (Exception e) {
            musica = null;
        }

        try {
            background = new Texture("game_background.png");
        } catch (Exception e) {
            background = null;
        }

        try {
            backgroundAtardecer = new Texture("game_background_atardecer.png");
        } catch (Exception e) {
            backgroundAtardecer = null;
        }
    }

    @Override
    public void render(float delta) {
        if (mostrandoInstrucciones) {
            tiempoInstrucciones += delta;
            dibujarInstrucciones();

            if (tiempoInstrucciones >= DURACION_INSTRUCCIONES) {
                mostrandoInstrucciones = false;
                tiempoObstaculo = 0;
                if (musica != null) musica.play();
            }
            return;
        }

        actualizar(delta);
        dibujar();
    }

    private void actualizar(float delta) {
        tiempoFondo += delta;

        controlarSalto(delta);
        controlarAgacharse();
        moverObstaculos(delta);
        crearObstaculos(delta);
        actualizarPowerUps(delta);
        actualizarMatriz();
        comprobarColisiones();
        comprobarPowerUps();
    }

    // ACTUALIZAR LA MATRIZ CON CUERPOS COMPLETOS
    private void actualizarMatriz() {
        gameMap.limpiar();
        colocarDinoEnMatriz();

        if (obstaculo1Activo) {
            colocarObstaculoEnMatriz(tipoObstaculo1, xObstaculo1, cactusLargo1);
        }
        if (obstaculo2Activo) {
            colocarObstaculoEnMatriz(tipoObstaculo2, xObstaculo2, cactusLargo2);
        }

        // El power-up también forma parte del mundo → va en la matriz
        if (powerUpActivo) {
            colocarPowerUpEnMatriz(tipoPowerUp, xPowerUp);
        }
    }

    private void colocarDinoEnMatriz() {
        float xDino = columnaDino * 15f;
        float anchoDino = agachado ? 50f : 40f;
        float altoDino = agachado ? 30f : 55f;

        marcarRect(xDino, yDino, anchoDino, altoDino, 'D');
    }

    private void colocarObstaculoEnMatriz(int tipo, float x, boolean esCactusLargo) {
        if (tipo == 1) {
            float ancho = esCactusLargo ? anchoCactusLargo : 35f;
            float alto = esCactusLargo ? altoCactusLargo : 52.9f;
            marcarRect(x, sueloY, ancho, alto, 'C');
        } else if (tipo == 2) {
            marcarRect(x, sueloY + 25f, 60f, 40f, 'P');
        }
    }

    private void colocarPowerUpEnMatriz(int tipo, float x) {
        // Para la matriz usamos una Y fija (sin oscilación) para que la
        // celda no "salte" entre filas mientras flota.
        char c = (tipo == 1) ? 'H' : 'O';
        marcarRect(x, sueloY + ALTURA_POWERUP, TAM_POWERUP, TAM_POWERUP, c);
    }

    private void marcarRect(float x, float y, float ancho, float alto, char tipo) {
        int colIni = GameMap.columnaDesdeX(x);
        int colFin = GameMap.columnaDesdeX(x + ancho);
        int filaA = GameMap.filaDesdeY(y);
        int filaB = GameMap.filaDesdeY(y + alto);

        int filaIni = Math.min(filaA, filaB);
        int filaFin = Math.max(filaA, filaB);

        for (int f = filaIni; f <= filaFin; f++) {
            for (int c = colIni; c <= colFin; c++) {
                switch (tipo) {
                    case 'D':
                        gameMap.colocarDinosaurio(f, c);
                        break;
                    case 'C':
                        gameMap.colocarCactus(f, c);
                        break;
                    case 'P':
                        gameMap.colocarPajaro(f, c);
                        break;
                    case 'H':
                        gameMap.colocarHoyuelo(f, c);
                        break;
                    case 'O':
                        gameMap.colocarOllita(f, c);
                        break;
                }
            }
        }
    }

    // COLISIONES: MATRIZ (BROAD-PHASE) + PÍXELES (FINE-PHASE)
    private void comprobarColisiones() {
        if (agachado) {
            rectDino.set(columnaDino * 15 + 8, yDino + 3, 28, 30);
        } else {
            rectDino.set(columnaDino * 15 + 8, yDino + 4, 25, 47);
        }

        // Broad-phase con la matriz
        if (!gameMap.rectTocaObstaculo(rectDino)) return;

        if (obstaculo1Activo) {
            crearHitbox(rectObstaculo1, tipoObstaculo1, xObstaculo1, cactusLargo1);
            if (rectDino.overlaps(rectObstaculo1)) {
                colision();
                return;
            }
        }
        if (obstaculo2Activo) {
            crearHitbox(rectObstaculo2, tipoObstaculo2, xObstaculo2, cactusLargo2);
            if (rectDino.overlaps(rectObstaculo2)) {
                colision();
            }
        }
    }

    private void crearHitbox(Rectangle rect, int tipo, float x, boolean esCactusLargo) {
        if (tipo == 1) {
            if (esCactusLargo) {
                rect.set(
                    x + anchoCactusLargo * 0.15f,
                    sueloY + altoCactusLargo * 0.05f,
                    anchoCactusLargo * 0.70f,
                    altoCactusLargo * 0.75f
                );
            } else {
                rect.set(x + 12, sueloY + 5, 15, 25);
            }
        } else {
            rect.set(x + 5, sueloY + 50, 50, 25);
        }
    }

    // SALTO / AGACHARSE
    private void controlarSalto(float delta) {
        boolean espacio = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !saltando && !agachado) {
            iniciarSalto();
        }

        if (saltando && espacioPrevio && !espacio) {
            terminarSalto();
        }
        espacioPrevio = espacio;

        if (!saltando) {
            acumuladorFisica = 0;
            return;
        }

        boolean abajo = Gdx.input.isKeyPressed(Input.Keys.S);
        if (abajo && !caidaRapida) {
            caidaRapida = true;
            velocidadSalto = -VEL_INICIO_CAIDA;
        } else if (!abajo && caidaRapida) {
            caidaRapida = false;
        }

        acumuladorFisica += Math.min(delta, 0.1f);
        while (saltando && acumuladorFisica >= PASO_FISICA) {
            pasoSalto();
            acumuladorFisica -= PASO_FISICA;
        }
    }

    private void iniciarSalto() {
        float t = (velocidad - VELOCIDAD_INICIAL) / (velocidadMaxima - VELOCIDAD_INICIAL);
        float velChrome = VEL_CHROME_MIN + t * (VEL_CHROME_MAX - VEL_CHROME_MIN);

        saltando = true;
        velocidadSalto = VEL_SALTO_BASE + (velChrome / 10f) * K;
        alturaMinimaAlcanzada = false;
        caidaRapida = false;
        acumuladorFisica = 0;

        if (sonidoSalto != null) sonidoSalto.play(1f);
    }

    private void terminarSalto() {
        if (alturaMinimaAlcanzada && velocidadSalto > VEL_CORTE) {
            velocidadSalto = VEL_CORTE;
        }
    }

    private void pasoSalto() {
        float altura = yDino - sueloY;

        if (caidaRapida) {
            altura += velocidadSalto * COEF_CAIDA_RAPIDA;
        } else {
            altura += velocidadSalto;
        }

        velocidadSalto -= GRAVEDAD_SALTO;

        if (altura > ALTURA_MINIMA || caidaRapida) alturaMinimaAlcanzada = true;
        if (altura > ALTURA_MAXIMA || caidaRapida) terminarSalto();

        if (altura <= 0) {
            altura = 0;
            velocidadSalto = 0;
            saltando = false;
            caidaRapida = false;
        }

        yDino = sueloY + altura;
    }

    private void controlarAgacharse() {
        agachado = Gdx.input.isKeyPressed(Input.Keys.S) && !saltando;
    }

    // OBSTÁCULOS
    private void moverObstaculos(float delta) {
        float movimiento = velocidad * delta;

        if (obstaculo1Activo) {
            xObstaculo1 -= movimiento;
            if (xObstaculo1 < -100) {
                obstaculo1Activo = false;
                sumarPunto();
            }
        }
        if (obstaculo2Activo) {
            xObstaculo2 -= movimiento;
            if (xObstaculo2 < -100) {
                obstaculo2Activo = false;
                sumarPunto();
            }
        }

        // Power-up del mundo: se mueve con el mundo
        if (powerUpActivo) {
            xPowerUp -= movimiento;
            if (xPowerUp < -100) {
                powerUpActivo = false;
                // NO da puntos: es un item, no un obstáculo
            }
        }
    }

    private void sumarPunto() {
        puntos++;
        velocidad = Math.min(velocidad + aumentoVelocidad, velocidadMaxima);
        distanciaMinima = Math.max(140, distanciaMinima - 5);
    }

    private void crearObstaculos(float delta) {
        tiempoObstaculo += delta;
        if (tiempoObstaculo < 0.9f) return;

        // Esperamos a que no haya NADA activo (obstáculos ni power-up)
        if (obstaculo1Activo || obstaculo2Activo || powerUpActivo) return;

        tiempoObstaculo = 0;

        // Primer obstáculo siempre
        obstaculo1Activo = true;
        xObstaculo1 = 920;
        tipoObstaculo1 = random.nextInt(2) + 1;
        if (tipoObstaculo1 == 1) cactusLargo1 = random.nextBoolean();

        // 70% de probabilidad de un segundo obstáculo
        if (random.nextFloat() < 0.70f) {
            obstaculo2Activo = true;
            xObstaculo2 = xObstaculo1 + distanciaMinima + random.nextInt(100);
            tipoObstaculo2 = random.nextInt(2) + 1;
            if (tipoObstaculo2 == 1) cactusLargo2 = random.nextBoolean();
        } else {
            obstaculo2Activo = false;
            tipoObstaculo2 = 0;
        }

        // 40% de probabilidad de que aparezca un power-up detrás de la oleada
        // (Hoyuelo u Ollita). Esto es la "generación natural del mundo".
        if (random.nextFloat() < 0.40f) {
            float baseX = obstaculo2Activo ? xObstaculo2 : xObstaculo1;
            xPowerUp = baseX + distanciaMinima + random.nextInt(150);
            tipoPowerUp = random.nextBoolean() ? 1 : 2;
            powerUpActivo = true;
        }
    }

    // DIBUJO
    private void dibujar() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        dibujarFondo();
        dibujarObjetos();

        if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            mostrarMatriz = !mostrarMatriz;
        }
        if (mostrarMatriz) {
            dibujarMatrizDebug();
        }

        font.getData().setScale(1.5f);
        font.draw(batch, "PUNTOS: " + puntos, 30, ALTO - 40);

        batch.end();
    }

    private void dibujarFondo() {
        if (background != null) {
            batch.setColor(1, 1, 1, 1);
            batch.draw(background, 0, 0, ANCHO, ALTO);
        }

        if (tiempoFondo >= TIEMPO_CAMBIO_FONDO && backgroundAtardecer != null) {
            float progreso = (tiempoFondo - TIEMPO_CAMBIO_FONDO) / DURACION_TRANSICION;
            if (progreso > 1) progreso = 1;

            batch.setColor(1, 1, 1, progreso);
            batch.draw(backgroundAtardecer, 0, 0, ANCHO, ALTO);
            batch.setColor(1, 1, 1, 1);
        }
    }

    private void dibujarObjetos() {
        float xDino = columnaDino * 15;

        if (dino != null) {
            if (agachado) batch.draw(dino, xDino, yDino, 50, 30);
            else batch.draw(dino, xDino, yDino, 40, 55);
        }

        dibujarObstaculo(tipoObstaculo1, xObstaculo1, obstaculo1Activo, cactusLargo1);
        dibujarObstaculo(tipoObstaculo2, xObstaculo2, obstaculo2Activo, cactusLargo2);

        dibujarPowerUps(xDino);
    }

    private void dibujarObstaculo(int tipo, float x, boolean activo, boolean esCactusLargo) {
        if (!activo) return;

        if (tipo == 1) {
            if (esCactusLargo && cactusLargo != null) {
                batch.draw(cactusLargo, x, sueloY, anchoCactusLargo, altoCactusLargo);
            } else if (cactus != null) {
                batch.draw(cactus, x, sueloY, 35, 52.9f);
            }
        } else if (tipo == 2 && bird != null) {
            batch.draw(bird, x, sueloY + 25, 60, 40);
        }
    }

// DEBUG: VISUALIZAR LA MATRIZ EN PANTALLA
    private void dibujarMatrizDebug() {
        char[][] m = gameMap.getMapa();

        for (int f = 0; f < GameMap.FILAS; f++) {
            for (int c = 0; c < GameMap.COLUMNAS; c++) {
                char ch = m[f][c];
                if (ch == ' ') continue;

                float x = GameMap.xDesdeColumna(c);
                float y = GameMap.yDesdeFila(f);

                if (ch == 'D') batch.setColor(0f, 1f, 0f, 0.35f);
                else if (ch == 'C') batch.setColor(1f, 0f, 0f, 0.35f);
                else if (ch == 'P') batch.setColor(0f, 0f, 1f, 0.35f);
                else if (ch == 'H') batch.setColor(1f, 0.6f, 0f, 0.45f);
                else if (ch == 'O') batch.setColor(1f, 1f, 0f, 0.45f);
                else if (ch == '-') batch.setColor(0.6f, 0.6f, 0f, 0.20f);
                else continue;

                batch.draw(
                    whitePixel,
                    x, y,
                    GameMap.CELDA_ANCHO,
                    GameMap.CELDA_ALTO
                );
            }
        }

        batch.setColor(1, 1, 1, 1);
    }

    // INSTRUCCIONES
    private void dibujarInstrucciones() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (background != null) batch.draw(background, 0, 0, ANCHO, ALTO);

        GlyphLayout layout = new GlyphLayout();
        dibujarTextoCentrado(layout, "PREPARATE", 2f, 360);
        dibujarTextoCentrado(layout, "ESPACIO = SALTAR", 1.4f, 280);
        dibujarTextoCentrado(layout, "S = AGACHARSE", 1.4f, 220);

        int segundos = (int) Math.ceil(DURACION_INSTRUCCIONES - tiempoInstrucciones);
        dibujarTextoCentrado(layout, "Comienza en " + segundos, 1.2f, 140);

        batch.end();
    }

    private void dibujarTextoCentrado(GlyphLayout layout, String texto, float escala, float y) {
        font.getData().setScale(escala);
        layout.setText(font, texto);
        font.draw(batch, texto, (ANCHO - layout.width) / 2, y);
    }

    // POWER-UPS (efectos y dibujo)
    private float yPowerUp() {
        return sueloY + ALTURA_POWERUP
            + (float) Math.sin(tiempoAnimacion * 4) * 4;
    }

    private void actualizarPowerUps(float delta) {
        tiempoAnimacion += delta;

        // Estado del efecto "Hoyuelo"
        if (hoyueloAbsorbiendo) {
            tiempoHoyuelo += delta;
            if (tiempoHoyuelo >= 1f) {
                hoyuelo = 0;
                hoyueloAbsorbiendo = false;
            }
        }

        // Estado del efecto "Ollita"
        if (ollita == 1) {
            tiempoOllita -= delta;
            if (tiempoOllita <= 0) ollita = 0;
        }

        // NOTA: ya no se generan power-ups por timer.
        // Ahora se generan en crearObstaculos() como parte del mundo.
    }

    private void comprobarPowerUps() {
        if (!powerUpActivo) return;

        rectPowerUp.set(
            xPowerUp + 8,
            yPowerUp() + 8,
            TAM_POWERUP - 16,
            TAM_POWERUP - 16
        );

        if (rectDino.overlaps(rectPowerUp)) {
            powerUpActivo = false;

            if (tipoPowerUp == 1) {
                hoyuelo = 1;
                hoyueloAbsorbiendo = false;
                tiempoHoyuelo = 0;
            } else {
                ollita = 1;
                tiempoOllita = 5f + random.nextFloat() * 5f;
            }
        }
    }

    private void colision() {
        if (terminado || ollita == 1) return;

        if (hoyuelo == 1) {
            if (!hoyueloAbsorbiendo) {
                hoyueloAbsorbiendo = true;
                tiempoHoyuelo = 0;
                if (sonidoChoque != null) sonidoChoque.play(1f);
            }
            return;
        }

        terminado = true;
        if (sonidoChoque != null) sonidoChoque.play(1f);
        gameOver();
    }

    private void dibujarPowerUps(float xDino) {
        // Power-up flotando en el mundo
        if (powerUpActivo) {
            Texture tex = tipoPowerUp == 1 ? texHoyueloTranquilo : texOllita;
            if (tex != null) {
                batch.draw(tex, xPowerUp, yPowerUp(), TAM_POWERUP, TAM_POWERUP);
            }
        }

        float anchoDino = agachado ? 50 : 40;
        float altoDino = agachado ? 30 : 55;

        // Indicador de Hoyuelo activo sobre el dino
        if (hoyuelo == 1 && texHoyueloEnojado != null) {
            boolean visible = !hoyueloAbsorbiendo
                || ((int) (tiempoHoyuelo * 10)) % 2 == 0;

            if (visible) {
                batch.draw(
                    texHoyueloEnojado,
                    xDino + anchoDino - 22,
                    yDino - 2,
                    TAM_HOYUELO,
                    TAM_HOYUELO
                );
            }
        }

        // Indicador de Ollita activa sobre el dino
        if (ollita == 1 && texOllita != null) {
            boolean visible = tiempoOllita > 1.5f
                || ((int) (tiempoOllita * 8)) % 2 == 0;

            if (visible) {
                float flotar = (float) Math.sin(tiempoAnimacion * 6) * 3;
                batch.draw(
                    texOllita,
                    xDino + anchoDino / 2 - TAM_OLLITA / 2,
                    yDino + altoDino - 4 + flotar,
                    TAM_OLLITA,
                    TAM_OLLITA
                );
            }
        }
    }

    // GAME OVER / CICLO DE VIDA
    private void gameOver() {
        game.setScreen(new GameOverScreen(game, personaje, puntos));
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (dino != null) dino.dispose();
        if (cactus != null) cactus.dispose();
        if (cactusLargo != null) cactusLargo.dispose();
        if (bird != null) bird.dispose();
        if (sonidoSalto != null) sonidoSalto.dispose();
        if (sonidoChoque != null) sonidoChoque.dispose();
        if (musica != null) musica.dispose();
        if (texOllita != null) texOllita.dispose();
        if (texHoyueloTranquilo != null) texHoyueloTranquilo.dispose();
        if (texHoyueloEnojado != null) texHoyueloEnojado.dispose();
        if (background != null) background.dispose();
        if (backgroundAtardecer != null) backgroundAtardecer.dispose();
    }
}
