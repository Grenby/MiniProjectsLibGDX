package com.mygdx.projects.light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class OldCode implements Screen, InputProcessor {

    private static final Cell cell;
    private static final float lengthRay;
    private static final float sin;
    private static final float cos;
    private static final float epsilon;
    private static final int size;
    private static final int numRay;

    private HashMap<Cell, ArrayList<Line>> map;
    private HashSet<Integer> alreadyInMap;
    private ArrayList<Line> lines;
    private ArrayList<Vector2> ray;

    private ShapeRenderer renderer;
    private Vector2 light;
    private boolean pressing;
    private int width;
    private int height;

    static {
        cell = new Cell(0, 0);
        numRay = 360;
        size = 10;

        epsilon = 0.0001f;
        lengthRay = 100;
        sin = MathUtils.sin(MathUtils.PI2 / numRay);
        cos = MathUtils.cos(MathUtils.PI2 / numRay);
    }

    {
        renderer = new ShapeRenderer();

        map = new HashMap<>();
        alreadyInMap = new HashSet<>();

        lines = new ArrayList<>();
        ray = new ArrayList<>();

        light = new Vector2(100, 100);
        pressing = false;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
    }

    private static class Cell {
        private int x, y;

        Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int hashCode() {
            final int s = x + y;
            return s * (s + 1) + x;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Cell) return this.x == ((Cell) obj).x && this.y == ((Cell) obj).y;
            return false;
        }

        Cell set(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }
    }

    private static class Line {
        Vector2 p1, p2;

        Line(Vector2 p1, Vector2 p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

//        Vector2 intersection(Vector2 p){
//            final float k1=p1.crs(p);
//            final float k2=p.crs(p2);
//            if (k2==0)return p2.cpy();
//            final float x=Math.abs(k1/k2);
//            return new Vector2(p1.x+x*p2.x,p1.y+x*p2.y).scl(1/(1+x));
//        }

        Vector2 intersection(float k1, float k2, float x1, float y1) {
            if (k2 == 0) return p2.cpy().sub(x1, y1);
            final float x = Math.abs(k1 / k2);
            return new Vector2(p1.x - x1 + x * (p2.x - x1), p1.y - y1 + x * (p2.y - y1)).scl(1 / (1 + x));
        }
    }

    private interface CallBack {
        /**
         * @param lines массив линий в данной клетке
         * @return -1 прервать поиск, 0 продолжить
         */
        int callBack(ArrayList<Line> lines);
    }

    private void mapCellLine(Cell cell, Line line) {
        HashMap<Cell, ArrayList<Line>> map = this.map;
        if (!map.containsKey(cell)) {
            ArrayList<Line> lineArrayList = new ArrayList<>();
            map.put(new Cell(cell.x, cell.y), lineArrayList);
            lineArrayList.add(line);
        } else map.get(cell).add(line);
    }

    private void d1(float x1, float y1, float x2, float y2, Line line) {
        if (y2 < y1) {
            float p = y1;
            y1 = y2;
            y2 = p;
            p = x1;
            x1 = x2;
            x2 = p;
        }
        final Vector2 p = new Vector2(x2 - x1, y2 - y1);

        final int finalX = (int) x2;
        final int finalY = (int) y2;

        int x = (int) x1;
        int y = (int) y1;

        mapCellLine(cell.set(x, y), line);

        while (x != finalX || y != finalY) {
            final float px = x - x1;
            final float py = y - y1;
            final float k2 = p.x * (py + 1) - p.y * px;
            if (MathUtils.isEqual(k2, 0, epsilon)) {
                x--;
                y++;
                if (y == finalY && x + 1 == finalX) break;
                mapCellLine(cell.set(x, y), line);
                continue;
            }
            final float k1 = p.x * (py + 1) - p.y * (px + 1);
            final float k3 = p.x * py - p.y * px;
            if (k1 * k2 <= 0) y++;
            else if (k2 * k3 <= 0) x--;
            else throw new RuntimeException(k1 + " " + k2 + " " + k3);
            mapCellLine(cell.set(x, y), line);
        }
    }

    private void d2(float x1, float y1, float x2, float y2, Line line) {
        if (x2 < x1) {
            float p = x1;
            x1 = x2;
            x2 = p;
        }
        if (y2 < y1) {
            float p = y1;
            y1 = y2;
            y2 = p;
        }
        final Vector2 p = new Vector2(x2 - x1, y2 - y1);

        final int finalX = (int) x2;
        final int finalY = (int) y2;
        int x = (int) x1;
        int y = (int) y1;


        mapCellLine(cell.set(x, y), line);

        while (x != finalX || y != finalY) {
            final float px = x - x1;
            final float py = y - y1;
            final float k2 = p.x * (py + 1) - p.y * (px + 1);
            if (MathUtils.isEqual(k2, 0, epsilon)) {
                x++;
                y++;
                mapCellLine(cell.set(x, y), line);
                continue;
            }
            final float k1 = p.x * py - p.y * (px + 1);
            final float k3 = p.x * (py + 1) - p.y * px;
            if (k1 * k2 <= 0) x++;
            else if (k2 * k3 <= 0) y++;
            else throw new RuntimeException(k1 + " " + k2 + " " + k3);
            mapCellLine(cell.set(x, y), line);
        }
    }

    private void getCells(float x1, float y1, float x2, float y2, Line line) {
        x1 /= size;
        x2 /= size;
        y1 /= size;
        y2 /= size;
        if ((x2 - x1) * (y2 - y1) < 0) d1(x1, y1, x2, y2, line);
        else d2(x1, y1, x2, y2, line);
    }

    private void createWall() {
        System.out.println("Old code");
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            touchDown(MathUtils.random(0, width), MathUtils.random(0, height), 0, 0);
            touchDown(MathUtils.random(0, width), MathUtils.random(0, height), 0, 0);
        }
        touchDown(10, 10, 0, 0);
        touchDown(10, height, 10, 0);

        touchDown(10, 10, 0, 0);
        touchDown(width - 10, 10, 0, 0);

        touchDown(width - 10, 10, 0, 0);
        touchDown(width - 10, height - 10, 0, 0);

        touchDown(10, height - 10, 0, 0);
        touchDown(width - 10, height - 10, 0, 0);

        System.out.println(System.currentTimeMillis() - time);

    }

    //Screen interface

    @Override
    public void show() {

        Gdx.input.setInputProcessor(this);


        Vector2 dir = new Vector2(lengthRay, 0);
        for (int i = 0; i < numRay; i++) {
            ray.add(new Vector2(dir));
            dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if (map.size() > 0) {
            renderer.setColor(Color.GRAY);
            for (Cell c : map.keySet()) {
                renderer.rect(c.x * size, c.y * size, size, size);
            }
            renderer.setColor(Color.WHITE);
        }
        renderer.end();

//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        for (int i = 0; i < numRay; i++) {
//            Vector2 v=ray.get(i);
//            //renderer.line(light.x+v.x,light.y+v.y,light.x+ray.get((i+1)%numRay).x,light.y+ray.get((i+1)%numRay).y);
//            renderer.triangle(light.x,light.y,light.x+v.x,light.y+v.y,light.x+ray.get((i+1)%numRay).x,light.y+ray.get((i+1)%numRay).y);
//            //renderer.line(light.x,light.y,light.x+v.x,light.y+v.y);
//            //renderer.circle(light.x+v.x,light.y+v.y,5);
//
//        }
//        renderer.setColor(Color.BLUE);
//        renderer.circle(light.x,light.y,10);
//        renderer.setColor(Color.WHITE);
//        renderer.end();
        renderer.begin(ShapeRenderer.ShapeType.Line);
        if (lines.size() > 0) for (Line l : lines) renderer.line(l.p1, l.p2);
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

    //InputProcessor interface

    @Override
    public boolean keyDown(int keycode) {
        createWall();
        return false;
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
        screenY = height - screenY;
        if (pressing) {
            pressing = false;

            Line line = lines.get(lines.size() - 1);
            Vector2 p1 = line.p1;
            Vector2 p2 = line.p2;
            p2.set(screenX, screenY);

            getCells(p1.x, p1.y, p2.x, p2.y, line);
        } else {
            pressing = true;
            lines.add(new Line(new Vector2(screenX, screenY), new Vector2(screenX, screenY)));
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
        screenY = height - screenY;
        if (pressing) lines.get(lines.size() - 1).p2.set(screenX, screenY);
        else {
            light.set(screenX, screenY);
            Vector2 dir = new Vector2(lengthRay, 0);
            for (int i = 0; i < numRay; i++) {
                // ray.get(i).set(intersection(light,dir));
                dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
            }
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
