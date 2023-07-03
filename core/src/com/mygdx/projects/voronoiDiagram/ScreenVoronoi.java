package com.mygdx.projects.voronoiDiagram;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Random;

public class ScreenVoronoi implements Screen, InputProcessor {

    int w = Gdx.graphics.getWidth();
    int h = Gdx.graphics.getHeight();

    Array<Vector2> vertices = new Array<>();
    Array<Vector2> res;
    ShapeRenderer renderer = new ShapeRenderer();
    boolean start = false;
    boolean end = false;
    ForceBuilder builder = new ForceBuilder();
    Shell shell = new Shell();
    boolean startReloc = false;
    Random random = new Random();

    HashMap<Vector2, DCE> points = new HashMap<>();
    Vector2 tmp1 = new Vector2();
    Vector2 tmp2 = new Vector2();
    Vector2 tmp3 = new Vector2();

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        System.out.println(Gdx.graphics.getWidth());
        builder.getBound().set(20, 20, Gdx.graphics.getWidth() - 40, Gdx.graphics.getHeight() - 40);

/*
points = builder.build(new Array<>(new Vector2[]{
new Vector2(48.0f,441.0f),
new Vector2(145.0f,163.0f),
new Vector2(178.0f,285.0f),
new Vector2(349.0f,175.0f),
new Vector2(397.0f,87.0f)
}));

не работает но мне лень исправлять
 */

        vertices.addAll(new Array<>(new Vector2[]{
                new Vector2(73.43572f, 170.21878f),
                new Vector2(74.913185f, 53.113792f),
                new Vector2(105.88735f, 351.2677f),
                new Vector2(122.36803f, 77.91451f),
                new Vector2(129.29584f, 237.60292f),
                new Vector2(176.20381f, 138.15718f),
                new Vector2(189.5252f, 309.26175f),
                new Vector2(193.18805f, 52.24468f),
                new Vector2(216.77112f, 217.05779f),
                new Vector2(222.44977f, 381.96835f),
                new Vector2(244.30888f, 121.04065f),
                new Vector2(255.82875f, 43.3379f),
                new Vector2(268.43024f, 265.2187f),
                new Vector2(278.72012f, 179.3721f),
                new Vector2(279.50015f, 322.772f),
                new Vector2(309.57376f, 51.745094f),
                new Vector2(327.88974f, 214.21432f),
                new Vector2(328.2197f, 112.30312f),
                new Vector2(355.7138f, 156.79247f),
                new Vector2(357.1043f, 383.03876f),
                new Vector2(359.94717f, 278.8171f),
                new Vector2(390.48508f, 52.350292f),
                new Vector2(398.2277f, 200.61967f),
                new Vector2(409.01624f, 408.77945f),
                new Vector2(410.33636f, 135.79712f),
                new Vector2(410.91138f, 329.19382f),
                new Vector2(446.21164f, 93.42859f),
                new Vector2(452.8364f, 259.66623f),
                new Vector2(470.55823f, 34.205597f),
                new Vector2(473.4648f, 346.76273f),
                new Vector2(474.45084f, 158.38599f),
                new Vector2(479.39755f, 405.03165f),
                new Vector2(514.45734f, 66.49567f),
                new Vector2(520.34503f, 277.49527f),
                new Vector2(520.8908f, 212.87001f),
                new Vector2(551.554f, 371.29913f),
                new Vector2(556.62823f, 150.80557f),
                new Vector2(593.659f, 246.46365f)
        }));
//        //start = true;
    }

    float square(Vector2 v1, Vector2 v2, Vector2 v3) {
        tmp2.set(v2).sub(v1);
        tmp3.sub(v2).sub(v3);
        return Math.abs(tmp2.crs(tmp3)) / 2f;
    }

    Vector2 getCenter(Vector2 v1, Vector2 v2, Vector2 v3) {
        return tmp2.set(v1).add(v2).add(v3).scl(1 / 3f);
    }

    Vector2 getCenter(DCE dce) {
//        for(i=0; i<n; i++)
//        {
//            // используем полученные формулы (**)
//            double s1 =square(xm,ym,x[i],y[i],x[(i+1)%n],y[(i+1)%n]);
//            xc+=s1*(xm+x[i]+x[(i+1)%n])/3;
//            yc+=s1*(ym+y[i]+y[(i+1)%n])/3;
//            s+=s1;
//        }
//        xc/=s; yc/=s;
        Vector2 pos = tmp1;
        pos.set(0, 0);
        int n = 0;
        DCE current = dce;
        current = current.getNext();
        float s = 0;
        do {
            float s1 = square(dce.getFrom(), current.getFrom(), current.getNext().getFrom());
            s += s1;
            pos.mulAdd(getCenter(dce.getFrom(), current.getFrom(), current.getNext().getFrom()), s1);
            n++;
            current = current.getNext();
        } while (current != dce);
        return pos.scl(1.f / s);
    }

    void update(float delta) {
        float velocity = 30;
        startReloc = false;
        for (Vector2 v : vertices) {
            Vector2 pos = getCenter(points.get(v));
            tmp2.set(pos).sub(v);
            if (tmp2.len2() > 0.01)
                startReloc = true;
            tmp2.nor();
            v.mulAdd(tmp2, velocity * delta);
        }
        try {
            points = builder.build(vertices);
        } catch (NullPointerException e) {
            System.out.println(" points = builder.build(new Array<>(new Vector2[]{");
            for (Vector2 v : vertices) {
                System.out.println("new Vector2" + getS(v) + (v == vertices.get(vertices.size - 1) ? "" : ","));
            }
            System.out.println("}));");
            throw e;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if (startReloc) {
            update(delta);
        }

        renderer.begin(ShapeRenderer.ShapeType.Line);
//
        renderer.setColor(Color.WHITE);
        for (int i = 0; i < vertices.size; i++) {
            renderer.circle(vertices.get(i).x, vertices.get(i).y, 5);
        }
        renderer.setColor(Color.GREEN);
        Rectangle r = builder.getBound();
        renderer.rect(r.x, r.y, r.width, r.height);
//        if (end){
//            for (int i = 0; i <res.size ; i++) {
//                renderer.line(res.get(i),res.get(i == res.size -1 ? 0:i+1));
//            }
//        }

        renderer.setColor(Color.WHITE);
        for (Vector2 point : points.keySet()) {
            //renderer.circle(point.x,point.y,10);
            if (start) {
                renderDCE(points.get(point));
            }
        }

        renderer.end();

        if (start && !end) {
            end = true;
            //builder.build(vertices);
            res = shell.getShell(vertices);
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

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys
                    .R: {
                if (vertices.size > 0)
                    vertices.removeIndex(vertices.size - 1);
                break;
            }
            case Input.Keys.T: {
                start = true;
                try {
                    points = builder.build(vertices);
                } catch (NullPointerException e) {
                    System.out.println(" points = builder.build(new Array<>(new Vector2[]{");
                    for (Vector2 v : vertices) {
                        System.out.println("new Vector2" + getS(v) + (v == vertices.get(vertices.size - 1) ? "" : ","));
                    }
                    System.out.println("}));");
                    throw e;
                }
                break;
            }
            case Input.Keys.ESCAPE: {
                Gdx.app.exit();
                break;
            }
            case Input.Keys.Y: {
                end = false;
                start = false;
                break;
            }
            case Input.Keys.P: {

                System.out.println(" points = builder.build(new Array<>(new Vector2[]{");
                for (Vector2 v : vertices) {
                    System.out.println("new Vector2" + getS(v) + (v == vertices.get(vertices.size - 1) ? "" : ","));
                }
                System.out.println("}));");
            }
            case Input.Keys.Q: {
                startReloc = true;
            }
        }
        return false;
    }

    String getS(Vector2 c) {
        return "(" + c.x + "f," + c.y + "f)";
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            vertices.add(new Vector2(screenX, Gdx.graphics.getHeight() - screenY));
            start = false;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    void getRandPoints(int num) {
        vertices.clear();

        for (int i = 0; i < num; i++) {
            vertices.add(new Vector2(random.nextInt(w), random.nextInt(h)));
        }

    }

    void timeTest(boolean p) {
        int num = 10000;
        long time;
        Array<Integer> n = new Array<>(num);
        Array<Double> res = new Array<>(num);

        getRandPoints(100);
        shell.getShell(vertices);


        for (int i = 0; i < num; i += 100) {
            getRandPoints((i + 1000) * 20);
            time = System.nanoTime();

            shell.getShell(vertices);
            shell.getShell(vertices);
            shell.getShell(vertices);
            shell.getShell(vertices);

            time = System.nanoTime() - time;
            n.add(i + 100);
            res.add(time / 1000.0d);
            //System.out.println(i);
        }
        if (p) {
            for (Integer i : n) {
                System.out.print(i + ",");
            }
            System.out.println();
            for (Double d : res) {
                System.out.print(d + ",");
            }
        }

    }

    void renderDCE(DCE dce) {
        DCE current = dce;
        do {
            switch (current.getType()) {
                case Line: {
                    renderLine(current);
                    break;
                }
                case RayIn: {
                    renderRayIn(current);
                    break;
                }
                case RayOut: {
                    renderRayOut(current);
                    break;
                }
                case Segment: {
                    renderer.line(current.getFrom(), current.getTo());
                    break;
                }
            }
            current = current.getNext();
        } while (current != dce);
    }

    void renderLine(DCE dce) {
        tmp2.set(dce.getFrom()).sub(dce.getTo());
        renderer.line(tmp1.set(dce.getFrom()).mulAdd(tmp2, 10), tmp3.set(dce.getFrom()).mulAdd(tmp2, -10));
    }

    void renderRayIn(DCE dce) {
        tmp1.set(dce.getTo());
        tmp2.set(dce.getFrom()).sub(dce.getTo());
        renderer.line(dce.getFrom(), tmp1.mulAdd(tmp2, 1000f));
    }

    void renderRayOut(DCE dce) {
        tmp1.set(dce.getFrom());
        tmp2.set(dce.getTo()).sub(dce.getFrom());
        renderer.line(dce.getFrom(), tmp1.mulAdd(tmp2, 1000f));
    }

}
