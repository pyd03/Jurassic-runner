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
    private Texture dino;
    private Texture cactus;
    private Texture cactusLargo;
    private Texture bird;

    // =========================
    // RESOLUCIÓN Y CÁMARA
    // =========================

    private static final float ANCHO = 900;
    private static final float ALTO = 500;

    private OrthographicCamera camera;
    private Viewport viewport;

    private final float sueloY = 225;

    // =========================
    // DINO
    // =========================

    private final int filaDino = 9;
    private final int columnaDino = 7;

    private float yDino;

    // =========================
    // SALTO Y AGACHARSE
    // =========================

    private boolean saltando = false;
    private boolean agachado = false;

    private float velocidadSalto = 0;

    private final float fuerzaSalto = 420f;
    private final float gravedad = 1100f;

    // =========================
    // OBSTÁCULOS
    // =========================

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

    private float velocidad = 180;
    private final float velocidadMaxima = 400;
    private final float aumentoVelocidad = 10;

    private float tiempoObstaculo = 0;

    private final Random random = new Random();

    // =========================
    // HITBOXES
    // =========================

    private final Rectangle rectDino = new Rectangle();
    private final Rectangle rectObstaculo1 = new Rectangle();
    private final Rectangle rectObstaculo2 = new Rectangle();

    // =========================
    // INSTRUCCIONES
    // =========================

    private final float DURACION_INSTRUCCIONES = 5f;

    private float tiempoInstrucciones = 0;
    private boolean mostrandoInstrucciones = true;

    // =========================
    // CONSTRUCTOR
    // =========================

    public GameScreen(Game game, int personaje) {
        this.game = game;
        this.personaje = personaje;
    }

    // =========================
    // INICIALIZAR
    // =========================

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        gameMap = new GameMap();

        camera = new OrthographicCamera();

        viewport = new FitViewport(
            ANCHO,
            ALTO,
            camera
        );

        viewport.apply();

        camera.position.set(
            ANCHO / 2,
            ALTO / 2,
            0
        );

        camera.update();

        yDino = sueloY;

        cargarRecursos();
    }

    // =========================
    // CARGAR RECURSOS
    // =========================

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
        } catch (Exception e) {
            cactusLargo = null;
        }

        try {
            bird = new Texture("bird.png");
        } catch (Exception e) {
            bird = null;
        }

        try {
            background = new Texture(
                "game_background.png"
            );
        } catch (Exception e) {
            background = null;
        }
    }

    // =========================
    // RENDER
    // =========================

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

    // =========================
    // ACTUALIZAR
    // =========================

    private void actualizar(float delta) {

        controlarSalto(delta);
        controlarAgacharse();
        moverObstaculos(delta);
        crearObstaculos(delta);
        actualizarMatriz();
        comprobarColisiones();
    }

    // =========================
    // SALTO
    // =========================

    private void controlarSalto(float delta) {

        if (
            Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                && !saltando
                && !agachado
        ) {
            saltando = true;
            velocidadSalto = fuerzaSalto;
        }

        if (saltando) {

            float gravedadActual;

            if (
                Gdx.input.isKeyPressed(Input.Keys.SPACE)
                    && velocidadSalto < 250
            ) {
                gravedadActual = gravedad * 0.55f;
            } else {
                gravedadActual = gravedad;
            }

            velocidadSalto -=
                gravedadActual * delta;

            yDino +=
                velocidadSalto * delta;

            if (yDino >= sueloY + 120) {
                yDino = sueloY + 120;
                velocidadSalto = 0;
            }

            if (yDino <= sueloY) {
                yDino = sueloY;
                velocidadSalto = 0;
                saltando = false;
            }
        }
    }

    // =========================
    // AGACHARSE
    // =========================

    private void controlarAgacharse() {

        agachado =
            Gdx.input.isKeyPressed(Input.Keys.S)
                && !saltando;
    }

    // =========================
    // MOVER OBSTÁCULOS
    // =========================

    private void moverObstaculos(float delta) {

        float movimiento =
            velocidad * delta;

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

    // =========================
    // SUMAR PUNTO Y VELOCIDAD
    // =========================

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
    // =========================
    // CREAR OBSTÁCULOS
    // =========================

    private void crearObstaculos(float delta) {

        tiempoObstaculo += delta;

        // Espera entre grupos
        if (tiempoObstaculo < 0.9f) {
            return;
        }

        // Espera hasta que no haya obstáculos
        if (
            obstaculo1Activo
                || obstaculo2Activo
        ) {
            return;
        }

        tiempoObstaculo = 0;

        // Primer obstáculo
        obstaculo1Activo = true;

        xObstaculo1 = 920;

        tipoObstaculo1 =
            random.nextInt(2) + 1;

        if (tipoObstaculo1 == 1) {
            cactusLargo1 = random.nextBoolean();
        }

        // 70% de posibilidades de crear un segundo
        if (random.nextFloat() < 0.70f) {

            obstaculo2Activo = true;

            xObstaculo2 =
                xObstaculo1 + distanciaMinima
                    + random.nextInt(100);

            tipoObstaculo2 =
                random.nextInt(2) + 1;

            if (tipoObstaculo2 == 1) {
                cactusLargo2 = random.nextBoolean();
            }
        } else {

            obstaculo2Activo = false;
            tipoObstaculo2 = 0;
        }
    }

    // =========================
    // MATRIZ
    // =========================

    private void actualizarMatriz() {

        gameMap.limpiar();

        gameMap.colocarDinosaurio(
            filaDino,
            columnaDino
        );

        if (obstaculo1Activo) {

            colocarEnMatriz(
                tipoObstaculo1,
                xObstaculo1
            );
        }

        if (obstaculo2Activo) {

            colocarEnMatriz(
                tipoObstaculo2,
                xObstaculo2
            );
        }
    }

    private void colocarEnMatriz(
        int tipo,
        float x
    ) {

        int columna =
            (int) (x / 15);

        if (tipo == 1) {

            gameMap.colocarCactus(
                9,
                columna
            );

        } else if (tipo == 2) {

            gameMap.colocarPajaro(
                6,
                columna
            );
        }
    }

    // =========================
    // HITBOXES Y COLISIONES
    // =========================

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

            if (
                rectDino.overlaps(
                    rectObstaculo1
                )
            ) {
                gameOver();
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

            if (
                rectDino.overlaps(
                    rectObstaculo2
                )
            ) {
                gameOver();
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
                    x + 10,
                    sueloY + 5,
                    100,
                    38
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
    // =========================
    // DIBUJAR JUEGO
    // =========================

    private void dibujar() {

        Gdx.gl.glClearColor(
            0.1f,
            0.1f,
            0.1f,
            1
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        batch.setProjectionMatrix(
            camera.combined
        );

        batch.begin();

        if (background != null) {

            batch.draw(
                background,
                0,
                0,
                ANCHO,
                ALTO
            );
        }

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

    // =========================
    // DIBUJAR OBJETOS
    // =========================

    private void dibujarObjetos() {

        float xDino = columnaDino * 15;

        if (dino != null) {

            if (agachado) {

                batch.draw(
                    dino,
                    xDino,
                    yDino,
                    50,
                    30
                );

            } else {

                batch.draw(
                    dino,
                    xDino,
                    yDino,
                    40,
                    55
                );
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

            if (
                esCactusLargo
                    && cactusLargo != null
            ) {

                batch.draw(
                    cactusLargo,
                    x,
                    sueloY,
                    120,
                    48
                );

            } else if (cactus != null) {

                batch.draw(
                    cactus,
                    x,
                    sueloY,
                    35,
                    52.9f
                );
            }
        }
        // PÁJARO
        else if (
            tipo == 2
                && bird != null
        ) {

            batch.draw(
                bird,
                x,
                sueloY + 25,
                60,
                40
            );
        }
    }

    // =========================
    // INSTRUCCIONES
    // =========================

    private void dibujarInstrucciones() {

        Gdx.gl.glClearColor(
            0.05f,
            0.05f,
            0.05f,
            1
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        batch.setProjectionMatrix(
            camera.combined
        );

        batch.begin();

        if (background != null) {

            batch.draw(
                background,
                0,
                0,
                ANCHO,
                ALTO
            );
        }

        GlyphLayout layout =
            new GlyphLayout();

        dibujarTextoCentrado(
            layout,
            "PREPARATE",
            2f,
            360
        );

        dibujarTextoCentrado(
            layout,
            "ESPACIO = SALTAR",
            1.4f,
            280
        );

        dibujarTextoCentrado(
            layout,
            "S = AGACHARSE",
            1.4f,
            220
        );

        int segundos =
            (int) Math.ceil(
                DURACION_INSTRUCCIONES
                    - tiempoInstrucciones
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

        layout.setText(
            font,
            texto
        );

        font.draw(
            batch,
            texto,
            (ANCHO - layout.width) / 2,
            y
        );
    }

    // =========================
    // GAME OVER
    // =========================

    private void gameOver() {

        game.setScreen(
            new GameOverScreen(
                game,
                personaje,
                puntos
            )
        );
    }

    // =========================
    // RESIZE
    // =========================

    @Override
    public void resize(
        int width,
        int height
    ) {

        viewport.update(
            width,
            height,
            true
        );
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

    // =========================
    // DISPOSE
    // =========================

    @Override
    public void dispose() {

        if (batch != null)
            batch.dispose();

        if (font != null)
            font.dispose();

        if (dino != null)
            dino.dispose();

        if (cactus != null)
            cactus.dispose();

        if (cactusLargo != null)
            cactusLargo.dispose();

        if (bird != null)
            bird.dispose();

        if (background != null)
            background.dispose();
    }
}
