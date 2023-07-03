package com.mygdx.projects.collisionTest;

import java.util.ArrayList;
import java.util.List;

public class CircleWorld {


    public List<CircleObj> circles = new ArrayList<>();
    float x0, y0, x1, y1;
    int iter = 4;

    public CircleWorld(float x0, float y0, float x1, float y1) {
        this.x0 = x0;
        this.x1 = x1;
        this.y0 = y0;
        this.y1 = y1;
    }

    public List<CircleObj> getCircles() {
        return circles;
    }

    public void addCircle(CircleObj obj) {
        circles.add(obj);
    }

    public void update(float delta) {
        for (CircleObj circleObj : circles) {

            updateBorder(circleObj, delta);
            collision(circleObj);

            circleObj.x = circleObj.x + circleObj.vx * delta;
            circleObj.y = circleObj.y + circleObj.vy * delta;

        }
    }

    private void updateBorder(CircleObj obj, float delta) {
        if (obj.x - obj.radius < x0) {
            obj.x = obj.x + 2 * (x0 - obj.x + obj.radius);
            obj.vx = Math.abs(obj.vx);
        }
        if (obj.x + obj.radius > x1) {
            obj.x = obj.x + 2 * (x1 - obj.x - obj.radius);
            obj.vx = -Math.abs(obj.vx);
        }

        if (obj.y - obj.radius < y0) {
            obj.y = obj.y + 2 * (y0 - obj.y + obj.radius);
            obj.vy = Math.abs(obj.vy);
        }
        if (obj.y + obj.radius > y1) {
            obj.y = obj.y + 2 * (y1 - obj.y - obj.radius);
            obj.vy = -Math.abs(obj.vy);
        }

    }

    private void collision(CircleObj obj1) {
        for (int i = 0; i < iter; i++) {
            for (int j = 0; j < circles.size(); j++) {
                CircleObj obj2 = circles.get(j);
                if (obj2 != obj1) {
                    float nx = obj2.x - obj1.x;
                    float ny = obj2.y - obj1.y;
                    float ln = (float) Math.sqrt(nx * nx + ny * ny);
                    float r = obj1.radius + obj2.radius;
                    if (r > ln) {
                        //update Position
                        nx /= ln;
                        ny /= ln;

                        float m1 = obj1.m;
                        float m2 = obj2.m;

                        float l = (obj1.radius + obj2.radius) - ln;

                        float dx1 = -nx * m2 / (m1 + m2) * l;
                        float dy1 = -ny * m2 / (m1 + m2) * l;

                        float dx2 = nx * m1 / (m1 + m2) * l;
                        float dy2 = ny * m1 / (m1 + m2) * l;

                        obj1.x += 2 * dx1;
                        obj1.y += 2 * dy1;

                        obj2.x += 2 * dx2;
                        obj2.y += 2 * dy2;

                        //update velocity

                        float tx = -ny;
                        float ty = nx;

                        float ut1 = obj1.vx * tx + obj1.vy * ty;
                        float ut2 = obj2.vx * tx + obj2.vy * ty;

                        float vn1 = obj1.vx * nx + obj1.vy * ny;
                        float vn2 = obj2.vx * nx + obj2.vy * ny;

                        float un1 = m2 / (m1 + m2) * ((m1 / m2 - 1) * vn1 + 2 * vn2);
                        float un2 = m1 / (m1 + m2) * (2 * vn1 + (m2 / m1 - 1) * vn2);

                        obj1.vx = ut1 * tx + un1 * nx;
                        obj1.vy = ut1 * ty + un1 * ny;

                        obj2.vx = ut2 * tx + un2 * nx;
                        obj2.vy = ut2 * ty + un2 * ny;

                    }
                }
            }
        }
    }


}
