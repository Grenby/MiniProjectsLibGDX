package com.mygdx.projects.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class LightScreen implements Screen, InputInterface {

    private OrthographicCamera camera;
    private ShapeRenderer renderer;

    private Vector2 light = new Vector2(0, 0);
    private ArrayList<Vector2> points = new ArrayList<>();
    private ArrayList<Section> sections;

    @Override
    public void addPoint(float x, float y, int b) {
        x = x - camera.viewportWidth / 2 + camera.position.x;
        y = y - camera.viewportHeight / 2 + camera.position.y;
        if (b == 0) {
            points.add(new Vector2(x, y));
            if (points.size() > 1) createSections();
        } else if (b == 1) {
            //light.set(x,y);
        }
    }

    @Override
    public void press() {
        createSections();
//        System.out.println(points.get(0));
//        System.out.println(points.get(1));
//        System.out.println(new Vector2(points.get(0).cpy().add(points.get(1))));
//        System.out.println(intersection(points.get(0),points.get(1),new Vector2(points.get(0).cpy().add(points.get(1)))));
        sort3();
    }

    void createSections() {

        sections = new ArrayList<>();
        int s = points.size();
        for (int i = 0; i < s; i++) sections.add(new Section(points.get(i), points.get((i + 1) % s), 0));
    }

    void sort2() {
        int i = 0;
        int s = sections.size();
        Vector2 p, p1, p2, p3;
        p = new Vector2();
        p1 = new Vector2();
        p2 = new Vector2();
        p3 = new Vector2();
        while (i < s) {
            Section section = sections.get(i);
            p.set((section.p1.x + section.p2.x) / 2 - light.x, (section.p1.y + section.p2.y) / 2 - light.y);
            float scl = p.dot(section.nor);
            if (p.x > 0) {
                if (scl > 0) {
                    p1.set(section.p1.x - light.x, section.p1.y - light.y);
                    int n = i;
                    do {
                        n++;
                        section = sections.get(n % s);
                        p.set((section.p1.x + section.p2.x) / 2 - light.x, (section.p1.y + section.p2.y) / 2 - light.y);
                        p2.set(section.p1.x - light.x, section.p1.y - light.y);
                    } while (p.dot(section.nor) > 0);

                    p3.set(section.p2.x - light.x, section.p2.y - light.y);

                    float k1 = p3.crs(p1);
                    float k2 = p3.crs(p2);

                    while (k1 * k2 < 0) {
                        n++;
                        section = sections.get(n % s);
                        p3.set(section.p2.x - light.x, section.p2.y - light.y);
                        k1 = p3.crs(p1);
                        k2 = p3.crs(p2);
                    }


                    int m = i;
                    float l1 = p3.crs(p1);
                    float l2 = p3.crs(p2);
                    p3.set(section.p2.x - light.x, section.p2.y - light.y);

                    while (l1 * l2 < 0) {
                        m--;
                        section = sections.get(m % s);
                        p3.set(section.p2.x - light.x, section.p2.y - light.y);
                        l1 = p3.crs(p1);
                        l2 = p3.crs(p2);
                    }

                    if (sections.get(m % s).length(light) > sections.get(n % s).length(light))
                        section = sections.get(m % s);
                    else section = sections.get(n % s);
                    Vector2 v;
                    if (Math.abs(k1) < Math.abs(k2)) v = section.intersection(p1);
                    else v = section.intersection(p2);
                    if (p1.x > 0) {
                        sections.get(i).setP2(v);
                        section.setP1(v);
                    } else {
                        sections.get(n % s).setP1(v);
                        section.setP2(v);
                    }
                    for (int j = i + 1; j < n; j++) sections.remove((i + 1) % sections.size());
                    s = sections.size();
                } else {
                    i++;
                }
            } else {
                if (scl > 0) {
                    p1.set(section.p1.x - light.x, section.p1.y - light.y);
                    int n = i;
                    do {
                        n++;
                        section = sections.get(n % s);
                        p.set((section.p1.x + section.p2.x) / 2 - light.x, (section.p1.y + section.p2.y) / 2 - light.y);
                        p2.set(section.p1.x - light.x, section.p1.y - light.y);
                    } while (p.dot(section.nor) > 0);

                    p3.set(section.p2.x - light.x, section.p2.y - light.y);

                    float k1 = p3.crs(p1);
                    float k2 = p3.crs(p2);

                    while (k1 * k2 < 0) {
                        n++;
                        section = sections.get(n % s);
                        p3.set(section.p2.x - light.x, section.p2.y - light.y);
                        k1 = p3.crs(p1);
                        k2 = p3.crs(p2);
                    }


                    int m = i;
                    float l1 = p3.crs(p1);
                    float l2 = p3.crs(p2);
                    p3.set(section.p2.x - light.x, section.p2.y - light.y);

                    while (l1 * l2 < 0) {
                        m--;
                        section = sections.get(m % s);
                        p3.set(section.p2.x - light.x, section.p2.y - light.y);
                        l1 = p3.crs(p1);
                        l2 = p3.crs(p2);
                    }

                    if (sections.get(m % s).length(light) > sections.get(n % s).length(light))
                        section = sections.get(m % s);
                    else section = sections.get(n % s);
                    Vector2 v;
                    if (Math.abs(k1) < Math.abs(k2)) v = section.intersection(p1);
                    else v = section.intersection(p2);
                    if (p1.x > 0) {
                        sections.get(i).setP2(v);
                        section.setP1(v);
                    } else {
                        sections.get(n % s).setP1(v);
                        section.setP2(v);
                    }
                    for (int j = i + 1; j < n; j++) sections.remove((i + 1) % sections.size());
                    s = sections.size();
                } else {
                    i++;
                }
            }
        }
        points.clear();
        for (Section s1 : sections) {
            points.add(s1.p1);
        }
    }

    void sort3() {
        int i = 0;
        int sSize = sections.size();
        while (i < sSize) {
            Section section = sections.get(i);
            boolean previousIn = false;
            int j = 0;
            int pSize = points.size();
            while (j < pSize) {
                Vector2 v = points.get(j);
                if (!(v.equals(section.p1) || v.equals(section.p2))) {
                    float dot = v.dot(section.p1);
                    float k1 = v.crs(section.p1);
                    float k2 = v.crs(section.p2);
                    if (dot > 0 && k1 * k2 < 0) {
                        Vector2 intersection = section.intersection(v);
                        if (intersection.len2() < v.len2()) {
                            if (!previousIn) {
                                sections.get(i - 1).setP1(v);
                                sections.get(j - 1).setP2(v);
                            }
                            previousIn = true;
                        }
                    } else {
                        if (previousIn) {
                            sections.get(j - 1).setP1(v);
                            sections.get(i + 1).setP2(v);
                        }
                        previousIn = false;
                    }
                } else {
                    previousIn = true;
                }
                j++;
            }
            i++;
        }
    }

    void sort4() {
        int i = 0;
        int s = sections.size();
        Vector2 p, p1, p2;
        p = new Vector2();
        p1 = new Vector2();
        p2 = new Vector2();
        while (i < s) {
            Section section = sections.get(i);
            p.set((section.p1.x + section.p2.x) / 2, (section.p1.y + section.p2.y) / 2);
            float scl = p.dot(section.nor);
            if (scl < 0) {
                i++;
            } else {
                p1.set(section.p1.x, section.p1.y);
                int n = i;
                do {
                    n++;
                    section = sections.get(n % s);
                    p.set((section.p1.x + section.p2.x) / 2, (section.p1.y + section.p2.y) / 2);
                    p2.set(section.p1.x, section.p1.y);
                } while (p.dot(section.nor) > 0);

                section = new Section(p1, p2, 0);

                p.set((section.p1.x + section.p2.x) / 2, (section.p1.y + section.p2.y) / 2);

                if (p.x > 0) {
                    while (p.dot(section.nor) > 0) {
                        section = new Section(p1, sections.get(n % s).p2, 0);
                        p.set((section.p1.x + section.p2.x) / 2, (section.p1.y + section.p2.y) / 2);
                        n++;
                    }
                    n--;
                    section = sections.get(n % s);
                    Vector2 v = section.intersection(p1);
                    sections.get(i).setP2(v);
                    section.setP1(v);

                    for (int j = i + 1; j < n; j++) sections.remove((i + 1) % sections.size());
                    s = sections.size();
                } else {
                    int m = n - 1;
                    n = i - 1;
                    while (p.dot(section.nor) > 0) {
                        section = new Section(sections.get(n % s).p1, p2, 0);
                        p.set((section.p1.x + section.p2.x) / 2, (section.p1.y + section.p2.y) / 2);
                        n--;
                    }
                    n++;
                    section = sections.get(n % s);
                    Vector2 v = section.intersection(p2);
                    sections.get(m % s).setP1(v);
                    section.setP2(v);

                    for (int j = n + 1; j < m; j++) sections.remove((n + 1) % sections.size());
                    s = sections.size();
                }
            }
        }
        points.clear();
        for (Section s1 : sections) {
            points.add(s1.p1);
        }
    }

    void rayTracing() {
        ArrayList<Vector2> list = new ArrayList<>();
        Vector2 p = new Vector2(0, 100);
        Vector2 v = new Vector2();
        int n = 3600;

        final float angle = -MathUtils.PI2 / (float) n;
        final float cos = (float) Math.cos(angle);
        final float sin = (float) Math.sin(angle);

        for (int i = 0; i <= n; i++) {
            float l = Float.MAX_VALUE;
            for (Section section : sections) {
                final float k1 = p.crs(section.p1);
                final float k2 = p.crs(section.p2);
                if (k1 * k2 < 0) {
                    Vector2 vv = section.intersection(p);
                    if (vv.isCollinear(p, 0.005f)) {
                        if (vv.len2() < l) {
                            v = vv;
                            l = vv.len2();
                        }
                    }
                }
            }
            list.add(v);
            final float xx = p.x * cos - p.y * sin;
            final float yy = p.x * sin + p.y * cos;
            p.set(xx, yy);
            //p.rotateRad(angle);
        }
        points = list;
        createSections();
    }

    void sort5() {

    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.update();
        renderer = new ShapeRenderer();
        Gdx.input.setInputProcessor(new Input(this));

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.setProjectionMatrix(camera.combined);


        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.circle(light.x, light.y, 5);
        if (sections != null) {
            for (Section section : sections) {
                //renderer.circle(section.p1.x, section.p1.y, 3);
                renderer.line(section.p1, section.p2);
                float x = (section.p1.x + section.p2.x) / 2;
                float y = (section.p1.y + section.p2.y) / 2;
                renderer.line(x, y, x + section.nor.x, y + section.nor.y);
            }
        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.GREEN);
        if (points != null && points.size() != 0) renderer.circle(points.get(0).x, points.get(0).y, 3);
        renderer.setColor(Color.WHITE);
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
