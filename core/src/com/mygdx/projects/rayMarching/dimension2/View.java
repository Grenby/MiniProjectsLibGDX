package com.mygdx.projects.rayMarching.dimension2;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

/**
 * kjlkjlkj
 * kjlkjlk
 */
public class View {

    private static final Vector2 tmp1 = new Vector2(0, 0);
    private static final Vector2 tmp2 = new Vector2(0, 0);

    private final ArrayList<Figure> list = new ArrayList<>();
    private final float angle = MathUtils.PI / 4;
    private final float numRay = 1;
    private final float sin = MathUtils.sin(angle / numRay);
    private final float cos = MathUtils.cos(angle / numRay);

    public final Vector2 posView = new Vector2(0, 0);
    public final Vector2 direction = new Vector2(1, 0);
    public boolean renderRay = false;
    public int maxStep = 100;
    public float maxDist = 1000;
    public float minDist = .1f;

    private final Vector3 intersect = new Vector3();

    void addFigure(Figure f) {
        list.add(f);
    }


    private float getMinDistance(Vector2 point) {
        float distance = Float.MAX_VALUE;
        for (Figure figure : list) {
            float d = figure.distance(point);
            distance = Math.min(distance, d);
        }
        return distance;
    }

    private Vector3 getMinIntersect(Vector2 point) {
        float distance = Float.MAX_VALUE;
        for (Figure figure : list) {
            float d = figure.distance(point);
            distance = Math.min(distance, d);
            intersect.set(figure.getPos(), distance);
        }
        return intersect;
    }


    private void render1(ShapeRenderer renderer) {
        Vector2 dir = tmp1.set(direction).nor();//.rotateRad(-angle/2);
        Vector2 p = tmp2.set(posView);

        renderer.setColor(Color.RED);

        for (int i = 0; i < numRay; i++) {
            float distance;
            for (int k = 0; k < maxStep; k++) {
                distance = list.get(0).distance(p);
                for (int j = 1; j < list.size(); j++) {
                    float d = list.get(j).distance(p);
                    distance = Math.min(distance, d);
                }
                if (distance > maxDist) break;
                if (distance < minDist) break;
                renderer.circle(p.x, p.y, distance);
                p.mulAdd(dir, distance);
                //System.out.println(p);
                //if (distance<0.01f)break;
            }
            //System.out.println(p);
            //renderer.point(p.x,p.y,0);
            float x = dir.dot(cos, -sin);
            float y = dir.dot(sin, cos);
            dir.set(x, y);
            p.set(posView);
        }
        renderer.setColor(Color.WHITE);
    }

    private void goLeft(ShapeRenderer renderer, Vector2 start, Vector2 end, float d, float sign) {
        float dist = getMinDistance(end);

        renderer.circle(end.x, end.y, dist);
        renderer.setColor(Color.WHITE);
        renderer.point(end.x, end.y, 0);
        renderer.setColor(Color.RED);

        float angle = (float) Math.acos((2 * d * d - dist * dist) / (2 * d * d));
        float delta = angle;

        Vector2 v = new Vector2();
        v.set(end).sub(start).nor();

        Vector2 point = new Vector2();
        point.set(end);

        while (dist > minDist && delta < MathUtils.PI2) {
            v.rotateRad(sign * angle);
            point.set(start).mulAdd(v, d);
            dist = getMinDistance(point);

            renderer.circle(point.x, point.y, dist);
            renderer.setColor(Color.WHITE);
            renderer.point(point.x, point.y, 0);
            renderer.setColor(Color.RED);

            angle = (float) Math.acos((2 * d * d - dist * dist) / (2 * d * d));
            delta += angle;
        }
    }

    private void render2(ShapeRenderer renderer) {

        Vector2 dir = tmp1.set(Vector2.X);
        Vector2 p = tmp2.set(posView);

        renderer.setColor(Color.RED);

        Vector3 v = getMinIntersect(p);

        renderer.circle(p.x, p.y, v.z);

        dir.set(posView).sub(v.x, v.y).nor();
        p = p.mulAdd(dir, v.z);

        goLeft(renderer, posView, p, v.z, 1);
        goLeft(renderer, posView, p, v.z, -1);
        renderer.setColor(Color.WHITE);

    }

    void render(ShapeRenderer renderer) {

        renderer.circle(posView.x, posView.y, 5);

        for (Figure f : list) {
            f.render(renderer);
        }

        render2(renderer);
    }

}
