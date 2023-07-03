package com.mygdx.projects.VoronoiDiagram;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

public class VoronoiData {

    private final HashMap<Vector2, DCEL.Face> faces = new HashMap<>();

    public HashMap<Vector2, DCEL.Face> getFaces() {
        return faces;
    }

}
