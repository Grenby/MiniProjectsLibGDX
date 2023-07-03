package com.mygdx.projects.rayMarching.dimention3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Sphere implements Figure {

    private Vector3 pos;
    private Color color = Color.RED;
    float r;

    Sphere(Vector3 pos, float r) {
        this.pos = pos;
        this.r = r;
    }

    @Override
    public float r() {
        return r;
    }

    @Override
    public float distance(Vector3 point) {
        return pos.dst(point) - r;
    }

    @Override
    public float distance(float x, float y, float z) {
        return pos.dst(x, y, z) - r;
    }

    @Override
    public Vector3 position() {
        return pos;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }


}
