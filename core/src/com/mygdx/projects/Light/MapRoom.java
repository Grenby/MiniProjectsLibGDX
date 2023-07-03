package com.mygdx.projects.Light;

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

public class MapRoom implements Screen, InputProcessor {

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
        size = 1;

        epsilon = 0.0001f;
        lengthRay = 500;
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

    private interface TheEnd {
        boolean end(float x1, float y1);
    }

    private void mapCellLine(Cell cell, Line line) {
        HashMap<Cell, ArrayList<Line>> map = this.map;
        if (!map.containsKey(cell)) {
            ArrayList<Line> lineArrayList = new ArrayList<>();
            map.put(new Cell(cell.x, cell.y), lineArrayList);
            lineArrayList.add(line);
        } else map.get(cell).add(line);
    }

    private void k(float x1, float y1, float x2, float y2, Line line) {
        if (Math.abs(x2 - x1) > 0.5f || Math.abs(y2 - y1) > 0.5f) {
            final float x12 = (x1 + x2) / 2;
            final float y12 = (y1 + y2) / 2;
            k(x1, y1, x12, y12, line);
            k(x12, y12, x2, y2, line);
        } else {
            final int xx1 = (int) (x1 / size);
            final int yy1 = (int) (y1 / size);
            final int xx2 = (int) (x2 / size);
            final int yy2 = (int) (y2 / size);
            int hash = cell.set(xx1, yy1).hashCode();
            if (alreadyInMap.add(hash)) mapCellLine(cell, line);
            hash = cell.set(xx2, yy2).hashCode();
            if (alreadyInMap.add(hash)) mapCellLine(cell.set(xx2, yy2), line);
        }
    }

    private void controlK(float x1, float y1, float x2, float y2, Line line) {
        alreadyInMap.clear();
        k(x1, y1, x2, y2, line);
    }

    private void intersectionK(CallBack callBack, TheEnd end, float x1, float y1, float deltaX, float deltaY) {
        if (!end.end(x1, y1)) return;
        int hash = cell.set((int) (x1 / size), (int) (y1 / size)).hashCode();
        if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1) return;
        else {
            if (deltaX > 0) {
                if (deltaY > 0) {
                    hash = cell.set(cell.x + 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x, cell.y + 1).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x - 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                } else {
                    hash = cell.set(cell.x + 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x + 1, cell.y - 1).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x - 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                }
            } else {
                if (deltaY > 0) {
                    hash = cell.set(cell.x - 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x, cell.y + 1).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x + 1, cell.y + 1).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                } else {
                    hash = cell.set(cell.x - 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x, cell.y - 1).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                    hash = cell.set(cell.x + 1, cell.y).hashCode();
                    if (map.containsKey(cell) && alreadyInMap.add(hash) && callBack.callBack(map.get(cell)) == -1)
                        return;
                }
            }
        }
        intersectionK(callBack, end, x1 + deltaX, y1 + deltaY, deltaX, deltaY);
    }

    private Vector2 intersection(Vector2 start, Vector2 dir) {
        Vector2 v = new Vector2(0, 0);

        CallBack callBack = lines -> {
            float l = Float.MAX_VALUE;
            boolean k = false;
            for (Line line : lines) {
                final float k1 = (line.p1.x - start.x) * dir.y - (line.p1.y - start.y) * dir.x;
                final float k2 = (line.p2.y - start.y) * dir.x - (line.p2.x - start.x) * dir.y;
                if (k1 * k2 > 0) {
                    Vector2 vv = line.intersection(k1, k2, start.x, start.y);
                    if (vv.len2() < l) {
                        v.set(vv);
                        l = vv.len2();
                        k = true;
                    }
                }
            }
            return k ? -1 : 0;
        };

        TheEnd end;

        float x2 = start.x + dir.x;
        float y2 = start.y + dir.y;

        if (dir.x > 0) {
            if (dir.y > 0) end = (x1, y1) -> x1 < x2 || y1 < y2;
            else end = (x1, y1) -> x1 < x2 || y1 > y2;
        } else {
            if (dir.y > 0) end = (x1, y1) -> x1 > x2 || y1 < y2;
            else end = (x1, y1) -> x1 > x2 || y1 > y2;
        }

        final float k = size / (2 * lengthRay);
        final float deltaX = dir.x * k;
        final float deltaY = dir.y * k;

        alreadyInMap.clear();
        intersectionK(callBack, end, start.x, start.y, deltaX, deltaY);


        if (v.isZero()) return dir;
        return v.len2() <= dir.len2() ? v : dir;
    }

    private void createWall() {
        System.out.println("Map room");
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
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

        createWall();

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

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < numRay; i++) {
            Vector2 v = ray.get(i);
            //renderer.line(light.x+v.x,light.y+v.y,light.x+ray.get((i+1)%numRay).x,light.y+ray.get((i+1)%numRay).y);
            renderer.triangle(light.x, light.y, light.x + v.x, light.y + v.y, light.x + ray.get((i + 1) % numRay).x, light.y + ray.get((i + 1) % numRay).y);
            //renderer.line(light.x,light.y,light.x+v.x,light.y+v.y);
            //renderer.circle(light.x+v.x,light.y+v.y,5);
        }
        renderer.setColor(Color.BLUE);
        renderer.circle(light.x, light.y, 10);
        renderer.setColor(Color.WHITE);
        renderer.end();
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

            //getCells(p1.x,p1.y,p2.x,p2.y,line);
            controlK(p1.x, p1.y, p2.x, p2.y, line);
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
                //   ray.get(i).set(intersection(light,dir));
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

