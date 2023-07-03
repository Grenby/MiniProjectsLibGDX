package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class TextureMode implements Screen {
    Texture texture;
    SpriteBatch batch = new SpriteBatch();

    @Override
    public void show() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        Vector3 tmp = new Vector3();

        PerlinNoise noise1 = new PerlinNoise();
        PerlinNoise noise2 = new PerlinNoise();
        PerlinNoise noise3 = new PerlinNoise();


        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                float a = noise1.getNoise(i / 100f, j / 100f, 8, 0.5f);
                float b = noise2.getNoise(i / 100f, j / 100f, 4, 0.5f);
                float c = noise3.getNoise(i / 100f, j / 100f, 1, 0.5f);
                pixmap.setColor(a, b, c, 1);
                pixmap.drawPixel(i, j);
            }
        }

        texture = new Texture(pixmap);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1, false);

        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();
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
