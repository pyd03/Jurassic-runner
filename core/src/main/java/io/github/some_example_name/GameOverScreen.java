package io.github.some_example_name;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class GameOverScreen implements Screen {

    private final Game game;

    private final int personaje;
    private final int puntos;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    private Texture background;
    private Texture gameOverImagen;

    private Rectangle reintentar;
    private Rectangle menu;

    public GameOverScreen(
        Game game,
        int personaje,
        int puntos
    ) {

        this.game = game;
        this.personaje = personaje;
        this.puntos = puntos;
    }

    @Override
    public void show() {

        batch = new SpriteBatch();

        font = new BitmapFont();

        layout = new GlyphLayout();

        try {

            background =
                new Texture("gameover_background.png");

        } catch (Exception e) {

            System.out.println(
                "No se pudo cargar gameover_background.png"
            );

            background = null;
        }

        try {

            gameOverImagen =
                new Texture("gameover.png");

        } catch (Exception e) {

            gameOverImagen = null;
        }

        crearBotones();
    }

    private void crearBotones() {

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        float anchoBoton =
            ancho * 0.30f;

        float altoBoton =
            alto * 0.10f;

        float xBoton =
            (ancho - anchoBoton) / 2;

        reintentar = new Rectangle(
            xBoton,
            alto * 0.30f,
            anchoBoton,
            altoBoton
        );

        menu = new Rectangle(
            xBoton,
            alto * 0.15f,
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

        // Game Over
        if (gameOverImagen != null) {

            float anchoTitulo =
                ancho * 0.35f;

            float altoTitulo =
                anchoTitulo *
                    gameOverImagen.getHeight() /
                    gameOverImagen.getWidth();

            batch.draw(
                gameOverImagen,
                (ancho - anchoTitulo) / 2,
                alto * 0.72f,
                anchoTitulo,
                altoTitulo
            );

        } else {

            font.getData().setScale(3);

            String titulo =
                "GAME OVER";

            layout.setText(
                font,
                titulo
            );

            font.draw(
                batch,
                titulo,
                (ancho - layout.width) / 2,
                alto * 0.80f
            );
        }

        // Puntos
        font.getData().setScale(1.5f);

        String textoPuntos =
            "PUNTOS: " + puntos;

        layout.setText(
            font,
            textoPuntos
        );

        font.draw(
            batch,
            textoPuntos,
            (ancho - layout.width) / 2,
            alto * 0.55f
        );

        // Reintentar
        font.getData().setScale(2);

        String textoReintentar =
            "REINTENTAR";

        layout.setText(
            font,
            textoReintentar
        );

        font.draw(
            batch,
            textoReintentar,
            reintentar.x +
                (reintentar.width - layout.width) / 2,
            reintentar.y +
                (reintentar.height + layout.height) / 2
        );

        // Menu Principal
        String textoMenu =
            "MENU PRINCIPAL";

        layout.setText(
            font,
            textoMenu
        );

        font.draw(
            batch,
            textoMenu,
            menu.x +
                (menu.width - layout.width) / 2,
            menu.y +
                (menu.height + layout.height) / 2
        );

        batch.end();

        comprobarClick();
    }

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

        if (font != null)
            font.dispose();

        if (background != null)
            background.dispose();

        if (gameOverImagen != null)
            gameOverImagen.dispose();
    }
}
