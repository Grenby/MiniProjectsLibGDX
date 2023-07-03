package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.projects.perlinNoise.noise.FractalNoise;
import com.mygdx.projects.utils.MyInput;

public class Tests implements Screen {
    private final MyInput myInput = new MyInput();

    Texture noise;
    SpriteBatch spriteBatch;

    @Override
    public void show() {
        Gdx.input.setInputProcessor(myInput);
        myInput.addCallback(Input.Keys.ESCAPE, () -> Gdx.app.exit());

        spriteBatch = new SpriteBatch();
        float[][] noise1, noise2, noise3;
        noise1 = WhiteNoise.getWhiteNoise(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        noise2 = WhiteNoise.getWhiteNoise(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        noise3 = WhiteNoise.getWhiteNoise(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
        Color c = new Color();
        //noise1 = PerlinNoiseGenerator.generatePerlinNoise()
        FractalNoise fractalNoise = new FractalNoise();

        for (int i = 0; i < Gdx.graphics.getWidth(); i++) {
            for (int j = 0; j < Gdx.graphics.getHeight(); j++) {
//                float r =noise1[i][j];//(float)((int)(noise1[i][j]+0.5f));
//                float g =noise2[i][j];
//                float b =noise3[i][j];
                float r = fractalNoise.getNoise(i / 256f, j / 256f, 1, 0.5f);
                c.set(r, r, r, 1);
                pixmap.setColor(c);
                pixmap.drawPixel(i, j);
            }
        }
        noise = new Texture(pixmap);
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        spriteBatch.begin();
        spriteBatch.draw(noise, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();

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
