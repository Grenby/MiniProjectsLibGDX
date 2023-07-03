package com.mygdx.projects.light;

import com.badlogic.gdx.math.Vector2;

public class Section {

    Vector2 p1, p2, nor;
    int num;
    float d2;
    boolean isZero = false;

    Section(Vector2 p1, Vector2 p2, int num) {
        this.p1 = p1;
        this.p2 = p2;
        this.num = num;
        nor = p1.cpy().sub(p2).rotate90(1).nor().scl(10);
        final float x = (p1.x + p2.x) / 2;
        final float y = (p1.y + p2.y) / 2;
        d2 = x * x + y * y;
    }

    Section(Vector2 p1, Vector2 p2, Vector2 nor, int num) {
        this.p1 = p1;
        this.p2 = p2;
        this.num = num;
        this.nor = nor;
        final float x = (p1.x + p2.x) / 2;
        final float y = (p1.y + p2.y) / 2;
        d2 = x * x + y * y;
    }


    float length(Vector2 point) {
        float x = (p1.x + p2.x) / 2 - point.x;
        float y = (p1.y + p2.y) / 2 - point.y;
        return x * x + y * y;
    }

    Vector2 intersection(Vector2 p) {
        final float k1 = p1.crs(p);
        final float k2 = p.crs(p2);
        if (k2 == 0) return p2.cpy();
        final float x = Math.abs(k1 / k2);
        Vector2 v = new Vector2(p1.x + x * p2.x, p1.y + x * p2.y).scl(1 / (1 + x));
        //System.out.println(p1+" "+p2+" "+p+" "+v);
        return v;
    }

    Vector2 intersection(float k1, float k2) {
        if (k2 == 0) return p2.cpy();
        final float x = Math.abs(k1 / k2);
        Vector2 v = new Vector2(p1.x + x * p2.x, p1.y + x * p2.y).scl(1 / (1 + x));
        //System.out.println(p1+" "+p2+" "+p+" "+v);
        return v;
    }


    void setP1(Vector2 p1) {
        this.p1.set(p1);
        nor.set(p1.x - p2.x, p1.y - p2.y).rotate90(1).nor().scl(10);
    }

    void setP2(Vector2 p2) {
        this.p2.set(p2);
        nor.set(p1.x - p2.x, p1.y - p2.y).rotate90(1).nor().scl(10);
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
}
