package com.mygdx.projects.grid;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class HexagonUtils {

    public static boolean intersectionOXZ(Ray ray, Vector3 out) {
        if (MathUtils.isEqual(ray.direction.y, 0, 0.1f)) return false;
        float t = -ray.origin.y / ray.direction.y;
        if (t < 0) return false;

        out.set(ray.direction).scl(t).add(ray.origin);
        return true;
        //return !(out.dst2(ray.origin) > 10000);
    }

    public static void addRing(int x, int y, int r) {

    }

    public static void addHex(int x, int y, int r) {

    }

}
