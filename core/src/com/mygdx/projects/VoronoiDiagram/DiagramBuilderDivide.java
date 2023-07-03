package com.mygdx.projects.VoronoiDiagram;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Objects;

public class DiagramBuilderDivide {

    final static float EPS = 0.001f;
    private final Array<Vector2> sortedPoints = new Array<>();

    private final Array<Intersect> points = new Array<>();
    private final HashMap<DCE, Vector2> bounder = new HashMap<>();


    public HashMap<Vector2, DCE> build(Array<Vector2> points) {
        sortedPoints.clear();
        sortedPoints.addAll(points);
        sortedPoints.sort((o1, o2) -> o1.x > o2.x ? 1 : o1.x < o2.x ? -1 : Float.compare(o1.y, o2.y));

        return divideBuild(0, sortedPoints.size);
    }

    private HashMap<Vector2, DCE> divideBuild(int from, int to) {
        if (to - from == 3) {
            return buildThreePoint(sortedPoints.get(from), sortedPoints.get(from + 1), sortedPoints.get(from + 2));
        } else if (to - from == 2) {
            return buildTwoPoint(sortedPoints.get(from), sortedPoints.get(from + 1));
        } else {
            return merge(divideBuild(from, (from + to) / 2), divideBuild((from + to) / 2, to));
        }
    }

    private void getMaxMin(Segment up, Segment down, HashMap<Vector2, DCE> left, HashMap<Vector2, DCE> right) {
        for (Vector2 v : left.keySet()) {
            if (up.p1 == null)
                up.p1 = v;
            if (down.p1 == null)
                down.p1 = v;
            if (v.y < down.p1.y || (v.y == down.p1.y && v.x > down.p1.x))
                down.p1 = v;
            if (v.y > up.p1.y || (v.y == down.p1.y && v.x < up.p1.x))
                up.p1 = v;
        }

        for (Vector2 v : right.keySet()) {
            if (up.p2 == null)
                up.p2 = v;
            if (down.p2 == null)
                down.p2 = v;
            if (v.y < down.p2.y || (v.y == down.p2.y && v.x < down.p2.x))
                down.p2 = v;
            if (v.y > up.p2.y || (v.y == down.p2.y && v.x > down.p2.x))
                up.p2 = v;
        }
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
        if (k1 * k2 > 0)
            return null;
        if (p1.dot(dir) < 0)
            return null;

        final float x = Math.abs(k1 / k2);
        Vector2 v = new Vector2(p1.x + x * p2.x, p1.y + x * p2.y).scl(1 / (1 + x));
        //System.out.println(p1+" "+p2+" "+p+" "+v);
        return v.add(pos);
    }

    private Intersect getIntersect(Vector2 pos, Vector2 dir, DCE dce) {
        if (dce == null)
            return null;
        Vector2 res = null;
        DCE resDCE = null;
        DCE current = dce;
        do {
            Vector2 v;
            if (dce.getType() == DCE.Type.Line) {
                v = lineIntersect(pos, dir, dce);
            } else if (dce.getType() == DCE.Type.RayIn) {
                v = rayIntersect(pos, dir, dce.getTo(), dce.getFrom());
            } else if (dce.getType() == DCE.Type.RayOut) {
                v = rayIntersect(pos, dir, dce.getFrom(), dce.getTo());
            } else
                v = segmentIntersect(pos, dir, dce);
            if (v != null) {
                if (res == null || v.y > res.y || (v.y == res.y && v.x > res.x)) {
                    res = v;
                    resDCE = current;
                }
            }
            current = current.getNext();
        } while (dce != current);
        return res == null ? null : new Intersect(res, dir.cpy(), resDCE);
    }

    private HashMap<Vector2, DCE> addBound(HashMap<Vector2, DCE> left, HashMap<Vector2, DCE> right) {

        return null;
    }

    private HashMap<Vector2, DCE> merge(HashMap<Vector2, DCE> left, HashMap<Vector2, DCE> right) {
        Segment up = new Segment(), down = new Segment();
        getMaxMin(up, down, left, right);

        Vector2 pos = new Vector2(up.p1).add(up.p2).scl(0.5f);
        Vector2 dir = new Vector2(up.p2).sub(up.p1).rotate90(1);

        Intersect i = null;
        Intersect i1 = getIntersect(pos, dir, left.get(up.p1));
        Intersect i2 = getIntersect(pos, dir, right.get(up.p2));
        if (i1 != null && i2 != null) {
            i = i1.point.y > i2.point.y ? i1 : i2;
        } else if (i1 == null && i2 != null) {
            i = i2;
        } else if (i1 != null) {
            i = i1;
        } else {
            dir.scl(-1);
            i1 = getIntersect(pos, dir, left.get(up.p1));
            i2 = getIntersect(pos, dir, right.get(up.p2));
            if (i1 != null && i2 != null) {
                i = i1.point.y > i2.point.y ? i1 : i2;
            } else if (i1 == null && i2 != null) {
                i = i2;
            } else if (i1 != null) {
                i = i1;
            }
            if (i == null)
                System.out.println("ERROR");
        }
        i.l = left.get(up.p1);
        i.r = right.get(up.p2);

        points.add(i);


        while (!up.equals(down)) {
            if (i == i1) {
                up.p1 = i.intersect.getTwin().getCenter();
            } else {
                up.p2 = i.intersect.getTwin().getCenter();
            }
            pos = new Vector2(up.p1).add(up.p2).scl(0.5f);
            dir = new Vector2(up.p2).sub(up.p1).rotate90(-1);
            i1 = getIntersect(pos, dir, left.get(up.p1));
            i2 = getIntersect(pos, dir, right.get(up.p2));

            if (i1 != null && i2 != null) {
                i = i1.point.y > i2.point.y ? i1 : i2;
            } else if (i1 == null && i2 != null) {
                i = i2;
            } else if (i1 != null) {
                i = i1;
            } else {
                i = new Intersect(points.get(points.size - 1).point, dir.cpy());
            }
            i.l = left.get(up.p1);
            i.r = right.get(up.p2);
            points.add(i);
        }

        for (Intersect in : points)
            System.out.println(in.point + " " + in.dir + " " + in.l.getCenter() + " " + in.r.getCenter());


        //todo smth wrong

        return addBound(left, right);
    }

    private HashMap<Vector2, DCE> buildTwoPoint(Vector2 p1, Vector2 p2) {
        HashMap<Vector2, DCE> map = new HashMap<>();
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

        map.put(p1, e1);
        map.put(p2, e2);
        return map;
    }

    private HashMap<Vector2, DCE> buildThreePoint(Vector2 p1, Vector2 p2, Vector2 p3) {
        if (inOneLine(p1, p2, p3)) {
            return buildOneLine(p1, p2, p3);
        } else {
            return buildCircle(p1, p2, p3);
        }
    }

    private HashMap<Vector2, DCE> buildOneLine(Vector2 p1, Vector2 p2, Vector2 p3) {
        HashMap<Vector2, DCE> map = new HashMap<>();
        DCE e1 = new DCE(), t1 = new DCE(),
                e2 = new DCE(), t2 = new DCE();

        e1.setCenter(p1);
        t1.setCenter(p2);
        e2.setCenter(p2);
        t2.setCenter(p3);

        e1.setType(DCE.Type.Line);
        e2.setType(DCE.Type.Line);
        t1.setType(DCE.Type.Line);
        t2.setType(DCE.Type.Line);

        e1.setNext(e1);
        t2.setNext(t2);

        t1.setNext(e2);
        e2.setNext(t1);

        e1.setTwin(t1);
        e2.setTwin(t2);

        Vector2 center = new Vector2(p1).add(p2).scl(0.5f);
        Vector2 dir = new Vector2(p2).sub(p1).rotate90(1);

        e1.setFrom(center.cpy().sub(dir));
        e1.setTo(center.cpy().add(dir));

        t1.setFrom(e1.getTo());
        t1.setTo(e1.getFrom());

        center.set(p2).add(p3).scl(0.5f);
        dir.set(p3).sub(p2).rotate90(1);

        e2.setFrom(center.cpy().sub(dir));
        e2.setTo(center.cpy().add(dir));

        t2.setFrom(e1.getTo());
        t2.setTo(e1.getFrom());

        map.put(p1, e1);
        map.put(p2, e2);
        map.put(p3, t2);


        return map;
    }

    private HashMap<Vector2, DCE> buildCircle(Vector2 p1, Vector2 p2, Vector2 p3) {
        HashMap<Vector2, DCE> map = new HashMap<>();
        DCE e1 = new DCE(), t1 = new DCE(),
                e2 = new DCE(), t2 = new DCE(),
                e3 = new DCE(), t3 = new DCE();

        e1.setCenter(p1);
        t1.setCenter(p2);
        e2.setCenter(p2);
        t2.setCenter(p3);
        e3.setCenter(p3);
        t3.setCenter(p1);

        e1.setType(DCE.Type.RayOut);
        e2.setType(DCE.Type.RayOut);
        e3.setType(DCE.Type.RayOut);

        t1.setType(DCE.Type.RayIn);
        t2.setType(DCE.Type.RayIn);
        t3.setType(DCE.Type.RayIn);

        Vector2 center = getCenter(p1, p2, p3);

        e1.setNext(t2);
        t2.setNext(e1);

        e2.setNext(t3);
        t3.setNext(e2);

        e3.setNext(t1);
        t1.setNext(e3);

        e1.setTwin(t1);
        e2.setTwin(t2);
        e3.setTwin(t3);

        e1.setFrom(center.cpy());
        e2.setFrom(center.cpy());
        e3.setFrom(center.cpy());

        t1.setTo(center.cpy());
        t2.setTo(center.cpy());
        t3.setTo(center.cpy());

        Vector2 v = new Vector2(center);
        int reflect = getReflect(p1, p2, p3, center);

        center.set(p2).add(p1).scl(0.5f);
        if (reflect == 1) {
            v.sub(center);
            center.mulAdd(v, 2);
        }
        e1.setTo(center.cpy());
        t1.setFrom(e1.getTo());

        center.set(p3).add(p2).scl(0.5f);
        if (reflect == 2) {
            v.sub(center);
            center.mulAdd(v, 2);
        }
        e2.setTo(center.cpy());
        t2.setFrom(e2.getTo());

        center.set(p1).add(p3).scl(0.5f);
        if (reflect == 3) {
            v.sub(center);
            center.mulAdd(v, 2);
        }
        e3.setTo(center.cpy());
        t3.setFrom(e3.getTo());

        map.put(p1, e1);
        map.put(p2, e2);
        map.put(p3, e3);
        return map;
    }

    private Vector2 getCenter(Vector2 p1, Vector2 p2, Vector2 p3) {
        float a = p2.x - p1.x;
        float b = p2.y - p1.y;
        float c = p3.x - p1.x;
        float d = p3.y - p1.y;
        float e = a * (p1.x + p2.x) + b * (p1.y + p2.y);
        float f = c * (p1.x + p3.x) + d * (p1.y + p3.y);
        float g = 2 * (a * (p3.y - p2.y) - b * (p3.x - p2.x));
        float cx = (d * e - b * f) / g;
        float cy = (a * f - c * e) / g;
        System.out.println(new Vector2(cx, cy));
        return new Vector2(cx, cy);
    }

    private boolean inOneLine(Vector2 p1, Vector2 p2, Vector2 p3) {
        Vector2 t1 = new Vector2(p2).sub(p1);
        Vector2 t2 = new Vector2(p3).sub(p2);
        return t1.isCollinear(t2, EPS);
    }

    private int getReflect(Vector2 p1, Vector2 p2, Vector2 p3, Vector2 center) {
        if (Intersector.isPointInTriangle(center, p1, p2, p3))
            return 0;
        float x = p1.x - center.x;
        float y = p1.y - center.y;
        float a = (p2.x + p3.x) / 2;
        float b = (p2.y + p3.y) / 2;
        if (a * x + b * y > 0)
            return 1;
        x = p2.x - center.x;
        y = p2.y - center.y;
        a = (p1.x + p3.x) / 2;
        b = (p1.y + p3.y) / 2;
        if (a * x + b * y > 0)
            return 2;
        return 3;
    }

    private static class Segment {
        Vector2 p1, p2;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Segment segment = (Segment) o;
            return Objects.equals(p1, segment.p1) && Objects.equals(p2, segment.p2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(p1, p2);
        }
    }

    private static class Intersect {
        Vector2 point;
        Vector2 dir;
        DCE l;
        DCE r;
        DCE intersect;

        public Intersect(Vector2 point, Vector2 dir) {
            this.point = point;
            this.dir = dir;
        }

        public Intersect(Vector2 point, Vector2 dir, DCE intersect) {
            this.point = point;
            this.dir = dir;
            this.intersect = intersect;
        }
    }


}
