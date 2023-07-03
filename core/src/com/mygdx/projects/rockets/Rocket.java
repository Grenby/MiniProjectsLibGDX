package com.mygdx.projects.rockets;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Rocket extends Unit implements BoidsUnit {

    static Vector2 tmp1 = new Vector2();
    static Vector2 tmp2 = new Vector2();
    static int qq = 0;
    int q = qq++;
    Unit target;
    float t;
    Vector2[] randVec = new Vector2[11];
    Vector2 boidsVelocity = new Vector2();


    void getNoise(float d) {
        if (target.position.dst2(position) < 2) {
            return;
        }
        d = (d) / 2;
        velocity.rotateRad(getNoise1(d) * MathUtils.PI / 4);
    }

    float func(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    float getNoise1(float d) {
        d %= randVec.length;
        int k = (int) d;
        d -= Math.floor(d);
        float left = tmp1.set(d, 0).dot(randVec[k]);
        float right = tmp2.set(d - 1, 0).dot(randVec[(k + 1) % randVec.length]);
        d = func(d);
        return lerp(left, right, d);
    }

    public Rocket(Unit target) {
        this.target = target;
        for (int i = 0; i < randVec.length; i++) {
            randVec[i] = new Vector2().setToRandomDirection();
        }
    }

    void getVelocity(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, float t) {
        float x = 1 - t;
        velocity.setZero()
                .mulAdd(p2, 3 * x * x).mulAdd(p1, -3 * x * x)
                .mulAdd(p3, 6 * x * t).mulAdd(p2, -6 * x * t)
                .mulAdd(p4, 3 * t * t).mulAdd(p3, -3 * t * t)
                .nor()
                .scl(5);
    }


    @Override
    protected void updateVelocity(float delta) {
        if (target.velocity.len2() < 0.1f) {
            getVelocity(
                    position,
                    tmp1.set(position).mulAdd(velocity, delta),
                    target.position,
                    tmp2.set(target.position).mulAdd(new Vector2(1, 0), delta),
                    delta
            );
        } else {
            getVelocity(
                    position,
                    tmp1.set(position).mulAdd(velocity, delta),
                    target.position,
                    tmp2.set(target.position).mulAdd(target.velocity, delta),
                    delta
            );
        }
        getNoise(t);

        if (position.dst2(target.position) > 2 && boidsVelocity.len2() > 0) {
            velocity.add(boidsVelocity).scl(0.3f);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        t += delta;
        if (target.position.dst(position) < 0.2f) {
            position.set(5, q);
            t = 0;
        }
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public void updateVelocity(Vector2 value) {
        boidsVelocity.set(value);
    }
}
