package com.mygdx.projects.RayM.dimention3v2.Figure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Plane extends Figure {

    private final Vector3 nor;
    private final float d;

    public Plane(Vector3 pos, Vector3 nor) {
        this.nor = nor;
        if (nor.len2() != 1) nor.nor();
        this.pos = pos;
        d = -nor.dot(pos);
        color = Color.BLUE;
    }

    @Override
    public float distance(float x, float y, float z) {
        return nor.dot(x, y, z) + d;
    }

    public Vector3 getNor() {
        return nor;
    }

    @Override
    public Vector3 normal(Vector3 pos) {
        return nor;
    }
}
