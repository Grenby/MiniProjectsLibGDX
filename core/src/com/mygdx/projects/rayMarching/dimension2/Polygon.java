package com.mygdx.projects.rayMarching.dimension2;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Polygon implements Figure {

    private Segment[] segments;

    Polygon(Segment[] segments) {
        this.segments = segments;
    }

    @Override
    public float distance(Vector2 point) {
        float d = segments[0].distance(point);
        for (int i = 1; i < segments.length; i++) {
            float s = segments[i].distance(point);
            d = d < s ? d : s;
        }
        return d;
    }


    @Override
    public void render(ShapeRenderer renderer) {
        for (Segment s : segments) s.render(renderer);
    }

    @Override
    public Vector2 getPos() {
        return null;
    }
}
