package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;

public class TestNoise {

    private final RandomXS128 random = new RandomXS128();
    private final Vector3[][] directions;
    private final int n;
    private int neighbour = 0;

    public TestNoise() {
        this(1024);
    }

    public TestNoise(int n) {
        this.n = n;
        directions = new Vector3[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                directions[i][j] = setRandom(new Vector3());
            }
        }
    }

    Vector3 tmp = new Vector3();
    Vector3 tmp1 = new Vector3();

    Vector3 setRandom(Vector3 v) {
        float a1 = 2 * MathUtils.PI * random.nextFloat();
        float a2 = 2 * MathUtils.PI * random.nextFloat();

        float r1 = 0.8f;
        float r2 = 0.2f;

        v.x = r1;
        v.rotateRad(a1, 0, 0, 1);

        tmp.set(v).crs(0, 0, 1).nor();
        tmp1.set(v).nor().scl(r2).rotateRad(a2, tmp.x, tmp.y, tmp.z);
        v.add(tmp1);

        return v;
    }

    private float qunticCurve(float t) {
        return t * t;
        //return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    private float getAngle() {
        return MathUtils.PI2 * random.nextFloat();
    }

    public float getNoise(float x, float y, float v) {
        int x0 = (int) x % (n - 1);
        int y0 = (int) y % (n - 1);

        x -= MathUtils.floor(x);
        y -= MathUtils.floor(y);
        float s = 1;
        float dot1 = directions[x0][y0].dot(x, y, v);
        float dot2 = directions[x0 + 1][y0].dot(x - 1, y, v);
        float dot3 = directions[x0][y0 + 1].dot(x, y - 1, v);
        float dot4 = directions[x0 + 1][y0 + 1].dot(x - 1, y - 1, v);

        x = qunticCurve(x);
        y = qunticCurve(y);

        float ux = lerp(dot3, dot4, x);
        float dx = lerp(dot1, dot2, x);

        return (lerp(dx, ux, y) + 1) / 2 * s;
    }

    public float getNoise(float x, float y, int octaves, float persistence) {
        float amplitude = 1; // сила применения шума к общей картине, будет уменьшаться с "мельчанием" шума
        // как сильно уменьшаться - регулирует persistence
        float max = 0; // необходимо для нормализации результата
        float result = 0; // накопитель результата

        while (octaves-- > 0) {
            max += amplitude;
            result += getNoise(x, y, (float) Math.atan(result / max)) * amplitude;
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
