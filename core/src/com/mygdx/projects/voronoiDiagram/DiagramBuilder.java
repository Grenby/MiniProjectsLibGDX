package com.mygdx.projects.voronoiDiagram;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.PriorityQueue;


public class DiagramBuilder {

    private final Array<Vector2> sortedVertex = new Array<>();
    private final HashMap<Vector2, Array<Vector2>> polygons = new HashMap<>();
    private float x1, x2, y1, y2;


    public Array<Vector2> build(Array<Vector2> centers) {

        PriorityQueue<Event> queue = new PriorityQueue<>((o1, o2) -> {
            if (o1.y == o2.y)
                return 0;
            return o1.y < o2.y ? 1 : -1;
        });

        for (Vector2 v : centers) {
            queue.add(new Event(Event.POINT_EVENT, v.y, v.x));
        }

        Arc left = null;
        TreeBP treeBP = new TreeBP();

//        while (!queue.isEmpty()){
//            Event event = queue.poll();
//            double y = queue.size()>0? event.y + (event.y + queue.peek().y)/2.0 : y1;
//            if (event.type == 0){
//                if (left == null){
//                    left = new Arc();
//                    left.y0 = event.y;
//                }else{
//                    Arc a = left;
//                    while (event.x > a.r){
//                        a = a.right;
//                    }
//                    Arc arc = new Arc();
//                    arc.y0 = event.y;
//                    arc.x0 = event.x;
//
//                    arc.right = a.right;
//                    if (arc.right!=null)
//                        a.right.left = arc;
//                    arc.left = a.left;
//                }
//            }else{
//
//            }
//        }

        return null;
    }

    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY1() {
        return y1;
    }

    public void setY1(float y1) {
        this.y1 = y1;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    static class Event {
        static int POINT_EVENT = 0;
        static int CIRCLE_EVENT = 1;

        int type = -1;
        double y = 0;
        double x = 0;

        public Event(int type, double y, double x) {
            this.type = type;
            this.y = y;
            this.x = x;
        }
    }

    static class Tree {

    }

    static class Arc {
        float y0, x0;
        float l, r;
        Arc right;
        Arc left;

        void update(double l) {

        }

        double getR(double l) {
            return right == null ? Double.MAX_VALUE : 1;
        }

        double getL(double l) {
            return left == null ? Double.MIN_VALUE : 1;
        }


    }

    static class BreakPoint {
        float x;
        float y;
        Arc lArc;
        Arc rArc;

        public BreakPoint(float x, float y, Arc lArc, Arc rArc) {
            this.x = x;
            this.y = y;
            this.lArc = lArc;
            this.rArc = rArc;
        }
    }

    static class TreeBP {
        static float eps = 0.001f;
        Arc first = null;
        Array<BreakPoint> points = new Array<>();

        TreeBP() {
        }

        void addArc(float x, float y, float l) {
            if (first == null) {
                first = new Arc();
                first.y0 = y;
                first.x0 = x;
            } else if (points.size == 0) {
                Arc m = new Arc();
                m.y0 = y;
                m.x0 = x;
                points.add(new BreakPoint(x, y, first, m));
                points.add(new BreakPoint(x, y, m, first));
            } else {
                Arc a = null;
                for (BreakPoint bp : points) {
                    bp.x = getX(l, bp.lArc.x0, bp.lArc.y0, bp.rArc.x0, bp.rArc.y0, 1);
                    if (bp.x > x)
                        a = bp.lArc;
                }
                if (a == null)
                    a = points.get(points.size - 1).rArc;
                Arc m = new Arc();
                m.y0 = y;
                m.x0 = x;
                points.add(new BreakPoint(x, y, a, m));
                points.add(new BreakPoint(x, y, m, a));
                points.sort((o1, o2) -> Float.compare(o1.x, o2.x));
            }
        }

        void update(float l) {

        }

        void updateR(float l, BreakPoint p) {

        }

        BreakPoint getBP(float x) {
            return null;
        }

        Arc getArc(float x) {
            return null;
        }

        float getX(float l, float x1, float y1, float x2, float y2, int right) {
            float eps = 0.001f;
            float a = y1 - y2;
            if (Math.abs(a) < eps) return (x1 + x2) / 2;

            float b = -2 * ((y1 - l) * x2 - (y2 - l) * x1);
            float c = (y1 - l) * (x2 * x2 + y2 * y2 - l * l) + (y2 - l) * (x1 * x2 + y1 * y1 - l * l);
            float d = b * b - 4 * a * c;
            if (d < 0) return 0;
            if (right > 0)
                return (-b + (float) Math.sqrt(d)) / (2 * a);
            else
                return (-b - (float) Math.sqrt(d)) / (2 * a);
        }

    }
}
