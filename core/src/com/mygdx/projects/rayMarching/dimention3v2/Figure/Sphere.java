package com.mygdx.projects.rayMarching.dimention3v2.Figure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Sphere extends Figure {

    private float r;

    public Sphere(Vector3 pos, float r) {
        this.pos = pos;
        this.r = r;
        color = Color.RED;
    }

    @Override
    public Vector3 normal(Vector3 pos_) {
        return new Vector3(pos_).sub(pos).nor();
    }

    @Override
    public float distance(float x, float y, float z) {
        return pos.dst(x, y, z) - r;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }
}
