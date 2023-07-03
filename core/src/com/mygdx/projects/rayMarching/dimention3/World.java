package com.mygdx.projects.rayMarching.dimention3;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class World {

    private ArrayList<Figure> figures = new ArrayList<>();
    private Vector3 tmp1 = new Vector3(),
            tmp2 = new Vector3();

    private int steps = 10;

    void addFigure(Figure figure) {
        figures.add(figure);
    }

    Figure getNear(Vector3 pos, Vector3 direction) {
        tmp1.set(pos);
        tmp2.set(direction).nor();
        Figure res = null;
        float distance0 = figures.get(0).distance(tmp1);
        for (int j = 1; j < figures.size(); j++) {
            float dopDistance = figures.get(j).distance(tmp1);
            distance0 = distance0 < dopDistance ? distance0 : dopDistance;
        }
        float distance = 100;
        for (int k = 0; k < 10; k++) {
            distance = figures.get(0).distance(tmp1);
            for (int j = 1; j < figures.size(); j++) {
                float dopDistance = figures.get(j).distance(tmp1);
                if (distance > dopDistance) {
                    distance = dopDistance;
                    res = figures.get(j);
                }
            }
            tmp1.add(tmp2.x * distance, tmp2.y * distance, tmp2.z * distance);
        }
        if (distance > 10) res = null;
        return res;
    }
}
