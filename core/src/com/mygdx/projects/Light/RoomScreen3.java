package com.mygdx.projects.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;

public class RoomScreen3 implements InputInterface, Screen {

    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private ArrayList<Point> oldPoints;
    private ArrayList<Point> points;
    private ArrayList<Section> sections;


    private static class Point extends Vector2 {
        float angle;
        int n, sortN;

        Point(float x, float y, int n) {
            super(x, y);
            angle = (float) Math.atan2(y, x);
            if (angle < 0) angle += MathUtils.PI2;
            this.n = n;
        }

    }

    private void createSection() {
        final int s = points.size();
        sections = new ArrayList<>(s);
        Vector2 nor = new Vector2(0, 0);
        Vector2 v = new Vector2(0, 0);
        for (int i = 0; i < s; i++) {
            Vector2 p1 = points.get(i);
            Vector2 p2 = points.get((i + 1) % s);
            nor.set(p2.y - p1.y, p1.x - p2.x);
            v.set(p1).add(p2);
            if (v.dot(nor) <= 0) sections.add(new Section(p1, p2, new Vector2(nor), i));
        }
    }


    void rayTracing() {
        Section first = sections.get(0);

        Point p = new Point(1, 0, 0);
        p.angle = MathUtils.PI2;
        Vector2 v = new Vector2(0, 0);

        Section nowSection = null;

        int n = 3000;

        LinkedList<Vector2> list = new LinkedList<>();

        final float angle = (float) (2 * Math.PI / (float) n);
        final float cos = (float) Math.cos(angle);
        final float sin = -(float) Math.sin(angle);


        for (int i = 0; i <= n; i++) {
            Section nowSection1 = null;
            int j = 0;
            while (j < sections.size()) {
                Section section = sections.get(j);
                final float k1 = p.crs(section.p1);
                final float k2 = p.crs(section.p2);
                if (k1 * k2 <= 0) {
                    Vector2 vv = section.intersection(k1, k2);
                    if (vv.dot(p) > 0) {
                        v = vv;
                        nowSection1 = section;
                        break;
                    }
                }
                j++;
            }
            if (i > 1 && nowSection1.equals(first)) break;
            if (nowSection1 == null) break;
            if (nowSection == null) {
                nowSection = nowSection1;
                list.addLast(v);
                list.addLast(nowSection.p2);
                p.set(nowSection.p2);
            } else if (nowSection.num == (nowSection1.num - 1)) {
                list.addLast(nowSection1.p2);
                nowSection = nowSection1;
                p.set(nowSection.p2);
            } else {
                Vector2 vv = v;
                if (nowSection.p2.len2() > nowSection1.p1.len2()) {
                    v = nowSection.intersection(nowSection1.p1);
                    list.removeLast();
                    list.addLast(v);
                    list.addLast(nowSection1.p1);
                } else {
                    v = nowSection1.intersection(nowSection.p2);
                    list.removeLast();
                    list.addLast(nowSection.p2);
                    list.addLast(v);
                }
                list.addLast(vv);
                nowSection = nowSection1;
            }
            //поворот вектора
            p.set(p.x * cos - p.y * sin, p.x * sin + p.y * cos);
            p.angle -= angle;
        }
        points = new ArrayList<>();
        for (Vector2 vector2 : list) points.add(new Point(vector2.x, vector2.y, 0));
    }

    static int dopSort(ArrayList<Point> a, int l, int r) {
        double k = a.get(l).angle;
        l--;
        r++;
        while (l < r) {
            do l++; while (a.get(l).angle < k);
            do r--; while (a.get(r).angle > k);
            if (l < r) {
                Point p = a.get(l);
                a.set(l, a.get(r));
                a.set(r, p);
            } else return r;
        }
        return r;
    }

    static void sort(ArrayList<Point> a, int l, int r) {
        if (l < r) {
            int k = dopSort(a, l, r);
            sort(a, l, k);
            sort(a, k + 1, r);
        }
    }

    @Override
    public void addPoint(float x, float y, int b) {
        x = x - camera.viewportWidth / 2 + camera.position.x;
        y = y - camera.viewportHeight / 2 + camera.position.y;
        points.add(new Point(x, y, points.size()));
        oldPoints.add(points.get(points.size() - 1));
    }

    @Override
    public void press() {
        createSection();
        sort(points, 0, points.size() - 1);
        for (int i = 0; i < points.size(); i++) points.get(i).sortN = i;

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new Input(this));

        renderer = new ShapeRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.update();


        points = new ArrayList<>();
        oldPoints = new ArrayList<>();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.circle(0, 0, 3);
        for (int i = 0; i < oldPoints.size(); i++)
            renderer.line(oldPoints.get(i), oldPoints.get((i + 1) % oldPoints.size()));
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

