package com.mygdx.projects.simpeExpample;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.Resource;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SmoothHexagonScreen implements Screen {

    SpriteBatch batch = new SpriteBatch();
    ShapeDrawer drawer = new ShapeDrawer(batch, Resource.getUISkin().getRegion("default-window"));
    Polygon polygon = new Polygon();

    @Override
    public void show() {
        polygon.setVertices(new float[]{0, 0, 100, 0, 100, 100, 0, 100});
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK, true);

        batch.begin();
        {
            drawer.polygon(polygon, 10, JoinType.SMOOTH);
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
