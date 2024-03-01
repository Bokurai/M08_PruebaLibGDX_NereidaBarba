package com.mygdx.m08_flappy.nereidabarba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Pausa implements Screen {

    final Bird game;
    OrthographicCamera camera;
    Stage stage;

    GameScreen gameScreen;

    public  Pausa(final Bird gam, GameScreen gameScreen){
        this.game = gam;
        this.gameScreen = gameScreen;
    }
    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.8f, 0.8f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (Gdx.input.justTouched()) {
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

    }
}
