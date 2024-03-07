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

    public Strawberry() {
        bounds = new Rectangle();
        setSize(50, 50);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!paused) { // Solo mover si no está pausado
            moveBy(-200 * delta, 0);
        }
        bounds.set(getX(), getY(), getWidth(), getHeight());
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
    public void setManager(AssetManager manager) {
        this.manager = manager;
    }


    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
