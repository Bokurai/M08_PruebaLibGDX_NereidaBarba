package com.mygdx.m08_flappy.nereidabarba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PauseScreen implements Screen {
    private final Bird game;
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private GameScreen gameScreen;
    private float playerX;
    private float playerY;
    private float score;

    public PauseScreen(final Bird game, GameScreen gameScreen, float playerX, float playerY, float score) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.playerX = playerX;
        this.playerY = playerY;
        this.score = score;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        game.bigFont.draw(game.batch, "Welcome to Puig Bird!!! ", 50, 300);
        batch.end();

        if (Gdx.input.justTouched()) {
            gameScreen.resumeGame(playerX, playerY, score);
            game.setScreen(gameScreen);
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
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
        batch.dispose();
    }
}
