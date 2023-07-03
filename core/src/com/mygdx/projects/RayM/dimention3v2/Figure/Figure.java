package com.mygdx.projects.RayM.dimention3v2.Figure;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public abstract class Figure {
    public Vector3 pos;
    public Color color;

    public Vector3 normal(Vector3 pos) {
        return null;
    }

    public abstract float distance(float x, float y, float z);

    public float distance(Vector3 from) {
        return distance(from.x, from.y, from.z);
    }
}
