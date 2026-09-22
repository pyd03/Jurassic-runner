package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class MenuScreen implements Screen {

    private final Game game;

    private SpriteBatch batch;

    private Texture background;
    private Texture titulo;
    private Texture jugarImagen;
    private Texture personajesImagen;
    private Texture salirImagen;

    private Rectangle jugar;
    private Rectangle personajes;
    private Rectangle salir;

    public MenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        try {
            background = new Texture("menu_background.png");
        } catch (Exception e) {
            background = null;
        }

        try {
            titulo = new Texture("titulo.png");
        } catch (Exception e) {
            titulo = null;
        }

        try {
            jugarImagen = new Texture("jugar.png");
        } catch (Exception e) {
            jugarImagen = null;
        }

        try {
            personajesImagen = new Texture("pj.png");
        } catch (Exception e) {
            personajesImagen = null;
        }

        try {
            salirImagen = new Texture("salir.png");
        } catch (Exception e) {
            salirImagen = null;
        }

        crearBotones();
    }

    private void crearBotones() {

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        float anchoBoton = ancho * 0.30f;
        float altoBoton = alto * 0.10f;

        float xBoton =
            (ancho - anchoBoton) / 2;

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

        float ancho =
            Gdx.graphics.getWidth();

        float alto =
            Gdx.graphics.getHeight();

        // Fondo
        if (background != null) {

            batch.draw(
                background,
                0,
                0,
                ancho,
                alto
            );
        }

        if (titulo != null) {

            float anchoTitulo = ancho * 0.55f;
            float altoTitulo =
                anchoTitulo *
                    titulo.getHeight() /
                    titulo.getWidth();

            batch.draw(
                titulo,
                (ancho - anchoTitulo) / 2,
                alto * 0.72f,
                anchoTitulo,
                altoTitulo
            );
        }

        if (jugarImagen != null) {

            batch.draw(
                jugarImagen,
                jugar.x,
                jugar.y,
                jugar.width,
                jugar.height
            );
        }

        if (personajesImagen != null) {

            batch.draw(
                personajesImagen,
                personajes.x,
                personajes.y,
                personajes.width,
                personajes.height
            );
        }

        if (salirImagen != null) {

            batch.draw(
                salirImagen,
                salir.x,
                salir.y,
                salir.width,
                salir.height
            );
        }

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
    public void resize(
        int width,
        int height
    ) {
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

        if (batch != null)
            batch.dispose();

        if (background != null)
            background.dispose();

        if (titulo != null)
            titulo.dispose();

        if (jugarImagen != null)
            jugarImagen.dispose();

        if (personajesImagen != null)
            personajesImagen.dispose();

        if (salirImagen != null)
            salirImagen.dispose();
    }
}
