package com.mygdx.projects.rayMarching.dimention3;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class View {

    Vector3 position = new Vector3(0, 0, 0),
            direction = new Vector3(1, 0, 0);
    boolean renderRay = false;
    Camera3 camera;
    private ArrayList<Figure> list = new ArrayList<>();


    void addFigure(Figure f) {
        list.add(f);
    }

    void render(ShapeRenderer renderer) {

    }


}
