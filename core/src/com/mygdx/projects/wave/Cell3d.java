package com.mygdx.projects.wave;

import com.badlogic.gdx.math.Vector3;

import java.util.Objects;


public class Cell3d {

    final static float EPS = 0.0001f;

    public Vector3 position = new Vector3();
    private float newZ = 0;
    float velocity = 0;
    Cell3d r, l, u, d;
    float mass = 1;
    boolean isInf = false;
    float k = 10;


    public void update(float delta) {
        if (isInf) {
            return;
        }
        float d1 = 0;
        float d2 = 0;
        float d3 = 0;
        float d4 = 0;

        if (!Objects.isNull(l)) {
            d1 = position.z - l.position.z;
        }
        if (!Objects.isNull(r)) {
            d2 = position.z - r.position.z;
        }
        if (!Objects.isNull(u)) {
            d3 = position.z - u.position.z;
        }
        if (!Objects.isNull(d)) {
            d4 = position.z - d.position.z;
        }

        float a = -k * (d1 + d2 + d3 + d4) / mass;
        update(delta, a);
    }

    private void update(float delta, float a) {
        velocity += a * delta;
        newZ = position.z + velocity * delta;
    }

    public void move() {
        position.z = newZ;
    }


}
