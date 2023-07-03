package com.mygdx.projects.voronoiDiagram;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class DCEL {

    public HashMap<Vector2, Vertex> vertices;

    public Vertex addVertex(Vector2 pos) {
        return vertices.put(pos, new Vertex(pos));
    }

    public HalfEdge addEdge(Vector2 from, Vector2 to) {
        Vertex v1 = vertices.get(from);
        Vertex v2 = vertices.get(to);
        if (v1 == null || v2 == null)
            return null;
        HalfEdge e1 = new HalfEdge();
        e1.origin = v1;

        HalfEdge e2 = new HalfEdge();
        e2.origin = v2;

        e1.twin = e2;
        e2.twin = e1;

        if (v1.edge == null)
            v1.edge = e1;
        if (v2.edge == null)
            v2.edge = e2;


        return e1;
    }

    public Vertex getVertex(Vector2 pos) {
        return vertices.get(pos);
    }

    public Face buildFromPoly(Vector2[] points) {
        for (Vector2 v : points) {
            addVertex(v);
        }

        for (int i = 0; i < points.length; i++) {
            addEdge(points[i], points[i + 1 == points.length ? 0 : i + 1]);
        }

        DCEL.Face f1, f2;

        f1 = new DCEL.Face();
        f2 = new DCEL.Face();

        f1.boundary = getVertex(points[0]).edge;
        f2.boundary = getVertex(points[0]).edge.twin;

        for (int i = 0; i < points.length; i++) {
            getVertex(points[i]).edge.next = getVertex(points[i + 1 == points.length ? 0 : i + 1]).edge;
            getVertex(points[i + 1 == points.length ? 0 : i + 1]).edge.prev = getVertex(points[i]).edge;

            getVertex(points[i]).edge.twin.prev = getVertex(points[i]).edge.next.twin;
            getVertex(points[i]).edge.next.twin.next = getVertex(points[i]).edge.twin;

            getVertex(points[i]).edge.face = f1;
            getVertex(points[i]).edge.twin.face = f2;

        }

        return f1;
    }

    public static class Vertex {
        public Vector2 pos;
        public HalfEdge edge = null;

        Vertex(Vector2 pos) {
            this.pos = pos;
        }
    }

    public static class HalfEdge {
        HalfEdge next, prev, twin;
        Vertex origin;
        Face face;
    }

    public static class Face {
        Vector2 center;
        HalfEdge boundary;
    }

}
