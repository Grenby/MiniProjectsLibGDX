package com.mygdx.projects.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSCounter;

public class TestScreen1 implements Screen {

    private static int WIDTH = Gdx.graphics.getWidth();
    private static int HEIGHT = Gdx.graphics.getHeight();

    Stage stage;
    Group group;

    BitmapFont font = Resource.getFont();
    SpriteBatch batch = new SpriteBatch();
    ShapeRenderer renderer = new ShapeRenderer();

    FPSCounter counter = new FPSCounter();

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        counter.update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        font.draw(batch, Float.toString(counter.getFps()), 0, HEIGHT);
        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
        renderer.rect(20, 20, 100, 30);
        renderer.end();

    }

    @Override
    public void resize(int width, int height) {
        WIDTH = width;
        HEIGHT = height;
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
