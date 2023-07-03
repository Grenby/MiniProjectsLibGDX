package com.mygdx.projects.RayM.dimension2;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Segment implements Figure {

    private Vector2 p1 = new Vector2(),
            p2 = new Vector2(),
            nor = new Vector2();

    Segment() {
    }

    Segment(float x1, float y1, float x2, float y2) {
        p1.set(x1, y1);
        p2.set(x2, y2);
        setNor();
    }

    Segment(Vector2 p1, Vector2 p2) {
        this.p1.set(p1);
        this.p2.set(p2);
        setNor();
    }

    public Vector2 getP1() {
        return p1;
    }

    public void setP1(Vector2 p1) {
        this.p1 = p1;
        setNor();
    }

    public Vector2 getP2() {
        return p2;
    }

    public void setP2(Vector2 p2) {
        this.p2 = p2;
        setNor();
    }

    private void setNor() {
        nor.set(p1.x - p2.x, p1.y - p2.y);
        nor.rotate90(1);
    }

    @Override
    public float distance(Vector2 point) {
        float k1 = nor.crs(point.x - p1.x, point.y - p1.y);
        float k2 = nor.crs(point.x - p2.x, point.y - p2.y);
        if (k1 * k2 <= 0) return Math.abs(nor.x * (point.x - p1.x) + nor.y * (point.y - p1.y)) / nor.len();
        else if (k1 < 0) return Vector2.len(point.x - p1.x, point.y - p1.y);
        else return Vector2.len(point.x - p2.x, point.y - p2.y);

    }

    @Override
    public void render(ShapeRenderer renderer) {
        renderer.line(p1, p2);
    }

    @Override
    public Vector2 getPos() {
        return null;
    }
}
