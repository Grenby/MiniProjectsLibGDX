package com.mygdx.projects.rockets;

import com.badlogic.gdx.math.Vector2;

public class Unit {

    Vector2 position = new Vector2();
    Vector2 velocity = new Vector2();

    protected void updateVelocity(float delta) {
    }

    public void update(float delta) {
        updateVelocity(delta);
        position.mulAdd(velocity, delta);
    }

}
