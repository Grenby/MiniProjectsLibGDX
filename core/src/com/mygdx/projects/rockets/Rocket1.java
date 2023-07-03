package com.mygdx.projects.rockets;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Rocket1 extends Unit {


    static Vector2 tmp1 = new Vector2();
    static Vector2 tmp2 = new Vector2();
    static int qqq = 0;

    int q = qqq++;

    Unit target;
    float time = 0;
    Vector2 start = new Vector2(5, 5);
    Vector2[] randVec = new Vector2[11];

    void getNoise(float d) {
        getVelocity(
                start,
                tmp1.set(0, 1),
                target.position,
                tmp2.set(1, 0),
                d
        );
        float val = getNoise1(d * randVec.length);
        val *= 1 - (1 - 2 * d) * (1 - 2 * d);
        tmp1.set(velocity).rotate90(1).nor().scl(val);
        position.add(tmp1);
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

    public Rocket1(Unit target) {
        this.target = target;
        for (int i = 0; i < randVec.length; i++) {
            randVec[i] = new Vector2().setToRandomDirection();
        }
    }

    void getPosition(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, float t) {
        float x = 1 - t;
        position.setZero()
                .mulAdd(p1, x * x * x)
                .mulAdd(p2, 3 * x * x * t)
                .mulAdd(p3, 3 * x * t * t)
                .mulAdd(p4, t * t * t);
    }


    @Override
    protected void updateVelocity(float delta) {

    }

    @Override
    public void update(float delta) {
        time += delta;
        getPosition(
                start,
                tmp1.set(0, 1),
                target.position,
                tmp2.set(1, 0),
                time / 4
        );
        System.out.println(position + "   ww");
        getNoise(time / 4);
        System.out.println(position);

        if (target.position.dst(position) < 0.2f) {
            position.set(5 + MathUtils.random() * 3, 5 + MathUtils.random() * 3);
            time = 0;
        }
    }


}
