package com.mygdx.m08_flappy.nereidabarba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Bird game;
    OrthographicCamera camera;
    boolean dead;
    Stage stage;
    Player player;
    float score;
    Array<Pipe> obstacles;
    long lastObstacleTime;
    private TextButton pauseButton;
    private boolean paused = false;

    public GameScreen(final Bird gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player = new Player();
        player.setManager(game.manager);
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        stage.addActor(player);

        obstacles = new Array<Pipe>();
        spawnObstacle();

        score = 0;

        // Crear skin y botón de pausa
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        pauseButton = new TextButton("Pause", skin);
        pauseButton.setPosition(700, 440); // Posición del botón de pausa
        pauseButton.setSize(100, 30); // Tamaño del botón de pausa
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (paused) {
                    resumeGame();
                } else {
                    pauseGame();
                }
            }
        });
        stage.addActor(pauseButton);
    }

    @Override
    public void render(float delta) {
        dead = false;

        // Lógica de pausa
        if (paused) {
            return; // Si está pausado, no procesar la lógica de actualización
        }

        // Comprueba si el jugador no se sale de la pantalla.
        // Si se sale por la parte inferior, game over
        if (player.getBounds().y > 480 - 45) {
            player.setY(480 - 45);
        }
        if (player.getBounds().y < 0 - 45) {
            dead = true;
        }

        // Comprueba si es necesario generar un nuevo obstáculo
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000) {
            spawnObstacle();
        }

        // Comprueba si las tuberías colisionan con el jugador
        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            }
        }

        // Elimina del array las tuberías que están fuera de pantalla
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }

        // Dibuja la puntuación en la pantalla
        game.batch.begin();
        game.smallFont.draw(game.batch, "Score: " + (int) score, 10, 470);
        game.batch.end();

        // Incrementa la puntuación con el tiempo de juego
        score += Gdx.graphics.getDeltaTime();

        // Si el jugador ha muerto, muestra la pantalla de Game Over
        if (dead) {
            game.manager.get("fail.wav", Sound.class).play();
            game.lastScore = (int) score;
            if (game.lastScore > game.topScore) {
                game.topScore = game.lastScore;
            }
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        // Limpia la pantalla con un color
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);

        // Actualiza la cámara
        camera.update();

        // Establece la matriz de proyección para el batch
        game.batch.setProjectionMatrix(camera.combined);

        // Dibuja el fondo
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class), 0, 0);
        game.batch.end();

        // Dibuja los actores
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

        // Procesa la entrada del usuario
        if (Gdx.input.justTouched()) {
            game.manager.get("flap.wav", Sound.class).play();
            player.impulso();
        }
        stage.act();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

    private void spawnObstacle() {
        // Calcula la altura del obstáculo aleatoriamente
        float holey = MathUtils.random(50, 230);

        // Crea dos obstáculos: Una tubería superior y una inferior
        Pipe pipe1 = new Pipe();
        pipe1.setX(800);
        pipe1.setY(holey - 230);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);
        Pipe pipe2 = new Pipe();
        pipe2.setX(800);
        pipe2.setY(holey + 200);
        pipe2.setUpsideDown(false);
        pipe2.setManager(game.manager);
        obstacles.add(pipe2);
        stage.addActor(pipe2);
        lastObstacleTime = TimeUtils.nanoTime();
    }
}
