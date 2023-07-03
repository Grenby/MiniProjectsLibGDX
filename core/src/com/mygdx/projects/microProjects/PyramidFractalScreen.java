package com.mygdx.projects.microProjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class PyramidFractalScreen implements Screen {

    ShapeRenderer renderer = new ShapeRenderer();
    PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    FirstPersonCameraController controller = new FirstPersonCameraController(camera);


    Vector3 p1 = new Vector3(0, 0, 0);
    Vector3 p2 = new Vector3(0, 10, 0);
    Vector3 p3 = new Vector3(10, 0, 0);
    Vector3 p4 = new Vector3(0, 0, 10);

    Vector3 tmp1 = new Vector3(0, 0, 0);
    Vector3 tmp2 = new Vector3(0, 0, 0);

    Random random = new Random();

    Vector3 getRandVertex() {
        int n = random.nextInt(4) + 1;
        switch (n) {
            case 1:
                return p1;
            case 2:
                return p2;
            case 3:
                return p3;
            case 4:
                return p4;
        }
        return null;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);


        camera.up.set(0, 0, 1);
        camera.direction.set(1, 0, 0).nor();
        camera.position.set(0, 0, 10);
        camera.update();
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.line(p1, p2);
            renderer.line(p2, p3);
            renderer.line(p3, p1);
            renderer.line(p4, p1);
            renderer.line(p4, p2);
            renderer.line(p4, p3);
        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Point);
        {
            tmp1.set(1, 1, 1);
            for (int i = 0; i < 100000; i++) {
                tmp2.set(getRandVertex());
                tmp2.sub(tmp1).scl(0.5f).add(tmp1);
                tmp1.set(tmp2);
                renderer.point(tmp1.x, tmp1.y, tmp1.z);
            }
        }
        renderer.end();
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
