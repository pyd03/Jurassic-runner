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

public class MenuScreen implements Screen {

    private final Game game;

    private SpriteBatch batch;
    private BitmapFont font;

    private Texture background;

    private Rectangle jugar;
    private Rectangle personajes;
    private Rectangle salir;

    private GlyphLayout layout;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();
        font = new BitmapFont();
        layout = new GlyphLayout();

        try {
            background = new Texture("menu_background.png");
        } catch (Exception e) {
            System.out.println("No se pudo cargar menu_background.png");
            background = null;
        }

        crearBotones();
    }

    private void crearBotones() {

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        float anchoBoton = ancho * 0.30f;
        float altoBoton = alto * 0.10f;

        float xBoton = (ancho - anchoBoton) / 2;

        jugar = new Rectangle(
            xBoton,
            alto * 0.55f,
            anchoBoton,
            altoBoton
        );

        personajes = new Rectangle(
            xBoton,
            alto * 0.40f,
            anchoBoton,
            altoBoton
        );

        salir = new Rectangle(
            xBoton,
            alto * 0.25f,
            anchoBoton,
            altoBoton
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

        // =========================
        // FONDO
        // =========================

        if (background != null) {

            batch.draw(
                background,
                0,
                0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            );
        }

        // =========================
        // TITULO
        // =========================

        font.getData().setScale(2);

        String titulo = "JURASSIC RUNNER";

        layout.setText(font, titulo);

        float xTitulo =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            titulo,
            xTitulo,
            Gdx.graphics.getHeight() * 0.85f
        );

        // =========================
        // INICIAR
        // =========================

        font.getData().setScale(1.5f);

        layout.setText(font, "INICIAR");

        float xIniciar =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            "INICIAR",
            xIniciar,
            jugar.y + jugar.height * 0.65f
        );

        // =========================
        // PERSONAJES
        // =========================

        layout.setText(font, "PERSONAJES");

        float xPersonajes =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            "PERSONAJES",
            xPersonajes,
            personajes.y + personajes.height * 0.65f
        );

        // =========================
        // SALIR
        // =========================

        layout.setText(font, "SALIR");

        float xSalir =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            "SALIR",
            xSalir,
            salir.y + salir.height * 0.65f
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

        if (jugar.contains(x, y)) {

            // Personaje predeterminado
            game.setScreen(
                new GameScreen(game, 1)
            );

        } else if (personajes.contains(x, y)) {

            game.setScreen(
                new CharacterScreen(game)
            );

        } else if (salir.contains(x, y)) {

            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {

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

        if (batch != null) {
            batch.dispose();
        }

        if (font != null) {
            font.dispose();
        }

        if (background != null) {
            background.dispose();
        }
    }
}
