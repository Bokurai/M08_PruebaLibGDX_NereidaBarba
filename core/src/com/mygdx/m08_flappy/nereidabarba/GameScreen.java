package com.mygdx.m08_flappy.nereidabarba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Bird game;
    OrthographicCamera camera;
    boolean dead;
    boolean pause;
    Stage stage;
    Player player;
    float score;
    Array<Pipe> obstacles;
    long lastObstacleTime;
    float pausedScore;
    float pausedPlayerX;
    float pausedPlayerY;
    Iterator<Pipe> pausedPipeIterator;
    private ImageButton pauseButton;

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
      
    }

    @Override
    public void render(float delta) {
        pause = false;
        dead = false;

        if (player.getBounds().y > 480 - 45) {
            player.setY(480 - 45);
        }
        if (player.getBounds().y < 0 - 45) {
            dead = true;
        }

        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000) {
            spawnObstacle();
        }

        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            }
        }

        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }

        game.batch.begin();
        game.smallFont.draw(game.batch, "Score: " + (int) score, 10, 470);
        game.batch.end();

        score += Gdx.graphics.getDeltaTime();

        if (dead) {
            game.manager.get("fail.wav", Sound.class).play();
            game.lastScore = (int) score;
            if (game.lastScore > game.topScore) {
                game.topScore = game.lastScore;
            }
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class), 0, 0);
        game.batch.end();
        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();

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
    public void show() {  // Botón de pausa
        Texture pauseButtonTexture = game.manager.get("pause_button.png", Texture.class);
        pauseButton = new ImageButton(pauseButtonTexture);
        pauseButton.setPosition(10, 10);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseGame();
            }
        });
        stage.addActor(pauseButton);
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
        float holey = MathUtils.random(50, 230);
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

     private void pauseGame() {
    pause = true;
    pausedScore = score;
    pausedPlayerX = player.getX();
    pausedPlayerY = player.getY();

    for (Actor actor : stage.getActors()) {
        if (actor instanceof Pipe) {
            Pipe pipe = (Pipe) actor;
            pipe.setPaused(true);
        }
    }

    game.setScreen(new PauseScreen(game, this, pausedScore, pausedPlayerX, pausedPlayerY));
}
}
