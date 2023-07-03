package com.mygdx.projects.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;

public class MyMath {

    public final static RandomXS128 random = new RandomXS128();

    private MyMath() {
    }

    public static float mix(float a, float b, float t) {
        return (b - a) * t + a;
    }

    public static float randAngle() {
        return MathUtils.PI2 * random.nextFloat();
    }

    public static float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(vector.y, vector.x);
    }

    public static Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = (float) Math.sin(angle);
        outVector.y = (float) Math.cos(angle);
        return outVector;
    }


}
