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

    private static final int[] TITULO_VIS = {9, 63, 381, 351};
    private static final int[] REINTENTAR_VIS = {3, 113, 704, 247};
    private static final int[] MENU_VIS = {3, 105, 375, 273};

    private final Game game;

    private final int personaje;
    private final int puntos;

    private SpriteBatch batch;
    private BitmapFont font;
    private GlyphLayout layout;

    private Texture background;
    private Texture gameOverImagen;
    private Texture reintentarImagen;
    private Texture menuImagen;

    private Rectangle reintentar;
    private Rectangle menu;

    private float escalaBoton;

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
                new Texture("game_over.png");

        } catch (Exception e) {

            gameOverImagen = null;
        }

        try {

            reintentarImagen =
                new Texture("reintentar.png");

        } catch (Exception e) {

            reintentarImagen = null;
        }

        try {

            menuImagen =
                new Texture("menu.png");

        } catch (Exception e) {

            menuImagen = null;
        }

        crearBotones();
    }

    private void crearBotones() {

        float ancho = Gdx.graphics.getWidth();
        float alto = Gdx.graphics.getHeight();

        escalaBoton = Math.min(
            ancho * 0.30f / (REINTENTAR_VIS[2] - REINTENTAR_VIS[0]),
            alto * 0.11f / (REINTENTAR_VIS[3] - REINTENTAR_VIS[1])
        );

        reintentar = rectVisible(
            REINTENTAR_VIS,
            ancho / 2,
            alto * 0.27f
        );

        menu = rectVisible(
            MENU_VIS,
            ancho / 2,
            alto * 0.12f
        );
    }

    private Rectangle rectVisible(
        int[] vis,
        float centroX,
        float centroY
    ) {

        float w = (vis[2] - vis[0]) * escalaBoton;
        float h = (vis[3] - vis[1]) * escalaBoton;

        return new Rectangle(
            centroX - w / 2,
            centroY - h / 2,
            w,
            h
        );
    }

    private void dibujarCentrado(
        Texture textura,
        int[] vis,
        float escala,
        float centroX,
        float centroY
    ) {

        float x = centroX - (vis[0] + vis[2]) / 2f * escala;

        float y = centroY
            - (textura.getHeight() - (vis[1] + vis[3]) / 2f) * escala;

        batch.draw(
            textura,
            x,
            y,
            textura.getWidth() * escala,
            textura.getHeight() * escala
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

            float escalaTitulo = Math.min(
                ancho * 0.28f / (TITULO_VIS[2] - TITULO_VIS[0]),
                alto * 0.38f / (TITULO_VIS[3] - TITULO_VIS[1])
            );

            dibujarCentrado(
                gameOverImagen,
                TITULO_VIS,
                escalaTitulo,
                ancho / 2,
                alto * 0.66f
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
            alto * 0.40f
        );

        // Reintentar
        if (reintentarImagen != null) {

            dibujarCentrado(
                reintentarImagen,
                REINTENTAR_VIS,
                escalaBoton,
                reintentar.x + reintentar.width / 2,
                reintentar.y + reintentar.height / 2
            );

        } else {

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
        }

        // Menu Principal
        if (menuImagen != null) {

            dibujarCentrado(
                menuImagen,
                MENU_VIS,
                escalaBoton,
                menu.x + menu.width / 2,
                menu.y + menu.height / 2
            );

        } else {

            font.getData().setScale(2);

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
        }

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

        if (reintentarImagen != null)
            reintentarImagen.dispose();

        if (menuImagen != null)
            menuImagen.dispose();
    }
}
