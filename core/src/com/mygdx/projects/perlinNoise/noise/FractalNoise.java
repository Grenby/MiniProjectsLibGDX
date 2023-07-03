package com.mygdx.projects.perlinNoise.noise;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;

public class FractalNoise {

    private final RandomXS128 random = new RandomXS128();
    private final int[] transitions;
    private final float[] values;
    private final int n;

    public FractalNoise() {
        this(256);
    }

    public FractalNoise(int n) {
        this.n = n;
        values = new float[n];
        transitions = new int[2 * n];
        for (int i = 0; i < n; i++) {
            transitions[i] = i;
            values[i] = random.nextFloat();
        }
        for (int i = 0; i < n; i++) {
            int j = transitions[i];
            transitions[i] = random.nextInt(n);
            transitions[transitions[i]] = j;
        }
        System.arraycopy(transitions, 0, transitions, n, n);
    }

    private float qunticCurve(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private int hash(int x, int y) {
        return transitions[transitions[x] + y];
    }

    public float getNoise(float x, float y) {
        int x0 = (int) x % (n - 1);
        int y0 = (int) y % (n - 1);

        x -= MathUtils.floor(x);
        y -= MathUtils.floor(y);

        x = qunticCurve(x);
        y = 1 - qunticCurve(y);

        float ux = lerp(values[transitions[hash(x0, y0)]], values[transitions[hash(x0 + 1, y0)]], x);
        float dx = lerp(values[transitions[hash(x0, y0 + 1)]], values[transitions[hash(x0 + 1, y0 + 1)]], x);

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


}
