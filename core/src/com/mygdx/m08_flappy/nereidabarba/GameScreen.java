package com.mygdx.m08_flappy.nereidabarba;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Bird game;

    Array<Strawberry> strawberries;
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
    boolean lastObstaclePassed;
    ImageButton pauseButton;
    SpriteBatch batch;
    boolean strawberryAppears;


    public GameScreen(final Bird gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player = new Player();
        player.setManager(game.manager);
        stage = new Stage();
        stage.getViewport().setCamera(camera);
        stage.addActor(player);

        // create the obstacles array and spawn the first obstacle
        strawberries = new Array<Strawberry>();
        obstacles = new Array<Pipe>();
        spawnObstacle();

        strawberryAppears = false;
        score = 0;
        lastObstaclePassed = false;


        batch = new SpriteBatch();
    }

    @Override
    public void render(float delta) {
        if (!pause) {
            dead = false;
            // Comprova que el jugador no es surt de la pantalla.
            // Si surt per la part inferior, game over
            if (player.getBounds().y > 480 - 45)
                player.setY(480 - 45);
            if (player.getBounds().y < 0 - 45) {
                dead = true;
            }
            // Comprova si cal generar un obstacle nou
            if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000) {
                spawnObstacle();
            }

            if (TimeUtils.nanoTime() - lastObstacleTime > 2000000000) {
                strawberryAppears = true;
            }
            // Comprova si les tuberies colisionen amb el jugador
            Iterator<Pipe> iter = obstacles.iterator();
            while (iter.hasNext()) {
                Pipe pipe = iter.next();
                if (pipe.getBounds().overlaps(player.getBounds())) {
                    dead = true;
                }
                //Quan el jugador passa per un obstacle, s'hi suma un punt extra
                else if (pipe.getX() < player.getX() && !lastObstaclePassed) {
                    lastObstaclePassed = true;
                    score += 1;
                }
            }
            // Treure de l'array les tuberies que estan fora de pantalla
            iter = obstacles.iterator();
            while (iter.hasNext()) {
                Pipe pipe = iter.next();
                if (pipe.getX() < -64) {
                    obstacles.removeValue(pipe, true);
                }
            }
            //La puntuació augmenta amb el temps de joc
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

            Iterator<Strawberry> iterator = strawberries.iterator();
            while (iterator.hasNext()) {
                Strawberry strawberry = iterator.next();
                if (strawberry.collideBird(player)) {
                    score += 20;
                    iterator.remove();
                    strawberry.remove();
                    break;
                }
            }

            // clear the screen with a color
            ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
            // tell the camera to update its matrices.
            camera.update();
            // tell the SpriteBatch to render in the
            // coordinate system specified by the camera.
            batch.setProjectionMatrix(camera.combined);
            // begin a new batch
            batch.begin();
            // draw the background
            batch.draw(game.manager.get("background.png", Texture.class), 0, 0);
            // draw the score
            game.smallFont.draw(batch, "Score: " + (int) score, 10, 470);

            batch.end();

            stage.getBatch().setProjectionMatrix(camera.combined);
            stage.draw();


            // process user input
            if (Gdx.input.justTouched()) {
                game.manager.get("flap.wav", Sound.class).play();
                player.impulso();
            }

            if (Gdx.input.isTouched()) {
                //Guardar la posició del player
                float touchX = Gdx.input.getX();
                float touchY = Gdx.input.getY();

                //Transformar aquestes posiccions en posicions de càmara
                touchX = camera.unproject(new Vector3(touchX, touchY, 0)).x;
                touchY = camera.unproject(new Vector3(touchX, touchY, 0)).y;

                //Establir un àrea per el botò
                float pauseButtonX = pauseButton.getX();
                float pauseButtonY = pauseButton.getY();
                float pauseButtonWidth = pauseButton.getWidth();
                float pauseButtonHeight = pauseButton.getHeight();

                //Si la pantalla detecta que s'ha pulsat el botó, inicia el métode de pausar el joc
                if (touchX >= pauseButtonX && touchX <= pauseButtonX + pauseButtonWidth &&
                        touchY >= pauseButtonY && touchY <= pauseButtonY + pauseButtonHeight) {
                    pauseGame();
                }
            }
            stage.act();
        }
    }



    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        Texture pauseButtonTexture = new Texture(Gdx.files.internal("button_up.png"));
        TextureRegionDrawable pauseButtonDrawable = new TextureRegionDrawable(new TextureRegion(pauseButtonTexture));
        ImageButton.ImageButtonStyle pauseButtonStyle = new ImageButton.ImageButtonStyle();
        pauseButtonStyle.up = pauseButtonDrawable;

        pauseButton = new ImageButton(pauseButtonStyle);
        pauseButton.setPosition(10, 350);
        stage.addActor(pauseButton);
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        pauseGame();
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void spawnObstacle() {
        // Calcula la alçada de l'obstacle aleatòriament
        float holey = MathUtils.random(50, 230);
        // Crea dos obstacles: Una tubería superior i una inferior
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
        if(!strawberryAppears){
            float strawberryX = (pipe1.getX() + pipe2.getX() + pipe2.getWidth())/2;
            float strawberryY = (pipe1.getY() + pipe2.getY() + pipe1.getHeight())/2;
            spawnStrawberry(strawberryX, strawberryY);
            strawberryAppears = false;
        }

    }

    private void spawnStrawberry(float x, float y) {
        if (MathUtils.randomBoolean(0.25f)) {
            Strawberry strawberry = new Strawberry();
            strawberry.setX(x);
            strawberry.setY(y);
            strawberry.setManager(game.manager);
            strawberries.add(strawberry);
            stage.addActor(strawberry);
            lastObstacleTime = TimeUtils.nanoTime();
        }
    }


    private void pauseGame() {
        //Reverteix el valor del boolean pause i guarda tant la puntuació com les posiciens en variables
        pause = true;
        pausedScore = score;
        pausedPlayerX = player.getX();
        pausedPlayerY = player.getY();

        //Para l'instanciació de les tuberies
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Pipe) {
                Pipe pipe = (Pipe) actor;
                pipe.setPaused(true);
            }
        }
        //Para el render de la classe
        Gdx.graphics.setContinuousRendering(false);

        //Es pasen les variables a la nova pantalla
        game.setScreen(new PauseScreen(game, this, pausedScore, pausedPlayerY, pausedPlayerX));
    }

    public void resumeGame(float playerX, float playerY, float score) {
        //Recuperem els valos de pausegame() i tornem a canviar el valor del boolean
        this.player.setX(pausedPlayerX);
        this.player.setY(pausedPlayerY);
        this.score = pausedScore;
        this.pause = false;

        //Per fer que les tuberies continuin funcionant
        for (Actor actor : stage.getActors()) {
            if (actor instanceof Pipe) {
                Pipe pipe = (Pipe) actor;
                pipe.setPaused(false);
            }
        }

        //Es renauda el render
        Gdx.graphics.setContinuousRendering(true);
        Gdx.graphics.requestRendering();
    }

}
