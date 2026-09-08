package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;

public class CharacterScreen implements Screen {

    private final Game game;

    private SpriteBatch batch;
    private BitmapFont font;

    private Texture background;

    private Texture dino1;
    private Texture dino2;
    private Texture dino3;

    private Rectangle personaje1;
    private Rectangle personaje2;
    private Rectangle personaje3;
    private Rectangle volver;

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();

        cargarImagenes();
        crearBotones();
    }

    private void cargarImagenes() {

        try {
            dino1 = new Texture("dino1.png");
        } catch (Exception e) {
            System.out.println("No se pudo cargar dino1.png");
            dino1 = null;
        }

        try {
            dino2 = new Texture("dino2.png");
        } catch (Exception e) {
            System.out.println("No se pudo cargar dino2.png");
            dino2 = null;
        }

        try {
            dino3 = new Texture("dino3.png");
        } catch (Exception e) {
            System.out.println("No se pudo cargar dino3.png");
            dino3 = null;
        }
    }

    private void crearBotones() {

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        // Tamaño de cada zona seleccionable
        float anchoBoton = ancho * 0.22f;
        float altoBoton = alto * 0.42f;

        // Posiciones
        personaje1 = new Rectangle(
            ancho * 0.08f,
            alto * 0.25f,
            anchoBoton,
            altoBoton
        );

        personaje2 = new Rectangle(
            ancho * 0.39f,
            alto * 0.25f,
            anchoBoton,
            altoBoton
        );

        personaje3 = new Rectangle(
            ancho * 0.70f,
            alto * 0.25f,
            anchoBoton,
            altoBoton
        );

        volver = new Rectangle(
            ancho * 0.40f,
            alto * 0.05f,
            ancho * 0.20f,
            alto * 0.12f
        );
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(
            0.1f,
            0.1f,
            0.1f,
            1
        );

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT
        );

        batch.begin();

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        // =========================
        // FONDO
        // =========================

        if (background != null) {

            batch.draw(
                background,
                0,
                0,
                ancho,
                alto
            );
        }

        // =========================
        // TITULO
        // =========================

        font.getData().setScale(2.2f);

        font.draw(
            batch,
            "SELECCIONA TU PERSONAJE",
            ancho * 0.30f,
            alto * 0.88f
        );

        // =========================
        // TAMAÑO DE LOS DINOS
        // =========================

        float tamañoDino = alto * 0.30f;

        // =========================
        // DINO 1
        // =========================

        if (dino1 != null) {

            batch.draw(
                dino1,
                personaje1.x + (personaje1.width - tamañoDino) / 2,
                personaje1.y + personaje1.height * 0.20f,
                tamañoDino,
                tamañoDino
            );
        }

        // =========================
        // DINO 2
        // =========================

        if (dino2 != null) {

            batch.draw(
                dino2,
                personaje2.x + (personaje2.width - tamañoDino) / 2,
                personaje2.y + personaje2.height * 0.20f,
                tamañoDino,
                tamañoDino
            );
        }

        // =========================
        // DINO 3
        // =========================

        if (dino3 != null) {

            batch.draw(
                dino3,
                personaje3.x + (personaje3.width - tamañoDino) / 2,
                personaje3.y + personaje3.height * 0.20f,
                tamañoDino,
                tamañoDino
            );
        }

        // =========================
        // NOMBRES
        // =========================

        font.getData().setScale(1.3f);

        GlyphLayout layout = new GlyphLayout();

        // BLUE
        String nombre1 = "BLUE";
        layout.setText(font, nombre1);

        float xNombre1 =
            personaje1.x + (personaje1.width - layout.width) / 2;

        float yNombre1 =
            personaje1.y + layout.height;

        font.draw(
            batch,
            nombre1,
            xNombre1,
            yNombre1
        );

        // CLASICO
        String nombre2 = "CLASICO";
        layout.setText(font, nombre2);

        float xNombre2 =
            personaje2.x + (personaje2.width - layout.width) / 2;

        float yNombre2 =
            personaje2.y + layout.height;

        font.draw(
            batch,
            nombre2,
            xNombre2,
            yNombre2
        );

        // REX
        String nombre3 = "REX";
        layout.setText(font, nombre3);

        float xNombre3 =
            personaje3.x + (personaje3.width - layout.width) / 2;

        float yNombre3 =
            personaje3.y + layout.height;

        font.draw(
            batch,
            nombre3,
            xNombre3,
            yNombre3
        );
        // =========================
        // VOLVER
        // =========================

        font.getData().setScale(1.3f);

        String textoVolver = "VOLVER";

        layout.setText(font, textoVolver);

        float xVolver =
            volver.x + (volver.width - layout.width) / 2;

        float yVolver =
            volver.y + (volver.height + layout.height) / 2;

        font.draw(
            batch,
            textoVolver,
            xVolver,
            yVolver
        );

        batch.end();

        comprobarClick();
    }
    private void comprobarClick() {

        if (!Gdx.input.justTouched()) {
            return;
        }

        float x = Gdx.input.getX();

        float y =
            Gdx.graphics.getHeight()
                - Gdx.input.getY();

        if (personaje1.contains(x, y)) {

            iniciarJuego(1);

        } else if (personaje2.contains(x, y)) {

            iniciarJuego(2);

        } else if (personaje3.contains(x, y)) {

            iniciarJuego(3);

        } else if (volver.contains(x, y)) {

            game.setScreen(
                new MenuScreen(game)
            );
        }
    }

    private void iniciarJuego(int personaje) {

        game.setScreen(
            new GameScreen(
                game,
                personaje
            )
        );
    }

    @Override
    public void resize(int width, int height) {

        // Recalcular posiciones cuando cambia
        // el tamaño de la ventana o pantalla completa.
        crearBotones();
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

        if (dino1 != null) dino1.dispose();

        if (dino2 != null) dino2.dispose();

        if (dino3 != null) dino3.dispose();
    }

    public CharacterScreen(Game game) {
        this.game = game;
    }
}
