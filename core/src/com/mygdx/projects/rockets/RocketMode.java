package com.mygdx.projects.rockets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;

public class RocketMode extends ScreenAdapter {

    final int WEIGHT = Gdx.graphics.getWidth();
    final int HEIGHT = Gdx.graphics.getHeight();

    final float W = 15;
    final float H = W / WEIGHT * HEIGHT;

    ShapeRenderer renderer = new ShapeRenderer();
    OrthographicCamera camera = new OrthographicCamera(W, H);

    Vector2 p1 = new Vector2();
    Vector2 p2 = new Vector2();
    Vector2 p3 = new Vector2();
    Vector2 p4 = new Vector2();

    Vector2 p5 = new Vector2();
    Vector2 p6 = new Vector2();

    Vector2 tmp1 = new Vector2();
    Vector2 tmp2 = new Vector2();
    Vector2 tmp3 = new Vector2();

    float t = 0;
    float scl = 2;
    Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();

    Vector2 targetPosition = new Vector2();
    Vector2 targetVelocity = new Vector2();

    Unit unit = new Unit();
    ArrayList<Rocket> rockets = new ArrayList<>();
    ArrayList<Rocket> near = new ArrayList<>();
    ArrayList<Rocket> avoid = new ArrayList<>();

    Vector2 getPos(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, float t) {
        float x = 1 - t;
        tmp1.setZero().mulAdd(p1, x * x * x).mulAdd(p2, 3 * x * x * t).mulAdd(p3, 3 * x * t * t).mulAdd(p4, t * t * t);
        return tmp1;
    }

    Vector2 getVelocity(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 p4, float t) {
        float x = 1 - t;
        tmp1.setZero()
                .mulAdd(p2, 3 * x * x).mulAdd(p1, -3 * x * x)
                .mulAdd(p3, 6 * x * t).mulAdd(p2, -6 * x * t)
                .mulAdd(p4, 3 * t * t).mulAdd(p3, -3 * t * t);
        return tmp1;
    }

    float[] rands = {0.78f, 0.5f, 0.134f, 1f, 0.78f};

    Vector2[] randVec = new Vector2[11];

    void render1(float delta) {
        t += delta;
        if (t >= 2 * scl) {
            t = 0;
        }
        if (t >= scl) {
            velocity.set(getVelocity(p4, p5, p6, p1, t / scl - 1)).nor().scl(1);
            position.set(getPos(p4, p5, p6, p1, t / scl - 1));
        } else {
            velocity.set(getVelocity(p1, p2, p3, p4, t / scl)).nor().scl(1);
            position.set(getPos(p1, p2, p3, p4, t / scl));
        }

        ScreenUtils.clear(Color.BLACK);

        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.setColor(Color.RED);

            renderer.circle(p1.x, p1.y, 0.05f, 30);
            renderer.circle(p2.x, p2.y, 0.05f, 30);
            renderer.circle(p3.x, p3.y, 0.05f, 30);
            renderer.circle(p4.x, p4.y, 0.05f, 30);

            renderer.line(p1, p2);
            renderer.line(p3, p4);


            renderer.setColor(Color.WHITE);
            float t = 0;
            while (t <= 1) {
                tmp2.set(getPos(p1, p2, p3, p4, t));
                tmp3.set(getPos(p1, p2, p3, p4, t + 0.01f));
                renderer.line(tmp2, tmp3);
                t += 0.01f;
            }
            t = 0;
            while (t <= 1) {
                tmp2.set(getPos(p4, p5, p6, p1, t));
                tmp3.set(getPos(p4, p5, p6, p1, t + 0.01f));
                renderer.line(tmp2, tmp3);
                t += 0.01f;
            }
            renderer.circle(position.x, position.y, 0.1f, 30);
            renderer.line(position, tmp1.set(position).mulAdd(velocity, 5));

        }
        renderer.end();


    }

    Vector2 getNoise(float d) {
        if (d < 1) {
            return tmp1.set(velocity);
        }
        d -= 1;
        d *= 2;
        float val = getNoise1(d);
        tmp1.set(velocity).rotateRad(val * MathUtils.PI / 4);
        return tmp1;
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

    void render2(float delta) {
        getVelocity(position, tmp2.set(position).mulAdd(velocity, delta), targetPosition, tmp3.set(targetPosition).mulAdd(targetVelocity, delta), delta).nor().scl(1);
        velocity.set(tmp1);

        velocity.set(getNoise(targetPosition.dst(position)));

        position.mulAdd(velocity, delta);
        //position.set(getPos(position,tmp2.set(position).mulAdd(velocity,delta), targetPosition,tmp3.set(targetPosition).mulAdd(targetVelocity,delta),delta));
        targetPosition.mulAdd(targetVelocity, delta);
        //targetVelocity.rotateRad(0.01f);

        ScreenUtils.clear(Color.BLACK);

        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.setColor(Color.RED);

            renderer.circle(targetPosition.x, targetPosition.y, 0.1f, 30);

            renderer.setColor(Color.WHITE);
            float t = 0;
            float d = 0.01f;
            while (t <= 1) {
                p1.set(getPos(position, tmp2.set(position).mulAdd(velocity, delta), targetPosition, tmp3.set(targetPosition).mulAdd(targetVelocity, delta), t));
                p2.set(getPos(position, tmp2.set(position).mulAdd(velocity, delta), targetPosition, tmp3.set(targetPosition).mulAdd(targetVelocity, delta), t + 0.01f));
                //renderer.line(p1,p2);
                t += d;
            }
            renderer.circle(position.x, position.y, 0.1f, 30);
            renderer.line(position, tmp1.set(position).mulAdd(velocity, 1));

        }
        renderer.end();

        velocity.set(getVelocity(position, tmp2.set(position).add(velocity), targetPosition, tmp3.set(targetPosition).add(targetVelocity), delta / scl)).nor();

    }

    Vector2 getRule1(Rocket rocket) {
        p1.setZero();
        for (Rocket r : avoid) {
            p1.add(rocket.position).sub(r.position);
        }
        return p1;
    }

    Vector2 getRule2(Rocket rocket) {
        p2.setZero();
        if (near.size() == 0) {
            return p2;
        }
        for (Rocket r : near) {
            p2.add(r.velocity);
        }
        p2.scl(1f / near.size());
        return p2.sub(rocket.velocity);
    }

    Vector2 getRule3(Rocket rocket) {
        p3.setZero();
        for (Rocket r : avoid) {
            p3.add(r.position);
        }
        p3.scl(1f / near.size());
        p3.sub(rocket.position);
        return p3;
    }

    void getNear(Rocket rocket) {
        float max = 1;
        float min = 0.3f;
        avoid.clear();
        near.clear();

        for (Rocket r : rockets) {
            if (r == rocket) {
                continue;
            }
            float d = r.position.dst2(rocket.position);
            if (d < max * max) {
                if (d > min * min) {
                    near.add(r);
                } else {
                    avoid.add(r);
                }
            }
        }
    }

    void update(float delta) {
        /*
        turnfactor: 0.2
visualRange: 20
protectedRange: 2
centeringfactor: 0.0005
avoidfactor: 0.05
matchingfactor: 0.05
maxspeed: 3
minspeed: 2
         */
        float centeringFactor = 0;//0.0005f;
        float avoidFactor = 0.05f;
        float matchingFactor = 0f;
        for (Rocket r : rockets) {
            getNear(r);
            Vector2 v1 = getRule1(r),
                    v2 = getRule2(r),
                    v3 = getRule1(r);
            tmp1.setZero()
                    .mulAdd(v1, avoidFactor)
                    .mulAdd(v2, matchingFactor)
                    .mulAdd(v3, centeringFactor);
            r.updateVelocity(tmp1.nor().scl(5));
        }
        for (int i = 0; i < rockets.size(); i++) {
            Rocket r1 = rockets.get(i);
            for (int j = i + 1; j < rockets.size(); j++) {
                Rocket r2 = rockets.get(j);
                if (r1.position.dst2(r2.position) < 0.04 && r1.position.dst2(unit.position) > 2) {
                    r1.position.set(-5 + MathUtils.random() * 3, 5);
                    r2.position.set(-5 + MathUtils.random() * 3, -5);
                }
            }
        }
    }

    void render3(float delta) {
        unit.velocity.set(targetVelocity).scl(10);
        unit.update(delta);
        for (Unit r : rockets) {
            r.update(delta);
        }

        update(delta);
        camera.position.set(targetPosition, 0);
        camera.update();
        ScreenUtils.clear(Color.BLACK);

        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.setColor(Color.RED);
            renderer.circle(unit.position.x, unit.position.y, 0.1f, 20);
            renderer.setColor(Color.WHITE);
            for (Unit u : rockets) {
                renderer.circle(u.position.x, u.position.y, 0.1f, 20);
                tmp1.set(u.position);
                tmp2.set(u.position).mulAdd(u.velocity, 1);
                // renderer.line(tmp1,tmp2);
            }
        }
        renderer.end();
    }

    void renderFunc(float delta) {
        ScreenUtils.clear(Color.BLACK);

        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.setColor(Color.WHITE);
            renderer.line(0, 0, randVec.length, 0);

            renderer.setColor(Color.RED);
            for (int i = 0; i < randVec.length; i++) {
                Vector2 v = randVec[i];
                tmp1.set(i, 0);
                tmp2.set(tmp1).mulAdd(v, 0.5f);
                renderer.line(tmp1, tmp2);
            }

            renderer.setColor(Color.WHITE);
            float x = 0;
            float d = 0.01f;
            while (x < randVec.length) {

                float y1 = getNoise1(x);
                float y2 = getNoise1(x + d);
                p1.set(x, y1 * 5);
                p2.set(x + d, y2 * 5);

                renderer.line(p1, p2);

                x += d;
            }

        }
        renderer.end();

    }

    @Override
    public void render(float delta) {

        targetVelocity.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            targetVelocity.add(0, 1);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            targetVelocity.add(0, -1);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            targetVelocity.add(1, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            targetVelocity.add(-1, 0);
        }
        targetVelocity.nor().scl(1);

        render3(delta);
        //renderFunc(delta);
        //render2(delta);
//        velocity.set(getVelocity(position,tmp2.set(position).add(velocity), targetPosition,tmp3.set(targetPosition).add(targetVelocity),delta)).nor();
//        position.mulAdd(velocity,scl*delta);
//        //position.set(getPos(position,tmp2.set(position).add(velocity),tmp3.set(targetPosition).mulAdd(targetVelocity,-1),targetPosition,delta/scl));
//        targetPosition.mulAdd(targetVelocity,delta);
//        targetVelocity.rotateRad(0.05f);
//
//
//        ScreenUtils.clear(Color.BLACK);
//
//        renderer.setProjectionMatrix(camera.combined);
//
//        renderer.begin(ShapeRenderer.ShapeType.Line);
//        {
//            renderer.setColor(Color.RED);
//
//            renderer.circle(targetPosition.x, targetPosition.y, 0.1f,30);
//
//            renderer.setColor(Color.WHITE);
//            float t = 0;
//            while (t<=1){
//                p1.set(getPos(position,tmp2.set(position).add(velocity),tmp3.set(targetPosition).mulAdd(targetVelocity,-1),targetPosition,t));
//                p2.set(getPos(position,tmp2.set(position).add(velocity),tmp3.set(targetPosition).mulAdd(targetVelocity,-1),targetPosition,t+0.01f));
//                renderer.line(p1,p2);
//                t+=0.01f;
//            }
//            renderer.circle(position.x,position.y,0.1f,30);
//            renderer.line(position,tmp1.set(position).mulAdd(velocity,1));
//
//        }
//        renderer.end();

        if (position.dst2(targetPosition) < 0.4 * 0.4) {
            targetPosition.set(1, 1);
            targetVelocity.set(1, 0);
            position.set(W, H);
        }

    }


    @Override
    public void show() {
        camera.position.set(5, 0, 0);
        camera.update();

        p1.set(1, 1);
        p2.set(2, 1);
        p3.set(4, 5);
        p4.set(4, 4);
        p5.set(4, 3);
        p6.set(0.5f, 1);

        position.set(p1);
        velocity.set(1, 0);
        targetPosition.set(p4);
        targetVelocity.set(1, 0);


        for (int i = 0; i < randVec.length; i++) {
            randVec[i] = new Vector2();
            randVec[i].set(1, 0);
            randVec[i].setToRandomDirection();
        }

        for (int i = 0; i < 30; i++) {
            rockets.add(new Rocket(unit));
            rockets.get(i).position.set(W / 2, H / 2 + i);
        }
        unit.position.set(1, 1);
    }


}
