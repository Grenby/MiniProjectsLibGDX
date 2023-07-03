package com.mygdx.projects.VoronoiDiagram;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;
import java.util.Objects;

/*
This is class for build Voronoi diagram. It uses a divide and conquer algorithm
 */
public class DiagramBuilder2 {

    private final Array<Vector2> sortedVertex = new Array<>();
    private final Rectangle bounds = new Rectangle();
    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();
    private final Vector2 tmp3 = new Vector2();
    private final Vector2 tmp4 = new Vector2();
    private final Vector2[] vertices = {new Vector2(), new Vector2(), new Vector2(), new Vector2()};
    Array<Vector2> shellLeft = new Array<>(), shellRight = new Array<>();
    Array<Vector2> points = new Array<>();
    Shell shell = new Shell();

    //void updateLeft(DCEL.Vertex,)
    Vector2 v1 = new Vector2();
    Vector2 v2 = new Vector2();
    Vector2 v3 = new Vector2();

    public HashMap<Vector2, DCEL.Face> build(Array<Vector2> centers) {
        sortedVertex.clear();
        sortedVertex.addAll(centers);

        sortedVertex.sort((o1, o2) -> o1.x > o2.x ? 1 : o1.x < o2.x ? -1 : Float.compare(o1.y, o2.y));

        vertices[0].set(bounds.x, bounds.y);
        vertices[3].set(bounds.x, bounds.y + bounds.height);
        vertices[2].set(bounds.x + bounds.width, bounds.y + bounds.height);
        vertices[1].set(bounds.x + bounds.width, bounds.y);

        return buildD(centers, 0, centers.size);
    }

    DCEL.Face getPoly(Vector2[] points) {
        DCEL dcel = new DCEL();

        for (Vector2 v : points) {
            dcel.addVertex(v);
        }

        for (int i = 0; i < points.length; i++) {
            dcel.addEdge(points[i], points[i + 1 == points.length ? 0 : i + 1]);
        }

        DCEL.Face f1, f2;

        f1 = new DCEL.Face();
        f2 = new DCEL.Face();

        f1.boundary = dcel.getVertex(points[0]).edge;
        f2.boundary = dcel.getVertex(points[0]).edge.twin;

        for (int i = 0; i < points.length; i++) {
            dcel.getVertex(points[i]).edge.next = dcel.getVertex(points[i + 1 == points.length ? 0 : i + 1]).edge;
            dcel.getVertex(points[i + 1 == points.length ? 0 : i + 1]).edge.prev = dcel.getVertex(points[i]).edge;

            dcel.getVertex(points[i]).edge.twin.prev = dcel.getVertex(points[i]).edge.next.twin;
            dcel.getVertex(points[i]).edge.next.twin.next = dcel.getVertex(points[i]).edge.twin;

            dcel.getVertex(points[i]).edge.face = f1;
            dcel.getVertex(points[i]).edge.twin.face = f2;

        }

        return f1;
    }

    HashMap<Vector2, DCEL.Face> buildD(Array<Vector2> centers, int from, int to) {
        if (to <= from)
            return null;
        if (to - from == 1) {
            HashMap<Vector2, DCEL.Face> res = new HashMap<>();
            DCEL.Face f = getPoly(vertices);
            f.center = centers.get(from);
            res.put(centers.get(from), f);
            return res;
        } else {
            HashMap<Vector2, DCEL.Face> l = buildD(centers, from, (from + to) / 2);
            HashMap<Vector2, DCEL.Face> r = buildD(centers, (from + to) / 2, to);
            return split(centers, from, to, l, r);
        }
    }

    HashMap<Vector2, DCEL.Face> split(Array<Vector2> centers, int from, int to, HashMap<Vector2, DCEL.Face> leftD, HashMap<Vector2, DCEL.Face> rightD) {
        int s = (from + to) / 2;
        points.addAll(centers, from, s - from);
        shellLeft = shell.getShell(points, shellLeft, true);

        points.clear();
        points.addAll(centers, s, to - s);
        shellRight = shell.getShell(points, shellRight, true);

        Section l = new Section();
        Section q = new Section();

        //pair 1-2 and 3-4
        l.p1 = shellLeft.get(0);
        q.p1 = l.p1;
        l.p2 = shellRight.get(0);
        q.p2 = l.p2;
        for (Vector2 v : shellLeft) {
            if (v.y > l.p1.y)
                l.p1 = v;
            if (v.y < q.p1.y)
                q.p1 = v;
        }
        for (Vector2 v : shellRight) {
            if (v.y > l.p2.y)
                l.p2 = v;
            if (v.y < q.p2.y)
                q.p2 = v;
        }


        Array<DCEL.Vertex> bounds = new Array<>();
        HashMap<DCEL.Face, DCEL.Vertex> in = new HashMap<>();
        DCEL dcel = new DCEL();

        Vector2 pos = new Vector2(l.p1).add(l.p2).scl(0.5f);
        Vector2 dir = new Vector2(l.p1).sub(l.p2).rotate90(1);

        Vector2 pointUp = new Vector2();
        Vector2 pointDown = new Vector2();

        intersectWithBound(dir, pos, pointUp, pointDown);
        if (pointDown.y > pointUp.y) {
            Vector2 v = pointDown;
            pointDown = pointUp;
            pointUp = v;
        }

        bounds.add(dcel.addVertex(pointUp));

        pos.set(pointUp);
        dir.scl(-1);


        do {
            DCEL.HalfEdge e = getEdge(leftD.get(l.p1), pos, dir);
            pointUp.set(v3);
            DCEL.HalfEdge e2 = getEdge(rightD.get(l.p2), pos, dir);
            if (pointUp.y < v3.y) {
                e = e2;
                pointUp.set(v3);
            }

            dcel.addVertex(pointUp);
            dcel.addEdge(pos, pointUp);


        } while (!l.equals(q));


        return null;
    }

    DCEL.HalfEdge getEdge(DCEL.Face face, Vector2 pos, Vector2 dir) {
        boolean f = false;
        DCEL.HalfEdge current = face.boundary;
        DCEL.HalfEdge edge = null;
        do {
            Vector2 p1 = current.origin.pos;
            Vector2 p2 = current.twin.origin.pos;

            v1.set(p1).sub(pos);
            v2.set(p2).sub(pos);

            final float k1 = v1.crs(dir);
            final float k2 = dir.crs(v2);

            if (k1 * k2 <= 0) {
                intersect(dir, v1, v2, v3);
                v3.add(pos);
                if (v3.dst2(pos) < 0.01f)
                    continue;
                if (f) {
                    if (v3.y < v1.y) {
                        edge = current;
                        v3.set(v1);
                    }
                } else {
                    edge = current;
                    v3.set(v1);
                    f = true;
                }
            }
            current = current.next;
        } while (current != face.boundary);

        return edge;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    void intersectWithBound(Vector2 dir, Vector2 pos, Vector2 res1, Vector2 res2) {
        if (dir.x == 0 || dir.y / dir.x > bounds.height / bounds.width) {
            intersect(dir, tmp1.set(vertices[1]).sub(pos), tmp2.set(vertices[2].sub(pos)), res1);
            intersect(dir, tmp1.set(vertices[3]).sub(pos), tmp2.set(vertices[0].sub(pos)), res2);
        } else {
            intersect(dir, tmp1.set(vertices[0]).sub(pos), tmp2.set(vertices[1].sub(pos)), res1);
            intersect(dir, tmp1.set(vertices[2]).sub(pos), tmp2.set(vertices[3].sub(pos)), res1);
        }
    }

    void intersect(Vector2 dir, Vector2 p1, Vector2 p2, Vector2 res) {


        final float k1 = p1.crs(dir);
        final float k2 = dir.crs(p2);

        if (k2 == 0)
            res.set(p2);

        final float x = Math.abs(k1 / k2);
        res.set(p1.x + x * p2.x, p1.y + x * p2.y).scl(1 / (1 + x));
    }

    static class Section {
        Vector2 p1, p2;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Section section = (Section) o;
            return Objects.equals(p1, section.p1) && Objects.equals(p2, section.p2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(p1, p2);
        }
    }

}
