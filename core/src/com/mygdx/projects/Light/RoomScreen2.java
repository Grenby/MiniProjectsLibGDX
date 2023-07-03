package com.mygdx.projects.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class RoomScreen2 implements InputInterface, Screen {

    private OrthographicCamera camera;
    private ShapeRenderer renderer;

    private ArrayList<Point> points;
    private ArrayList<Point> sortPoints;

    private ArrayList<Section> sections;
    private ArrayList<Section> sortSections;

    private int size = 0;


    private static class Point extends Vector2 implements Comparable<Point> {
        float angle;
        int n, sortN;

        Point(float x, float y, int n) {
            super(x, y);
            if (x < 0) {
                if (y < 0) {
                    float s = crs(Vector2.X);
                    s *= s;
                    s /= len2();
                    angle = -2 + s;
                } else {
                    float s = dot(Vector2.X);
                    s *= s;
                    s /= len2();
                    angle = 1 + s;
                }
            } else {
                if (y < 0) {
                    float s = dot(Vector2.X);
                    s *= s;
                    s /= len2();
                    angle = -1 + s;
                } else {
                    float s = crs(Vector2.X);
                    s *= s;
                    s /= len2();
                    angle = +s;
                }
            }
            this.n = n;
        }

        @Override
        public int compareTo(Point o) {
            return Float.compare(angle, o.angle);
        }
    }

    private static class Section implements Comparable<Section> {

        static Vector2 nor = new Vector2(0, 0);

        Point p1, p2;

        float d2;
        int num;

        boolean isZero = false;

        Section(Point p1, Point p2, int num) {
            this.p1 = p1;
            this.p2 = p2;
            this.num = num;
            nor.set(p1).sub(p2).rotate90(1);
            float k1 = nor.crs(p1);
            float k2 = nor.crs(p2);
            if (k1 * k2 < 0) {
                d2 = -nor.x * p1.x - nor.y * p1.y;
                d2 *= d2;
                d2 /= nor.len2();
            } else d2 = Math.min(p1.len2(), p2.len2());
        }

        Point intersection(Vector2 p) {

            final float k1 = p1.crs(p);
            final float k2 = p.crs(p2);
            if (k2 == 0) return new Point(p2.x, p2.y, 0);
            final float x = Math.abs(k1 / k2);
            Point v = new Point(p1.x + x * p2.x, p1.y + x * p2.y, 1);
            v.scl(1 / (1 + x));
            if (v.x == 0) System.out.println(p);
            return v;
        }

        //выводит k2==0
        Point intersection(float k1, float k2) {
            if (k2 == 0) return new Point(p2.x, p2.y, 0);
            final float x = Math.abs(k1 / k2);
            Point v = new Point(p1.x + x * p2.x, p1.y + x * p2.y, 1);
            v.scl(1 / (1 + x));
            return v;
        }

        @Override
        public String toString() {
            return p1 + " " + p2;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Section) {
                return p1.equals(((Section) obj).p1);
            }
            return false;
        }

        @Override
        public int compareTo(Section o) {
            return Float.compare(this.d2, o.d2);
        }
    }

    private void createSection() {
        for (int i = 0; i < size; i++) {
            Point p1 = points.get(i);
            Point p2 = points.get((i + 1) % size);
            sections.add(new Section(p1, p2, i));
        }
    }

    /**
     * @param p1      точка с меньшим уголм
     * @param p2      точка с большим углом
     * @param p       точка в секторе
     * @param section секция на точках p1 p2
     * @return false -секцию ничего не загораживает.true -перед section есть точка
     */
    private boolean dopStartR(Point p1, Point p2, Point p, Section section) {
        if (section.isZero) return true;
        Vector2 v = section.intersection(p);
        if (v.len2() < p.len2()) {
            /*
            p3 p4 -следующая и предыдущая точки для p
            */
            Point p3 = points.get((p.n + 1) % size);
            Point p4 = points.get(p.n - 1 < 0 ? size - 1 : p.n - 1);
            if (p3.n != p1.n && p3.n != p2.n) {
                Section section1 = sections.get(p.n);
                if (!section1.isZero) {
                    final float k1 = p1.crs(p3);
                    final float k2 = p1.crs(p);
                    final float k3 = p2.crs(p3);
                    final float k4 = p2.crs(p);
                    if (k1 * k2 < 0) section1.p1 = section1.intersection(p1);
                    else if (k3 * k4 < 0) section1.p1 = section1.intersection(p2);
                    else section1.isZero = true;
                    section1.p1.n = p.n;
                }
            } else sections.get(p.n).isZero = true;
            if (p4.n != p1.n && p4.n != p2.n) {
                Section section1 = sections.get(p4.n);
                if (!section1.isZero) {
                    final float k1 = p1.crs(p4);
                    final float k2 = p1.crs(p);
                    final float k3 = p2.crs(p4);
                    final float k4 = p2.crs(p);
                    if (k1 * k2 < 0) section1.p2 = section1.intersection(p1);
                    else if (k3 * k4 < 0) section1.p2 = section1.intersection(p2);
                    else section1.isZero = true;
                    section1.p2.n = p.n;
                }
            } else sections.get(p4.n).isZero = true;
            return false;
        } else return true;
    }


    //p1.angle<p2.angle
    private void startR(Point p1, Point p2, Section section) {
        if (p1.angle * p2.angle < 0 && (p1.dot(Vector2.X) < 0 || p2.dot(Vector2.X) < 0)) {
            for (int i = 0; i < p1.sortN; i++) if (dopStartR(p1, p2, sortPoints.get(i), section)) return;
            for (int i = p2.sortN + 1; i < size; i++) if (dopStartR(p1, p2, sortPoints.get(i), section)) return;
        } else for (int i = p1.sortN + 1; i < p2.sortN; i++) if (dopStartR(p1, p2, sortPoints.get(i), section)) return;
    }

    private void start() {
        for (Section section : sortSections) {
            if (Math.abs(section.p1.x - section.p2.x) < 0.001f && Math.abs(section.p1.y - section.p2.y) < 0.001f) {
                section.isZero = true;
                continue;
            }
            if (!section.isZero) {
                Point p1 = section.p1;
                Point p2 = section.p2;
                if (p1.angle > p2.angle) startR(p2, p1, section);
                else startR(p1, p2, section);
            }

        }
        points = new ArrayList<>();
        for (Section section : sections) {
            if (!section.isZero) {
                points.add(new Point(section.p1.x, section.p1.y, section.num));
                points.add(new Point(section.p2.x, section.p2.y, section.num));
            }
        }
    }


    @Override
    public void addPoint(float x, float y, int b) {
        x = x - camera.viewportWidth / 2 + camera.position.x;
        y = y - camera.viewportHeight / 2 + camera.position.y;
        points.add(new Point(x, y, points.size()));
    }

    int q = 0;

    @Override
    public void press() {
        size = points.size();

        sections = new ArrayList<>(size);
        sortSections = new ArrayList<>();

        createSection();

        sortPoints.addAll(points);
        sortSections.addAll(sections);

        sortSections.sort(Section::compareTo);
        sortPoints.sort(Point::compareTo);

        for (int i = 0; i < size; i++) sortPoints.get(i).sortN = i;
        start();
    }


    private void createRoom(int n) {
        float k = 1f;
        switch (n) {
            case 1: {
                addPoint(100 * k, 100 * k, 0);
                addPoint(100 * k, -200 * k, 0);
                addPoint(300 * k, -200 * k, 0);
                addPoint(300 * k, 200 * k, 0);
                addPoint(-100 * k, 200 * k, 0);
                addPoint(-100 * k, 400 * k, 0);
                addPoint(500 * k, 400 * k, 0);
                addPoint(500 * k, -400 * k, 0);
                addPoint(-100 * k, -400 * k, 0);
                addPoint(-100 * k, 100 * k, 0);
                break;
            }
            case 2: {
                addPoint(100 * k, 100 * k, 0);
                addPoint(100 * k, -200 * k, 0);
                addPoint(400 * k, -200 * k, 0);
                addPoint(400 * k, -500 * k, 0);
                addPoint(-300 * k, -500 * k, 0);
                addPoint(-300 * k, -200 * k, 0);
                addPoint(-100 * k, -200 * k, 0);
                addPoint(-100 * k, 100 * k, 0);
                break;
            }
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new Input(this));

        renderer = new ShapeRenderer();

        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 0);
        camera.update();


        sortPoints = new ArrayList<>();
        points = new ArrayList<>();

        //createRoom(1);

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
}
