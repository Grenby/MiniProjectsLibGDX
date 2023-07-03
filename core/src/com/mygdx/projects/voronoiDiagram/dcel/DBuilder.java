package com.mygdx.projects.voronoiDiagram.dcel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class DBuilder {

    public static class Edge {

        private Edge twin, next, prev;
        private Vector2 from, to;
        private Face left;

        private Edge() {
        }

        public Edge(Vector2 from, Vector2 to, Face left, Face right) {
            this.from = from;
            this.to = to;
            this.left = left;
            createTwin(right);
        }

        private void createTwin(Face right) {
            twin = new Edge();

            twin.from = this.to;
            twin.to = this.from;
            twin.twin = this;
            twin.left = right;
        }

        public Edge setPrev(Edge edge) {
            this.prev = edge;
            this.twin.next = edge.twin;
            return this;
        }

        public Edge setNext(Edge edge) {
            this.next = edge;
            this.twin.prev = edge.twin;
            return this;
        }

        public Edge getTwin() {
            return twin;
        }

        public Edge getNext() {
            return next;
        }

        public Edge getPrev() {
            return prev;
        }

        public Vector2 getFrom() {
            return from;
        }

        public Vector2 getTo() {
            return to;
        }

        public Face getLeft() {
            return left;
        }

        public void setFrom(Vector2 from) {
            this.from = from;
            this.twin.to = from;
        }

        public void setTo(Vector2 to) {
            this.to = to;
            this.twin.from = to;
        }
    }

    static public class Face {
        private Edge first = null;
        private Vector2 center = null;

        public Edge getFirst() {
            return first;
        }

        public void setFirst(Edge first) {
            this.first = first;
        }

        public Vector2 getCenter() {
            return center;
        }

        public void setCenter(Vector2 center) {
            this.center = center;
        }
    }

    Array<Face> faces = new Array<>();
    HashMap<Vector2, Face> poly = new HashMap<>();

    Array<Vector2> intersect = new Array<>();
    Array<Edge> intersectEdge = new Array<>();


    public Array<Vector2> build(Array<Vector2> centers, Array<Vector2> bounder) {
        createBounder(bounder);
        for (Vector2 v : centers) {
            addPoint(v);
        }
        return null;
    }

    private void createBounder(Array<Vector2> bound) {

        Face in = new Face();
        Face out = new Face();

        Edge edge = null;

        for (int i = 0; i < bound.size; i++) {

            Vector2 from = bound.get(i);
            Vector2 to = bound.get(i + 1 == bound.size ? 0 : i + 1);

            Edge e = new Edge(from, to, in, out);

            if (edge == null) {
                in.setFirst(e);
                out.setFirst(e.getTwin());
            } else {
                e.setPrev(edge);
                edge.setNext(e);
            }
            edge = e;
        }

        if (edge != null) {
            edge.setNext(in.getFirst());
            in.getFirst().setPrev(edge);
        }

        faces.add(in);
        faces.add(out);
    }

    private void addPoint(Vector2 v) {
        if (poly.size() == 0) {
            faces.get(0).setCenter(v);
            poly.put(v, faces.first());

        } else {
            Vector2 near = getNear(v);
            Face first = poly.get(near);

            do {
                getIntersect(v, poly.get(near));
            } while (first != poly.get(near));

        }
    }

    private void update(Face f, Edge e) {
        f.setFirst(e);

    }

    private void getIntersect(Vector2 v, Face f) {

    }

    private Vector2 getNear(Vector2 v) {
        Vector2 res = null;
        float dst = Float.MAX_VALUE;
        for (Vector2 c : poly.keySet()) {
            if (c.dst2(v) < dst) {
                dst = c.dst2(v);
                res = v;
            }
        }
        return res;
    }

}
