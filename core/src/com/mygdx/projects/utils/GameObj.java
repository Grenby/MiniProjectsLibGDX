package com.mygdx.projects.utils;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;

public interface GameObj {

    enum TypeBoundingVolume {
        Box, Sphere
    }

    TypeBoundingVolume getTypeBoundingVolume();

    BoundingBox getBoundingBox();

    Sphere getSphere();

}
