package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.math.RandomXS128;

public class WhiteNoise {

    private static final RandomXS128 random = new RandomXS128();

    public static float[][] getWhiteNoise(int w, int h) {
        return getWhiteNoise(new float[w][h], 0, 1);
    }

    public static float[][] getWhiteNoise(float[][] out, float min, float max) {
        int w = out.length;
        int h = out[0].length;
        float d = max - min;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                out[i][j] = (random.nextFloat()) * d + min;
            }
        }
        return out;
    }

}
