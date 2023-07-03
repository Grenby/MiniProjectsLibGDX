package com.mygdx.projects.rayMarching.dimention3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;


public class RayM3Screen implements Screen {

    private int width = Gdx.graphics.getWidth();
    private int height = Gdx.graphics.getHeight();

    private PerspectiveCamera camera = new PerspectiveCamera(67, width, height);

    private ShapeRenderer renderer = new ShapeRenderer();

    private ArrayList<Figure> list = new ArrayList<>();
    private Vector3 tmp1 = new Vector3(),
            tmp3 = new Vector3(),
            tmp2 = new Vector3(),
            light = new Vector3(200, 400, 0);
    int step = 5;
    Figure choose = null;
    Vector2 posM = null;

    void addFigure(Figure figure) {
        list.add(figure);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new Input(this));
        camera.position.set(-100, 100, 0);
        camera.lookAt(200, 0, 0);
        camera.update();
        //addFigure(new Sphere(new Vector3(200,0,0),100).setColor(Color.GREEN));
        //addFigure(new Sphere(new Vector3(200,50,50),100).setColor(Color.BLUE));
        addFigure(new Sphere(new Vector3(200, 0, 0), 100));
    }

    void moved(float x, float y) {
        y = height / 2f - y;
        x = width / 2f - x;
        if (posM == null) posM = new Vector2(x, y);

        if (choose != null) {
            Vector3 p = new Vector3(camera.direction).crs(camera.up).nor().scl(x);
            choose.position().set(camera.position).add(p);
        } else {
            x /= 50;
            y /= 50;
            Vector3 w = new Vector3(camera.direction);

        }
        posM.set(x, y);
    }

    void choose(float x, float y, int b) {
        y = height / 2f - y;
        x = width / 2f - x;
        Vector3 p = new Vector3(camera.direction).crs(camera.up).nor().scl(x);
        p.add(camera.direction);
        p.add(camera.up.x * y, camera.up.y * y, camera.up.z * y);
        if (b == 0) {
            Figure f = rayCast(p.nor());
            if (f == choose) choose = null;
            else choose = f;
        } else {
            p.nor().scl(100);
            camera.direction.set(p);
            camera.update();
        }
    }

    private Figure rayCast(Vector3 direction) {
        Figure f = null;
        Vector3 pos = camera.position.cpy();
        float distance = 100;
        for (int k = 0; k < 10; k++) {
            distance = list.get(0).distance(pos);
            f = list.get(0);
            for (int j = 1; j < list.size(); j++) {
                float dopDistance = list.get(j).distance(pos);
                if (distance > dopDistance) {
                    f = list.get(j);
                    distance = dopDistance;
                }
            }
            pos.add(direction.x * distance, direction.y * distance, direction.z * distance);
        }
        if (distance > 10) return null;
        return f;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.begin(ShapeRenderer.ShapeType.Point);

        tmp1.set(camera.direction);
        tmp2.set(camera.direction).crs(camera.up).nor();

        tmp3.set(camera.position);

        tmp1.
                add(tmp2.x * width / 2, tmp2.y * width / 2, tmp2.z * width / 2).
                sub(camera.up.x * height / 2, camera.up.y * height / 2, camera.up.z * height / 2);

        float distance0 = list.get(0).distance(tmp3);
        for (int j = 1; j < list.size(); j++) {
            float dopDistance = list.get(j).distance(tmp3);
            distance0 = distance0 < dopDistance ? distance0 : dopDistance;
        }
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                float l = tmp1.len();
                tmp3.set(camera.position).add(tmp1.x * distance0 / l, tmp1.y * distance0 / l, tmp1.z * distance0 / l);
                Figure f = null;
                float distance = 100;
                for (int k = 0; k < 10; k++) {
                    distance = list.get(0).distance(tmp3);
                    f = list.get(0);
                    for (int j = 1; j < list.size(); j++) {
                        float dopDistance = list.get(j).distance(tmp3);
                        if (distance > dopDistance) {
                            f = list.get(j);
                            distance = dopDistance;
                        }
                    }
                    distance /= l;
                    tmp3.add(tmp1.x * distance, tmp1.y * distance, tmp1.z * distance);
                }
                if (distance * l > 10) renderer.setColor(Color.GRAY);
                else {
                    Color color = f.getColor().cpy();
                    renderer.setColor(color.mul((1.1f + light.dot(tmp3.sub(f.position())) / light.len() / tmp3.len())));
                }
                renderer.point(x, y, 0);
                tmp1.add(camera.up);
            }
            tmp1.sub(camera.up.x * height, camera.up.y * height, camera.up.z * height);
            tmp1.sub(tmp2);
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
