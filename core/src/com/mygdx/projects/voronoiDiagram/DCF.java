package com.mygdx.projects.voronoiDiagram;

import com.badlogic.gdx.math.Vector2;

public class DCF {

    private Vector2 center;
    private DCE begin;

    public DCF(Vector2 center, DCE begin) {
        this.center = center;
        this.begin = begin;
    }

    public Vector2 getCenter() {
        return center;
    }

    public void setCenter(Vector2 center) {
        this.center = center;
    }

    public DCE getBegin() {
        return begin;
    }

    public void setBegin(DCE begin) {
        this.begin = begin;
    }
}
