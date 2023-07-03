package com.mygdx.projects.wave;

import com.badlogic.gdx.math.Vector2;

import java.util.Objects;

public class Cell2d {

    final static float EPS = 0.0001f;

    Vector2 position = new Vector2();
    float newY = 0;
    float velocity = 0;
    Cell2d r, l;
    float mass = 1;
    boolean isInf = false;
    float k = 10;


    public void update(float delta) {
        if (isInf) {
            return;
        }
        float d1 = 0;
        float d2 = 0;

        if (!Objects.isNull(l)) {
            d1 = position.y - l.position.y;
        }
        if (!Objects.isNull(r)) {
            d2 = position.y - r.position.y;
        }
        float a = -k * (d1 + d2) / mass;
        update(delta, a);
    }

    private void update(float delta, float a) {
        velocity += a * delta;
        newY = position.y + velocity * delta;
    }

    public void move() {
        position.y = newY;
    }


}
