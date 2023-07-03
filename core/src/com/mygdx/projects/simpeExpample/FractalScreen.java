package com.mygdx.projects.simpeExpample;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.projects.VoronoiDiagram.Shell;

import java.util.Random;

public class FractalScreen implements Screen {

    Vector2 p1 = new Vector2(0, 0);
    Vector2 p2 = new Vector2(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
    Vector2 p3 = new Vector2(Gdx.graphics.getWidth(), 0);

    int num = 3;

    ShapeRenderer renderer = new ShapeRenderer();

    Array<Vector2> points;
    Array<Vector2> shell;
    Shell s = new Shell();

    @Override
    public void show() {
        points = new Array<>();
        setRandPoint();
        shell = s.getShell(points);
    }

    boolean rend = false;

    Vector2 tmp1 = new Vector2();
    Vector2 tmp2 = new Vector2();

    Random random = new Random();

    Vector2 getPoint(Vector2 from) {
        double d = 3 * random.nextFloat();
        int n = 0;
        if (d < 1 / 3d)
            n = 1;
        else if (d < 2 / 3d)
            n = 2;
        else
            n = 3;
        switch (n) {
            case 1: {
                tmp2.set(p1);
                break;
            }
            case 2: {
                tmp2.set(p2);
                break;
            }
            case 3: {
                tmp2.set(p3);
                break;
            }
        }
        tmp2.sub(from).scl(0.61803f / 8).add(from);
        return tmp2;
    }

    void setRandPoint() {
        points.clear();
        for (int i = 0; i < num; i++) {
            points.add(new Vector2(
                    (random.nextFloat() - 0.5f) * Gdx.graphics.getWidth() / 4 * 3f + Gdx.graphics.getWidth() / 2f,
                    (random.nextFloat() - 0.5f) * Gdx.graphics.getHeight() / 4 * 3f + Gdx.graphics.getHeight() / 2f
            ));
        }
        shell = s.getShell(points);
    }

    Vector2 getRandVertex() {
        return shell.get(random.nextInt(shell.size));
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            setRandPoint();
        }
        if (!rend) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

            renderer.begin(ShapeRenderer.ShapeType.Line);
            {
                for (int i = 0; i < shell.size; i++) {
                    renderer.line(shell.get(i), shell.get((i + 1) % shell.size));
                }
            }
            renderer.end();

            renderer.begin(ShapeRenderer.ShapeType.Point);
            tmp1.set(points.get(0));
            for (int i = 0; i < 10000000; i++) {
                tmp2.set(getRandVertex());
                tmp2.sub(tmp1).scl(0.5f).add(tmp1);
                tmp1.set(tmp2);
                renderer.point(tmp1.x, tmp1.y, 0);
            }
            renderer.end();

        }
//        rend = true;
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
