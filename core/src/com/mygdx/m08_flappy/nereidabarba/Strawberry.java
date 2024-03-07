package com.mygdx.m08_flappy.nereidabarba;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Strawberry extends Actor {
    private Rectangle bounds;
    private AssetManager manager;
    private boolean paused; // Nuevo atributo para el estado pausado

    public Strawberry(AssetManager manager) {
        this.manager = manager;
        bounds = new Rectangle();
        setSize(20, 20);
        setVisible(false);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        bounds.set(getX(), getY(), getWidth(), getHeight());
        if (!isVisible()) {
            setVisible(true);
        }
        if (getX() < -64) {
            remove();
        }
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        Texture strawberryTexture = manager.get("strawberry.png", Texture.class);
        batch.draw(strawberryTexture, getX(), getY(), getWidth(), getHeight());
    }


    public Rectangle getBounds() {
        return bounds;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
