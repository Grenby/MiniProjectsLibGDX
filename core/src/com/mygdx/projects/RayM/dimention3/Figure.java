package com.mygdx.projects.RayM.dimention3;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public interface Figure {

    float r();

    float distance(Vector3 point);

    float distance(float x, float y, float z);

    Vector3 position();

    //Vector3 normal();
    Color getColor();

    void setColor(Color color);

}
