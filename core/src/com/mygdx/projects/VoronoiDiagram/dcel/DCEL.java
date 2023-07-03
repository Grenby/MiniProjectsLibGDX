package com.mygdx.projects.VoronoiDiagram.dcel;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class DCEL {

    public static class Vertex {
        private Vector2 pos;
        private Edge edge;

        public Vector2 getPos() {
            return pos;
        }

        public Vertex setPos(Vector2 pos) {
            this.pos = pos;
            return this;
        }

        public Edge getEdge() {
            return edge;
        }

        public Vertex setEdge(Edge edge) {
            this.edge = edge;
            return this;
        }
    }

    public static class Edge {

        Edge twin, next, prev;
        Vertex from, to;
        Face left;

        public Edge(Vertex from, Vertex to, Face left, Face right) {

        }

        public Edge getTwin() {
            return twin;
        }

        public Edge setTwin(Edge twin) {
            this.twin = twin;
            return this;
        }

        public Edge getNext() {
            return next;
        }

        public Edge setNext(Edge next) {
            this.next = next;
            return this;
        }

        public Edge getPrev() {
            return prev;
        }

        public Edge setPrev(Edge prev) {
            this.prev = prev;
            return this;
        }

        public Vertex getFrom() {
            return from;
        }

        public Edge setFrom(Vertex from) {
            this.from = from;
            return this;
        }

        public Face getLeft() {
            return left;
        }

        public Edge setLeft(Face left) {
            this.left = left;
            return this;
        }

        public Vertex getTo() {
            return to;
        }

        public Edge setTo(Vertex to) {
            this.to = to;
            return this;
        }
    }

    public static class Face {
        Edge bounder;

        public Edge getBounder() {
            return bounder;
        }

        public Face setBounder(Edge bounder) {
            this.bounder = bounder;
            return this;
        }
    }

    public Array<Face> faces = new Array<>();

}
