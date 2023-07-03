package com.mygdx.projects.rockets;

import com.badlogic.gdx.math.Vector2;

public interface BoidsUnit {

    Vector2 getPosition();

    Vector2 getVelocity();

    void updateVelocity(Vector2 value);

}
