package com.mygdx.projects.voronoiDiagram;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.LinkedList;

public class ForceBuilder {

    final float EPS = 0.01f;

    Rectangle bound = new Rectangle();
    Vector2[] vertex = new Vector2[]{new Vector2(), new Vector2(), new Vector2(), new Vector2()};
    LinkedList<Intersect> intersects = new LinkedList<>();
    HashMap<Vector2, DCE> res = new HashMap<>();
    Vector2 tmp1 = new Vector2();
    Vector2 tmp2 = new Vector2();

    private void updateCenters(Vector2 center) {
        DCE dce = res.get(center);
        DCE current = dce;
        do {
            current.setCenter(center);
            current = current.getNext();
        } while (current != dce);
    }

    public HashMap<Vector2, DCE> build(Array<Vector2> points) throws NullPointerException {
        points.sort((o1, o2) -> o1.x > o2.x ? 1 : o1.x < o2.x ? -1 : Float.compare(o1.y, o2.y));
        res.clear();
        if (points.size <= 1)
            return null;
        vertex[0].set(bound.x, bound.y);
        vertex[1].set(bound.x + bound.width, bound.y);
        vertex[2].set(bound.x + bound.width, bound.y + bound.height);
        vertex[3].set(bound.x, bound.y + bound.height);
        addFirstPoint(points.get(0));
        //buildTwoPoint(points.get(0),points.get(1));
        for (int i = 1; i < points.size; i++) {
            if (res.containsKey(points.get(i)))
                continue;
            buildPoint(points.get(i));
        }
        return res;
    }

    private Vector2 getDCE(Vector2 pos) {
        float len = Float.MAX_VALUE;
        Vector2 r = null;
        for (Vector2 v : res.keySet()) {
            if (pos.dst2(v) < len) {
                len = pos.dst2(v);
                r = v;
            }
        }
        return r;
    }

    private Vector2 intersectTwoLine(float a1, float b1, float c1, float a2, float b2, float c2) {
        float x, y;
        if (Math.abs(a1) < EPS) {
            y = -c1 / b1;
            x = (c1 / b1 * b2 - c2) / a2;
        } else {
            y = (a2 * c1 - c2 * a1) / (a1 * b2 - a2 * b1);
            x = -c1 / a1 - b1 / a1 * y;
        }

        return new Vector2(x, y);
    }

    private Vector2 lineIntersect(Vector2 pos, Vector2 dir, DCE dce) {
        Vector2 tmp = new Vector2(dce.getFrom()).sub(dce.getTo());
        if (tmp.isCollinear(dir, EPS))
            return null;
        tmp.rotate90(1);
        Vector2 v = intersectTwoLine(-dir.y, dir.x, -pos.dot(-dir.y, dir.x), tmp.x, tmp.y, -tmp.dot(dce.getFrom()));
        if (tmp.set(v).sub(pos).dot(dir) < 0)
            return null;
        return v;
    }

    private Vector2 rayIntersect(Vector2 pos, Vector2 dir, Vector2 end, Vector2 on) {
        Vector2 dir2 = new Vector2(on).sub(end);
        float f = Intersector.intersectRayRay(pos, dir, end, dir2);
        if (f == Float.POSITIVE_INFINITY || f < 0)
            return null;
        Vector2 point = new Vector2(pos).mulAdd(dir, f);
        if (point.cpy().sub(end).dot(dir2) < 0)
            return null;
        return point;
    }

    private Vector2 segmentIntersect(Vector2 pos, Vector2 dir, DCE dce) {
        Vector2 p1 = new Vector2(dce.getFrom()).sub(pos);
        Vector2 p2 = new Vector2(dce.getTo()).sub(pos);

        final float k1 = p1.crs(dir);
        final float k2 = dir.crs(p2);
        if (k2 == 0) return dce.getTo().cpy();
        if (k1 * k2 < 0)
            return null;

        final float x = Math.abs(k1 / k2);
        Vector2 v = new Vector2(p1.x + x * p2.x, p1.y + x * p2.y).scl(1 / (1 + x));
        //System.out.println(p1+" "+p2+" "+p+" "+v);
        return v.add(pos);
    }

    private int getBound(Vector2 pos) {
        if (Math.abs(pos.x - bound.x) < EPS)
            return 3;
        if (Math.abs(pos.x - bound.x - bound.width) < EPS)
            return 1;
        if (Math.abs(pos.y - bound.y) < EPS)
            return 0;
        if (Math.abs(pos.y - bound.y - bound.height) < EPS)
            return 2;
        return 0;
    }

    private void buildPoint(Vector2 point) throws NullPointerException {
        Vector2 center = getDCE(point);
        Vector2 pos = new Vector2(point).add(center).scl(0.5f);
        Vector2 dir = new Vector2(point).sub(center).rotate90(1);
        intersects.clear();
        Intersect start = intersect(pos, dir, res.get(center));
//        DCE dce = updateBorder(intersect,point);
//        res.put(point,dce);
        intersects.add(start);
        Vector2 p = start.v1;
        Intersect intersect = start;
        while (!pointOnBorder(p) && p.dst2(start.v2) > EPS) {
            center = intersect.dce1.getTwin().getCenter();
            if (center == null) {
                System.out.println(intersect.dce2.getTwin().getFrom());
                System.out.println(intersect.dce2.getTwin().getTo());
            }
            pos = new Vector2(point).add(center).scl(0.5f);
            dir = new Vector2(point).sub(center).rotate90(1);
            intersect = intersect(pos, dir, res.get(center));
            intersects.addLast(intersect);
            p = intersect.v1;
        }
        if (p.dst2(start.v2) > EPS) {
            p = start.v2;
            intersect = start;
            while (!pointOnBorder(p)) {
                center = intersect.dce2.getTwin().getCenter();
                if (center == null) {
                    System.out.println(intersect.dce2.getTwin().getFrom());
                    System.out.println(intersect.dce2.getTwin().getTo());
                }
                pos = new Vector2(point).add(center).scl(0.5f);
                dir = new Vector2(point).sub(center).rotate90(1);
                intersect = intersect(pos, dir, res.get(center));
                intersects.addFirst(intersect);
                p = intersect.v2;
            }
            DCE current = null;
            DCE s = null;
            for (Intersect i : intersects) {
                DCE dce = updateBorder(i, point);
                if (current == null) {
                    current = dce;
                    s = dce;
                    res.put(point, dce);
                } else {
                    current.setNext(dce);
                    current = dce;
                }
            }
            int v1 = getBound(intersects.getLast().v1);
            int v2 = getBound(intersects.getFirst().v2);
            if (v1 == v2) {
                Vector2 from = intersects.getLast().v1;
                Vector2 to = intersects.getFirst().v2;
                DCE e = new DCE(), t = new DCE();
                e.setTwin(t);
                e.setType(DCE.Type.Segment);
                t.setType(DCE.Type.Segment);
                e.setPrev(current);
                e.setNext(s);
                e.setCenter(point);
                e.setFrom(from);
                e.setTo(to);
                t.setFrom(to);
                t.setTo(from);
            } else {
                Vector2 from = intersects.getLast().v1;
                Vector2 to = vertex[(v1 + 1) % 4];
                DCE e = new DCE(), t = new DCE();
                e.setTwin(t);
                e.setType(DCE.Type.Segment);
                t.setType(DCE.Type.Segment);
                e.setPrev(current);
                e.setCenter(point);
                e.setFrom(from);
                e.setTo(to);
                t.setFrom(to);
                t.setTo(from);
                current = e;
                v1++;
                v1 %= 4;
                while (v1 != v2) {
                    from = vertex[v1];
                    to = vertex[(v1 + 1) % 4];
                    e = new DCE();
                    t = new DCE();
                    e.setTwin(t);
                    e.setType(DCE.Type.Segment);
                    t.setType(DCE.Type.Segment);
                    e.setPrev(current);
                    e.setCenter(point);
                    e.setFrom(from);
                    e.setTo(to);
                    t.setFrom(to);
                    t.setTo(from);
                    current = e;
                    v1++;
                    v1 %= 4;
                }
                from = vertex[v1];
                to = intersects.getFirst().v2;
                e = new DCE();
                t = new DCE();
                e.setTwin(t);
                e.setType(DCE.Type.Segment);
                t.setType(DCE.Type.Segment);
                e.setPrev(current);
                e.setCenter(point);
                e.setFrom(from);
                e.setNext(s);
                e.setTo(to);
                t.setFrom(to);
                t.setTo(from);
                current = e;
                res.put(point, e);
            }
        } else {
            DCE current = null;
            DCE first = null;
            for (Intersect i : intersects) {
                DCE dce = updateBorder(i, point);
                if (current == null) {
                    current = dce;
                    first = dce;
                    res.put(point, dce);
                } else {
                    current.setNext(dce);
                    current = dce;
                }
            }
            if (current != null)
                current.setNext(first);
        }
    }

    private DCE updateBorder(Intersect intersect, Vector2 point) {
        DCE e = new DCE(), t = new DCE();

        e.setTwin(t);
        e.setCenter(intersect.dce1.getCenter());
        t.setCenter(point);
        e.setType(DCE.Type.Segment);
        t.setType(DCE.Type.Segment);

        e.setFrom(intersect.v1);
        e.setTo(intersect.v2);
        t.setTo(e.getFrom());
        t.setFrom(e.getTo());

        intersect.dce1.setTo(intersect.v1);
        intersect.dce1.getTwin().setFrom(intersect.v1);

        intersect.dce2.setFrom(intersect.v2);
        intersect.dce2.getTwin().setTo(intersect.v2);

        e.setPrev(intersect.dce1);
        e.setNext(intersect.dce2);
        res.put(intersect.dce1.getCenter(), e);
        return t;
    }

    private boolean pointOnBorder(Vector2 pos) {
        float lx = Math.min(Math.abs(pos.x - bound.x), Math.abs(pos.x - bound.width - bound.x));
        float ly = Math.min(Math.abs(pos.y - bound.y), Math.abs(pos.y - bound.height - bound.y));
        float l = Math.min(lx, ly);
        return l < EPS;
    }

    private void buildTwoPoint(Vector2 p1, Vector2 p2) {
        DCE e1 = new DCE(), e2 = new DCE();
        e1.setCenter(p1);
        e2.setCenter(p2);

        e1.setNext(e1);
        e2.setNext(e2);
        e1.setTwin(e2);

        e1.setType(DCE.Type.Line);
        e2.setType(DCE.Type.Line);

        Vector2 center = new Vector2(p1).add(p2).scl(0.5f);
        Vector2 dir = new Vector2(p2).sub(p1).rotate90(1);

        e1.setFrom(center.cpy().sub(dir));
        e1.setTo(center.cpy().add(dir));

        e2.setFrom(e1.getTo());
        e2.setTo(e1.getFrom());

        res.put(p1, e1);
        res.put(p2, e2);
    }

    private void addPointOnLine(Vector2 pos, Vector2 center) {
        DCE e = new DCE(), t = new DCE();
        Vector2 from = new Vector2(pos).add(center).scl(0.5f);
        Vector2 to = new Vector2(center).sub(pos).rotate90(1).add(from);

        e.setFrom(from);
        t.setTo(from);

        e.setTo(to);
        t.setFrom(to);

        e.setTwin(t);

        e.setCenter(pos);
        t.setCenter(center);

        e.setType(DCE.Type.Line);
        t.setType(DCE.Type.Line);

        DCE l1 = res.get(center);
        DCE l2 = l1.getNext();

        Vector2 tmp = new Vector2(l1.getFrom()).sub(pos);
        Vector2 dir = new Vector2(center).sub(pos);
        if (l1 == l2) {
            e.setNext(e);
            t.setNext(l1);
            if (tmp.dot(dir) == 0) {
                center = l1.getTwin().getCenter();
                from = new Vector2(pos).add(center).scl(0.5f);
                to = new Vector2(center).sub(pos).rotate90(1).add(from);
                l1.setFrom(from);
                l1.setTo(to);
                l1.getTwin().setTo(from);
                l1.getTwin().setFrom(to);
            }
        } else {
            if (tmp.dot(dir) > 0) {
                DCE l = l1;
                l1 = l2;
                l2 = l1;
            }
            if (tmp.dot(dir) < 0) {

            } else if (tmp.dot(dir) == 0) {

            } else {

            }

        }

    }

    private void addFirstPoint(Vector2 pos) {
        DCE[] e = new DCE[4];
        DCE[] t = new DCE[4];
        for (int i = 0; i < 4; i++) {
            e[i] = new DCE();
            t[i] = new DCE();
            e[i].setTwin(t[i]);
        }
        for (int i = 0; i < 4; i++) {
            e[i].setNext(e[i + 1 == 4 ? 0 : i + 1]);
            e[i].setPrev(e[i == 0 ? 3 : i - 1]);

            t[i].setNext(t[i + 1 == 4 ? 0 : i + 1]);
            t[i].setPrev(t[i == 0 ? 3 : i - 1]);

            e[i].setType(DCE.Type.Segment);
            t[i].setType(DCE.Type.Segment);

            e[i].setFrom(vertex[i]);
            e[i].setTo(vertex[i + 1 == 4 ? 0 : i + 1]);
            t[i].setFrom(e[i].getTo());
            t[i].setTo(e[i].getFrom());

            e[i].setCenter(pos);
        }

        res.put(pos, e[0]);
    }

    private Intersect intersect(Vector2 pos, Vector2 dir, DCE dce) throws NullPointerException {
        DCE current = dce;
        Intersect intersect = null;
        do {
            Vector2 v;
            v = segmentIntersect(pos, dir, current);
            if (v != null) {
                if (intersect == null) {
                    intersect = new Intersect();
                    intersect.dce1 = current;
                    intersect.v1 = v;
                } else {
                    intersect.dce2 = current;
                    intersect.v2 = v;
                    break;
                }
            }
            current = current.getNext();
        } while (current != dce);
        if (intersect != null) {
            if (intersect.v2 == null) {
                throw new NullPointerException();
            }
            Vector2 center = dce.getCenter();
            tmp1.set(intersect.v1).sub(center);
            tmp2.set(intersect.v2).sub(center);
            if (tmp1.crs(tmp2) < 0)
                intersect.swap();
        } else {
            System.out.println(pos);
            System.out.println(dir.nor());
            System.out.println(dce.getCenter());
            current = dce;
            Vector2 v = new Vector2(pos).sub(dce.getCenter());
            v.add(pos);
            System.out.println(v);
            System.out.println("DCE:");
            Array<Vector2> points = new Array<>();
            do {
                points.add(current.getFrom());
                System.out.println(current.getFrom());
                v.set(current.getFrom()).sub(current.getTo());
                System.out.println(v.nor());
                Vector2 vv = new Vector2(pos).sub(current.getTo());
                System.out.println(vv.nor());
                System.out.println(v.crs(dir));
                current = current.getNext();
            } while (current != dce);
            System.out.println(Intersector.isPointInPolygon(points, pos));
            /*

                (407.40784,107.90103)
(16.781044,-343.15723)
(578.98645,116.29156)
(235.82922,99.510506)
DCE:
(640.0,239.92401)
(584.1314,234.26917)
(475.55728,0.0)
(640.0,0.0)
false

             */
        }

        return intersect;
    }

    public Rectangle getBound() {
        return bound;
    }

    static class Intersect {
        DCE dce1, dce2;
        Vector2 v1, v2;

        void swap() {
            Vector2 v = v1;
            v1 = v2;
            v2 = v;
            DCE dce = dce1;
            dce1 = dce2;
            dce2 = dce;
        }

    }

}
