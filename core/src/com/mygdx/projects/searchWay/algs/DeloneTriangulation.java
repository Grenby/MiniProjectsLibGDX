package com.mygdx.projects.searchWay.algs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.projects.utils.garphs.GraphImpl;

public final class DeloneTriangulation {

    private DeloneTriangulation() {
    }

    public static GraphImpl<Vector2> build(Array<Vector2> points, GraphImpl<Vector2> out) {
        points.sort((o1, o2) -> Float.compare(o1.x, o2.x));


        return out;
    }

}
