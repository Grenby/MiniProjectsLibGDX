package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.projects.utils.MyMath;

public class PerlinNoise {
    private final RandomXS128 random = new RandomXS128();
    private final int[] transitions;
    private final Vector2[] directions;
    private final int n;

    public PerlinNoise() {
        this(256);
    }

    public PerlinNoise(int n) {
        this.n = n;
        directions = new Vector2[n];
        transitions = new int[2 * n];
        for (int i = 0; i < n; i++) {
            transitions[i] = i;
            directions[i] = new Vector2();
            MyMath.angleToVector(directions[i], getAngle());
        }
        for (int i = 0; i < n; i++) {
            int j = transitions[i];
            transitions[i] = random.nextInt(n);
            transitions[transitions[i]] = j;
        }
        System.arraycopy(transitions, 0, transitions, n, n);
    }

    private float qunticCurve(float t) {
        //return t*t + Math.signum(t) * t -1;
        //return (float)Math.sqrt( t * t * t * (t * (t * 6 - 15) + 10) * ((float) Math.cos(t*Math.PI/2)+1)/2);
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private float getAngle() {
        return MathUtils.PI2 * random.nextFloat();
    }

    private int hash(int x, int y) {
        return transitions[transitions[x] + y];
    }

    public float getNoise(float x, float y) {
        int x0 = (int) x % (n - 1);
        int y0 = (int) y % (n - 1);

        x -= MathUtils.floor(x);
        y -= MathUtils.floor(y);

        float dot1 = directions[hash(x0, y0)].dot(x, y);
        float dot2 = directions[hash(x0 + 1, y0)].dot(x - 1, y);
        float dot3 = directions[hash(x0, y0 + 1)].dot(x, y - 1);
        float dot4 = directions[hash(x0 + 1, y0 + 1)].dot(x - 1, y - 1);

        x = qunticCurve(x);
        y = qunticCurve(y);

        float ux = lerp(dot3, dot4, x);
        float dx = lerp(dot1, dot2, x);

        return (lerp(dx, ux, y) + 1) / 2;
    }

    public float getNoise(float x, float y, int octaves, float persistence) {
        float amplitude = 1; // сила применения шума к общей картине, будет уменьшаться с "мельчанием" шума
        // как сильно уменьшаться - регулирует persistence
        float max = 0; // необходимо для нормализации результата
        float result = 0; // накопитель результата

        while (octaves-- > 0) {
            max += amplitude;
            result += getNoise(x, y) * amplitude;
            amplitude *= persistence;
            x /= persistence; // удваиваем частоту шума (делаем его более мелким) с каждой октавой
            y /= persistence;
        }
        return result / max;
    }

    public static PerlinNoise create(int n) {
        return new PerlinNoise(n);
    }

    public static PerlinNoise create() {
        return new PerlinNoise(256);
    }


}
