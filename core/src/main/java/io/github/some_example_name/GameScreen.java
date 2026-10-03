package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    private static final float TAM_POWERUP = 50;
    private static final float ALTURA_POWERUP = 80;
    private static final float TAM_HOYUELO = 52;
    private static final float TAM_OLLITA = 46;

    private int hoyuelo = 0;
    private int ollita = 0;
    private boolean hoyueloAbsorbiendo = false;
    private float tiempoHoyuelo = 0;
    private float tiempoOllita = 0;

    private boolean powerUpActivo = false;
    private int tipoPowerUp = 0;
    private float xPowerUp = -100;
    private float tiempoPowerUp = 0;
    private float intervaloPowerUp = 10f;
    private float tiempoAnimacion = 0;
    private boolean terminado = false;
    private final Rectangle rectPowerUp = new Rectangle();

    // RESOLUCIÓN Y CÁMARA
    private static final float ANCHO = 900;
    private static final float ALTO = 500;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final float sueloY = 200;

    // DINO
    private final int filaDino = 9;
    private final int columnaDino = 7;
    private float yDino;

    // SALTO Y AGACHARSE
    private static final float PASO_FISICA = 1f / 60f;
    private static final float K = 55f / 47f;

    private static final float GRAVEDAD_SALTO = 0.6f * K;
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
    // 0 = ninguno
    // 1 = cactus
    // 2 = pájaro
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

    // CONSTRUCTOR
    public GameScreen(Game game, int personaje) {
        this.game = game;
        this.personaje = personaje;
    }

    // INICIALIZAR
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

        cargarRecursos();
    }

    // CARGAR RECURSOS
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

    // RENDER
    @Override
    public void render(float delta) {
        if (mostrandoInstrucciones) {
            tiempoInstrucciones += delta;
            dibujarInstrucciones();

            if (tiempoInstrucciones >= DURACION_INSTRUCCIONES) {
                mostrandoInstrucciones = false;
                tiempoObstaculo = 0;
            }
            return;
        }

        actualizar(delta);
        dibujar();
    }

    // ACTUALIZAR
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

    // SALTO
    private void controlarSalto(float delta) {
        boolean espacio = Gdx.input.isKeyPressed(Input.Keys.SPACE);

        if (
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                && !saltando
                && !agachado
        ) {
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
        float t = (velocidad - VELOCIDAD_INICIAL)
            / (velocidadMaxima - VELOCIDAD_INICIAL);
        float velChrome = VEL_CHROME_MIN + t * (VEL_CHROME_MAX - VEL_CHROME_MIN);

        saltando = true;
        velocidadSalto = VEL_SALTO_BASE + (velChrome / 10f) * K;
        alturaMinimaAlcanzada = false;
        caidaRapida = false;
        acumuladorFisica = 0;
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

        if (altura > ALTURA_MINIMA || caidaRapida) {
            alturaMinimaAlcanzada = true;
        }

        if (altura > ALTURA_MAXIMA || caidaRapida) {
            terminarSalto();
        }

        if (altura <= 0) {
            altura = 0;
            velocidadSalto = 0;
            saltando = false;
            caidaRapida = false;
        }

        yDino = sueloY + altura;
    }

    // AGACHARSE
    private void controlarAgacharse() {
        agachado = Gdx.input.isKeyPressed(Input.Keys.S) && !saltando;
    }

    // MOVER OBSTÁCULOS
    private void moverObstaculos(float delta) {
        float movimiento = velocidad * delta;

        // Obstáculo 1
        if (obstaculo1Activo) {
            xObstaculo1 -= movimiento;

            if (xObstaculo1 < -100) {
                obstaculo1Activo = false;
                sumarPunto();
            }
        }

        // Obstáculo 2
        if (obstaculo2Activo) {
            xObstaculo2 -= movimiento;

            if (xObstaculo2 < -100) {
                obstaculo2Activo = false;
                sumarPunto();
            }
        }
    }

    // SUMAR PUNTO Y VELOCIDAD
    private void sumarPunto() {
        puntos++;

        velocidad = Math.min(
            velocidad + aumentoVelocidad,
            velocidadMaxima
        );

        distanciaMinima = Math.max(
            140,
            distanciaMinima - 5
        );
    }

    // CREAR OBSTÁCULOS
    private void crearObstaculos(float delta) {
        tiempoObstaculo += delta;

        // Espera entre grupos
        if (tiempoObstaculo < 0.9f) {
            return;
        }

        // Espera hasta que no haya obstáculos
        if (obstaculo1Activo || obstaculo2Activo) {
            return;
        }

        tiempoObstaculo = 0;

        // Primer obstáculo
        obstaculo1Activo = true;
        xObstaculo1 = 920;
        tipoObstaculo1 = random.nextInt(2) + 1;

        if (tipoObstaculo1 == 1) {
            cactusLargo1 = random.nextBoolean();
        }

        // 70% de posibilidades de crear un segundo
        if (random.nextFloat() < 0.70f) {
            obstaculo2Activo = true;
            xObstaculo2 = xObstaculo1 + distanciaMinima + random.nextInt(100);
            tipoObstaculo2 = random.nextInt(2) + 1;

            if (tipoObstaculo2 == 1) {
                cactusLargo2 = random.nextBoolean();
            }
        } else {
            obstaculo2Activo = false;
            tipoObstaculo2 = 0;
        }
    }

    // MATRIZ
    private void actualizarMatriz() {
        gameMap.limpiar();
        gameMap.colocarDinosaurio(filaDino, columnaDino);

        if (obstaculo1Activo) {
            colocarEnMatriz(tipoObstaculo1, xObstaculo1);
        }

        if (obstaculo2Activo) {
            colocarEnMatriz(tipoObstaculo2, xObstaculo2);
        }
    }

    private void colocarEnMatriz(int tipo, float x) {
        int columna = (int) (x / 15);

        if (tipo == 1) {
            gameMap.colocarCactus(9, columna);
        } else if (tipo == 2) {
            gameMap.colocarPajaro(6, columna);
        }
    }

    // HITBOXES Y COLISIONES
    private void comprobarColisiones() {
        // DINO
        if (agachado) {
            rectDino.set(
                columnaDino * 15 + 8,
                yDino + 3,
                28,
                30
            );
        } else {
            rectDino.set(
                columnaDino * 15 + 8,
                yDino + 4,
                25,
                47
            );
        }

        // OBSTÁCULO 1
        if (obstaculo1Activo) {
            crearHitbox(
                rectObstaculo1,
                tipoObstaculo1,
                xObstaculo1,
                cactusLargo1
            );

            if (rectDino.overlaps(rectObstaculo1)) {
                colision();
            }
        }

        // OBSTÁCULO 2
        if (obstaculo2Activo) {
            crearHitbox(
                rectObstaculo2,
                tipoObstaculo2,
                xObstaculo2,
                cactusLargo2
            );

            if (rectDino.overlaps(rectObstaculo2)) {
                colision();
            }
        }
    }

    private void crearHitbox(
        Rectangle rect,
        int tipo,
        float x,
        boolean cactusLargo
    ) {
        if (tipo == 1) {
            if (cactusLargo) {
                rect.set(
                    x + anchoCactusLargo * 0.15f,
                    sueloY + altoCactusLargo * 0.05f,
                    anchoCactusLargo * 0.70f,
                    altoCactusLargo * 0.75f
                );
            } else {
                rect.set(
                    x + 12,
                    sueloY + 5,
                    15,
                    25
                );
            }
        } else {
            // HITBOX DEL PÁJARO
            rect.set(
                x + 5,
                sueloY + 50,
                50,
                25
            );
        }
    }

    // DIBUJAR JUEGO
    private void dibujar() {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        dibujarFondo();
        dibujarObjetos();

        font.getData().setScale(1.5f);
        font.draw(
            batch,
            "PUNTOS: " + puntos,
            30,
            ALTO - 40
        );

        batch.end();
    }

    private void dibujarFondo() {
        // FONDO DE DÍA
        if (background != null) {
            batch.setColor(1, 1, 1, 1);
            batch.draw(background, 0, 0, ANCHO, ALTO);
        }

        // TRANSICIÓN AL ATARDECER
        if (
            tiempoFondo >= TIEMPO_CAMBIO_FONDO
                && backgroundAtardecer != null
        ) {
            float progreso =
                (tiempoFondo - TIEMPO_CAMBIO_FONDO) / DURACION_TRANSICION;

            if (progreso > 1) {
                progreso = 1;
            }

            batch.setColor(1, 1, 1, progreso);
            batch.draw(backgroundAtardecer, 0, 0, ANCHO, ALTO);
            batch.setColor(1, 1, 1, 1);
        }
    }

    // DIBUJAR OBJETOS
    private void dibujarObjetos() {
        float xDino = columnaDino * 15;

        if (dino != null) {
            if (agachado) {
                batch.draw(dino, xDino, yDino, 50, 30);
            } else {
                batch.draw(dino, xDino, yDino, 40, 55);
            }
        }

        // OBSTÁCULO 1
        dibujarObstaculo(
            tipoObstaculo1,
            xObstaculo1,
            obstaculo1Activo,
            cactusLargo1
        );

        // OBSTÁCULO 2
        dibujarObstaculo(
            tipoObstaculo2,
            xObstaculo2,
            obstaculo2Activo,
            cactusLargo2
        );

        dibujarPowerUps(xDino);
    }

    private void dibujarObstaculo(
        int tipo,
        float x,
        boolean activo,
        boolean esCactusLargo
    ) {
        if (!activo) {
            return;
        }

        // CACTUS
        if (tipo == 1) {
            if (esCactusLargo && cactusLargo != null) {
                batch.draw(cactusLargo, x, sueloY, anchoCactusLargo, altoCactusLargo);
            } else if (cactus != null) {
                batch.draw(cactus, x, sueloY, 35, 52.9f);
            }
        }
        // PÁJARO
        else if (tipo == 2 && bird != null) {
            batch.draw(bird, x, sueloY + 25, 60, 40);
        }
    }

    // INSTRUCCIONES
    private void dibujarInstrucciones() {
        Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (background != null) {
            batch.draw(background, 0, 0, ANCHO, ALTO);
        }

        GlyphLayout layout = new GlyphLayout();

        dibujarTextoCentrado(layout, "PREPARATE", 2f, 360);
        dibujarTextoCentrado(layout, "ESPACIO = SALTAR", 1.4f, 280);
        dibujarTextoCentrado(layout, "S = AGACHARSE", 1.4f, 220);

        int segundos = (int) Math.ceil(
            DURACION_INSTRUCCIONES - tiempoInstrucciones
        );

        dibujarTextoCentrado(
            layout,
            "Comienza en " + segundos,
            1.2f,
            140
        );

        batch.end();
    }

    private void dibujarTextoCentrado(
        GlyphLayout layout,
        String texto,
        float escala,
        float y
    ) {
        font.getData().setScale(escala);
        layout.setText(font, texto);

        font.draw(
            batch,
            texto,
            (ANCHO - layout.width) / 2,
            y
        );
    }

    private float yPowerUp() {
        return sueloY + ALTURA_POWERUP
            + (float) Math.sin(tiempoAnimacion * 4) * 4;
    }

    private void actualizarPowerUps(float delta) {
        tiempoAnimacion += delta;

        if (!powerUpActivo && hoyuelo == 0 && ollita == 0) {
            tiempoPowerUp += delta;

            if (tiempoPowerUp >= intervaloPowerUp) {
                crearPowerUp();
            }
        }

        if (powerUpActivo) {
            xPowerUp -= velocidad * delta;

            if (xPowerUp < -100) {
                powerUpActivo = false;
            }
        }

        if (hoyueloAbsorbiendo) {
            tiempoHoyuelo += delta;

            if (tiempoHoyuelo >= 1f) {
                hoyuelo = 0;
                hoyueloAbsorbiendo = false;
            }
        }

        if (ollita == 1) {
            tiempoOllita -= delta;

            if (tiempoOllita <= 0) {
                ollita = 0;
            }
        }
    }

    private void crearPowerUp() {
        powerUpActivo = true;
        xPowerUp = 920;
        tipoPowerUp = random.nextBoolean() ? 1 : 2;
        tiempoPowerUp = 0;
        intervaloPowerUp = 12 + random.nextFloat() * 8;
    }

    private void comprobarPowerUps() {
        if (!powerUpActivo) {
            return;
        }

        rectPowerUp.set(
            xPowerUp + 8,
            yPowerUp() + 8,
            TAM_POWERUP - 16,
            TAM_POWERUP - 16
        );

        if (rectDino.overlaps(rectPowerUp)) {
            powerUpActivo = false;
            tiempoPowerUp = 0;

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
        if (terminado || ollita == 1) {
            return;
        }

        if (hoyuelo == 1) {
            if (!hoyueloAbsorbiendo) {
                hoyueloAbsorbiendo = true;
                tiempoHoyuelo = 0;
            }
            return;
        }

        terminado = true;
        gameOver();
    }

    private void dibujarPowerUps(float xDino) {
        if (powerUpActivo) {
            Texture tex = tipoPowerUp == 1 ? texHoyueloTranquilo : texOllita;

            if (tex != null) {
                batch.draw(
                    tex,
                    xPowerUp,
                    yPowerUp(),
                    TAM_POWERUP,
                    TAM_POWERUP
                );
            }
        }

        float anchoDino = agachado ? 50 : 40;
        float altoDino = agachado ? 30 : 55;

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

    // GAME OVER
    private void gameOver() {
        game.setScreen(
            new GameOverScreen(
                game,
                personaje,
                puntos
            )
        );
    }

    // RESIZE
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

    // DISPOSE
    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (font != null) font.dispose();
        if (dino != null) dino.dispose();
        if (cactus != null) cactus.dispose();
        if (cactusLargo != null) cactusLargo.dispose();
        if (bird != null) bird.dispose();
        if (texOllita != null) texOllita.dispose();
        if (texHoyueloTranquilo != null) texHoyueloTranquilo.dispose();
        if (texHoyueloEnojado != null) texHoyueloEnojado.dispose();
        if (background != null) background.dispose();
        if (backgroundAtardecer != null) backgroundAtardecer.dispose();
    }
}
