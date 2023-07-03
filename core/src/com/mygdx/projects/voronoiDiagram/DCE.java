package com.mygdx.projects.voronoiDiagram;

import com.badlogic.gdx.math.Vector2;

public class DCE {

    public static final float INF = 10e5f;
    private Type type;
    private Vector2 from, to;
    private DCE twin, next, prev;
    private Vector2 center;

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Vector2 getFrom() {
        return from;
    }

    public void setFrom(Vector2 from) {
        this.from = from;
    }

    public Vector2 getTo() {
        return to;
    }

    public void setTo(Vector2 to) {
        this.to = to;
    }

    public DCE getTwin() {
        return twin;
    }

    public void setTwin(DCE twin) {
        if (twin != null)
            twin.twin = this;
        this.twin = twin;
    }

    public DCE getNext() {
        return next;
    }

    public void setNext(DCE next) {
        if (next != null)
            next.prev = this;
        this.next = next;
    }

    public DCE getPrev() {
        return prev;
    }

    public void setPrev(DCE prev) {
        if (prev != null)
            prev.next = this;
        this.prev = prev;
    }

    /*
    RayOut go out from point "from"
    RayIn go in to point "from"

     */
    enum Type {
        RayIn, RayOut, Line, Segment
    }

}
