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
    Stage stage;
    Player player;
    float score;
    Array<Pipe> obstacles;
    long lastObstacleTime;
    private TextButton pauseButton;

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

        Skin skin = new Skin();

        TextureRegion buttonUp = new TextureRegion(new Texture(Gdx.files.internal("button_up.png")));
        TextureRegion buttonDown = new TextureRegion(new Texture(Gdx.files.internal("button_down.png")));


        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(buttonUp);
        buttonStyle.down = new TextureRegionDrawable(buttonDown);

        skin.add("default", buttonStyle);

        // Crear botón con el estilo del skin
        TextButton pauseButton = new TextButton("Pause", skin);
        pauseButton.setPosition(600, 400);
        pauseButton.setSize(75, 50);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new Pausa(game,GameScreen.this));
            }
        });
        stage.addActor(pauseButton);
    }

    @Override
    public void render(float delta) {
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
}
