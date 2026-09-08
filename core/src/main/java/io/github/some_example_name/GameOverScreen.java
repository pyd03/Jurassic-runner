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

public class GameOverScreen implements Screen {

    private final Game game;

    private final int personaje;
    private final int puntos;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    private Texture background;

    private Rectangle reintentar;
    private Rectangle menu;

    public GameOverScreen(
        Game game,
        int personaje,
        int puntos) {

        this.game = game;
        this.personaje = personaje;
        this.puntos = puntos;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        font = new BitmapFont();

        layout = new GlyphLayout();

        // =========================
        // FONDO
        // =========================

        try {

            background =
                new Texture("gameover_background.png");

        } catch (Exception e) {

            System.out.println(
                "No se pudo cargar gameover_background.png"
            );

            background = null;
        }

        crearBotones();
    }

    // =========================
    // CREAR BOTONES
    // =========================

    private void crearBotones() {

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        float anchoBoton = ancho * 0.30f;
        float altoBoton = alto * 0.10f;

        float xBoton =
            (ancho - anchoBoton) / 2;

        reintentar = new Rectangle(
            xBoton,
            alto * 0.42f,
            anchoBoton,
            altoBoton
        );

        menu = new Rectangle(
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

        float ancho =
            Gdx.graphics.getWidth();

        float alto =
            Gdx.graphics.getHeight();

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
        // GAME OVER
        // =========================

        font.getData().setScale(3);

        String titulo = "GAME OVER";

        layout.setText(font, titulo);

        float xTitulo =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            titulo,
            xTitulo,
            alto * 0.80f
        );

        // =========================
        // PUNTOS
        // =========================

        font.getData().setScale(1.5f);

        String textoPuntos =
            "PUNTOS: " + puntos;

        layout.setText(
            font,
            textoPuntos
        );

        float xPuntos =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            textoPuntos,
            xPuntos,
            alto * 0.65f
        );

        // =========================
        // REINTENTAR
        // =========================

        String textoReintentar =
            "REINTENTAR";

        layout.setText(
            font,
            textoReintentar
        );

        float xReintentar =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            textoReintentar,
            xReintentar,
            reintentar.y
                + reintentar.height * 0.65f
        );

        // =========================
        // MENU PRINCIPAL
        // =========================

        String textoMenu =
            "MENU PRINCIPAL";

        layout.setText(
            font,
            textoMenu
        );

        float xMenu =
            (ancho - layout.width) / 2;

        font.draw(
            batch,
            textoMenu,
            xMenu,
            menu.y
                + menu.height * 0.65f
        );

        batch.end();

        comprobarClick();
    }

    // =========================
    // CLICK
    // =========================

    private void comprobarClick() {

        if (!Gdx.input.justTouched()) {
            return;
        }

        float x =
            Gdx.input.getX();

        float y =
            Gdx.graphics.getHeight()
                - Gdx.input.getY();

        if (reintentar.contains(x, y)) {

            game.setScreen(
                new GameScreen(
                    game,
                    personaje
                )
            );

        } else if (menu.contains(x, y)) {

            game.setScreen(
                new MenuScreen(game)
            );
        }
    }

    // =========================
    // RESIZE
    // =========================

    @Override
    public void resize(
        int width,
        int height) {

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

    // =========================
    // DISPOSE
    // =========================

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
