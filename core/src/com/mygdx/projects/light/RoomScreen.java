package com.mygdx.projects.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;

public class RoomScreen implements Screen, InputInterface {


    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private ArrayList<Vector2> points;
    private ArrayList<Section> sections;


    void rayTracing() {

        ArrayList<Vector2> list = new ArrayList<>();
        Vector2 p = new Vector2(0, 100);
        Vector2 v = new Vector2();
        Section nowSection = null;
        boolean first = false;
        boolean second = false;
        int n = 3600;

        final float angle = -MathUtils.PI2 / (float) n;
        final float cos = (float) Math.cos(angle);
        final float sin = (float) Math.sin(angle);

        int q = 0;
        while (q < sections.size()) {
            v.set(sections.get(q).p1).add(sections.get(q).p2).scl(1 / 2);
            if (v.dot(sections.get(q).nor) > 0) {
                sections.remove(q);
            } else q++;
        }
        for (int i = 0; i <= n; i++) {
            float l = Float.MAX_VALUE;
            Section nowSection1 = null;
            for (Section section : sections) {
                final float k1 = p.crs(section.p1);
                final float k2 = p.crs(section.p2);
                if (k1 * k2 <= 0) {
                    Vector2 vv = section.intersection(p);
                    if (vv.isCollinear(p, 0.005f)) {
                        if (vv.len2() < l) {
                            v = vv;
                            l = vv.len2();
                            nowSection1 = section;
                        }
                    }
                }
            }
            p.set(p.x * cos - p.y * sin, p.x * sin + p.y * cos);
            if (nowSection == null) nowSection = nowSection1;
            if (nowSection.p1.equals(nowSection1.p1) && nowSection.p2.equals(nowSection1.p2)) {
                if (first) {
                    if (second) {
                        list.remove(list.size() - 1);
                        list.add(v);
                    } else {
                        list.add(v);
                        second = true;
                    }
                } else {
                    list.add(v);
                    first = true;
                }
            } else if (nowSection.num == (nowSection1.num - 1) % sections.size()) {
                list.remove(list.size() - 1);
                list.add(nowSection.p2);
                list.add(v);
                first = true;
                second = true;
                nowSection = nowSection1;
            } else {
                Vector2 vv = v;
                v = nowSection.intersection(nowSection1.p1);
                if (v.len2() > nowSection1.p1.len2()) {
                    list.remove(list.size() - 1);
                    list.add(v);
                    list.add(nowSection1.p1);
                } else {
                    v = nowSection1.intersection(nowSection.p2);
                    list.remove(list.size() - 1);
                    list.add(nowSection.p2);
                    list.add(v);
                }
                list.add(vv);
                nowSection = nowSection1;
            }
        }
        points = list;
        createSections();
    }

    void rayTracing1() {
        Vector2 p = new Vector2(points.get(0));
        Vector2 v = new Vector2(0, 0);

        Section nowSection = null;

        boolean second = false;


        int n = 3000;

        LinkedList<Vector2> list = new LinkedList<>();

        final float angle = (float) (2 * Math.PI / (float) n);
        final float cos = (float) Math.cos(angle);
        final float sin = -(float) Math.sin(angle);

        p.set(p.x * cos - p.y * sin, p.x * sin + p.y * cos);

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
            if (nowSection == null) {
                nowSection = nowSection1;
                list.addLast(v);
            } else if (nowSection.p1.equals(nowSection1.p1)) {
                if (second) {
                    list.removeLast();
                    list.addLast(v);
                } else {
                    list.addLast(v);
                    second = true;
                }
            } else if (nowSection.num == (nowSection1.num - 1)) {
                list.removeLast();
                list.addLast(nowSection.p2);
                list.addLast(v);
                second = true;
                nowSection = nowSection1;

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
        }
        points = new ArrayList<>();
        points.addAll(list);

    }

    void rayTracing2() {
        Section first = sections.get(0);

        Vector2 p = new Vector2(points.get(0));
        Vector2 v = new Vector2(0, 0);

        Section nowSection = null;

        int n = 3000;

        LinkedList<Vector2> list = new LinkedList<>();

        final float angle = (float) (2 * Math.PI / (float) n);
        final float cos = (float) Math.cos(angle);
        final float sin = -(float) Math.sin(angle);

        p.set(p.x * cos - p.y * sin, p.x * sin + p.y * cos);

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
        }
        points = new ArrayList<>();
        points.addAll(list);

    }

    int dopSort(ArrayList<Section> a, int l, int r) {
        double k = a.get(l).d2;
        l--;
        r++;
        while (l < r) {
            do l++; while (a.get(l).d2 < k);
            do r--; while (a.get(r).d2 > k);
            if (l < r) {
                Section p = a.get(l);
                a.set(l, a.get(r));
                a.set(r, p);
            } else return r;
        }
        return r;
    }

    void sort(ArrayList<Section> a, int l, int r) {
        if (l < r) {
            int k = dopSort(a, l, r);
            sort(a, l, k);
            sort(a, k + 1, r);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new Input(this));

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.update();

        renderer = new ShapeRenderer();

        points = new ArrayList<>();

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.circle(0, 0, 3);
        for (int i = 0; i < points.size(); i++) renderer.line(points.get(i), points.get((i + 1) % points.size()));
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

    @Override
    public void addPoint(float x, float y, int b) {
        x = x - camera.viewportWidth / 2 + camera.position.x;
        y = y - camera.viewportHeight / 2 + camera.position.y;
        points.add(new Vector2(x, y));
    }

    @Override
    public void press() {
        createSections();
        sort(sections, 0, sections.size() - 1);
        rayTracing2();
    }


    void createSections() {
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


}
