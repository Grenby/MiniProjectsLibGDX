package com.mygdx.projects.perlinNoise.noise;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.projects.utils.MyMath;

public class PerlinNoiseSquare {

    private final RandomXS128 random = new RandomXS128();
    private final Vector2[][] directions;
    private final int n = 256;

    public static class Builder {
        private final PerlinNoiseSquare perlinNoiseSquare;

        public Builder() {
            perlinNoiseSquare = new PerlinNoiseSquare();
        }

        public Builder(long seed) {
            perlinNoiseSquare = new PerlinNoiseSquare(seed);
        }

        public Builder addUp(PerlinNoiseSquare noise) {
            int n = noise.n;
            for (int i = 0; i < n; i++) {
                perlinNoiseSquare.directions[i][n - 1].set(noise.directions[i][n - 1]);
            }
            return this;
        }

        public Builder addLeft(PerlinNoiseSquare noise) {
            int n = noise.n;
            for (int i = 0; i < n; i++) {
                perlinNoiseSquare.directions[0][i].set(noise.directions[0][i]);
            }
            return this;
        }

        public Builder addDown(PerlinNoiseSquare noise) {
            int n = noise.n;
            for (int i = 0; i < n; i++) {
                perlinNoiseSquare.directions[i][0].set(noise.directions[i][0]);
            }
            return this;
        }

        public Builder addRight(PerlinNoiseSquare noise) {
            int n = noise.n;
            for (int i = 0; i < n; i++) {
                perlinNoiseSquare.directions[n - 1][i].set(noise.directions[n - 1][i]);
            }
            return this;
        }

        public PerlinNoiseSquare create() {
            return perlinNoiseSquare;
        }

    }

    public PerlinNoiseSquare() {
        this(-1);
    }

    public PerlinNoiseSquare(long seed) {
        if (seed != -1)
            random.setSeed(seed);
        directions = new Vector2[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                directions[i][j] = new Vector2();
                MyMath.angleToVector(directions[i][j], getAngle());
            }
        }
    }

    private float qunticCurve(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private float getAngle() {
        return MathUtils.PI2 * random.nextFloat();
    }

    public float getNoise(float x, float y) {
        int x0 = (int) x % (n - 1);
        int y0 = (int) y % (n - 1);

        x -= MathUtils.floor(x);
        y -= MathUtils.floor(y);

        float dot1 = directions[x0][y0].dot(x, y);
        float dot2 = directions[x0 + 1][y0].dot(x - 1, y);
        float dot3 = directions[x0][y0 + 1].dot(x, y - 1);
        float dot4 = directions[x0 + 1][y0 + 1].dot(x - 1, y - 1);

        x = qunticCurve(x);
        y = qunticCurve(y);

        float ux = lerp(dot3, dot4, x);
        float dx = lerp(dot1, dot2, x);

        return (lerp(dx, ux, y) + 1) / 2;
    }

    public float getNoise(float x, float y, int octaves) {
        float amplitude = 1; // сила применения шума к общей картине, будет уменьшаться с "мельчанием" шума
        // как сильно уменьшаться - регулирует persistence
        float max = 0; // необходимо для нормализации результата
        float result = 0; // накопитель результата

        while (octaves-- > 0) {
            max += amplitude;
            result += getNoise(x, y) * amplitude;
            amplitude /= 2;
            x *= 2; // удваиваем частоту шума (делаем его более мелким) с каждой октавой
            y *= 2;
        }
        return result / max;
    }

}
