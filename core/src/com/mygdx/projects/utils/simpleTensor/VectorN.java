package com.mygdx.projects.utils.simpleTensor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector;

import java.util.Arrays;

public class VectorN implements Vector<VectorN> {

    private float[] arr = null;

    public VectorN(int n) {
        arr = new float[n];
    }

    public VectorN() {
        arr = new float[0];
    }

    public int dim() {
        return arr.length;
    }

    @Override
    public VectorN cpy() {
        return new VectorN(arr.length);
    }

    @Override
    public float len() {
        return (float) Math.sqrt(len2());
    }

    @Override
    public float len2() {
        float l = 0;
        for (float f : arr) {
            l += f * f;
        }
        return l;
    }

    @Override
    public VectorN limit(float limit) {
        return limit2(limit * limit);
    }

    @Override
    public VectorN limit2(float limit2) {
        float len2 = len2();
        if (len2 > limit2) {
            return scl((float) Math.sqrt(limit2 / len2));
        }
        return this;
    }

    @Override
    public VectorN setLength(float len) {
        return setLength2(len * len);
    }

    @Override
    public VectorN setLength2(float len2) {
        float oldLen2 = len2();
        return (oldLen2 == 0 || oldLen2 == len2) ? this : scl((float) Math.sqrt(len2 / oldLen2));
    }

    @Override
    public VectorN clamp(float min, float max) {
        final float len2 = len2();
        if (len2 == 0f) return this;
        float max2 = max * max;
        if (len2 > max2) return scl((float) Math.sqrt(max2 / len2));
        float min2 = min * min;
        if (len2 < min2) return scl((float) Math.sqrt(min2 / len2));
        return this;
    }

    @Override
    public VectorN set(VectorN v) {
        if (v.dim() == dim()) {
            if (dim() >= 0) System.arraycopy(v.arr, 0, arr, 0, dim());
        }
        return this;
    }

    @Override
    public VectorN sub(VectorN v) {
        if (v.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                arr[i] -= v.arr[i];
            }
        }
        return this;
    }

    @Override
    public VectorN nor() {
        float l = len();
        if (l == 0)
            return this;
        return scl(1 / l);
    }

    @Override
    public VectorN add(VectorN v) {
        if (v.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                arr[i] += v.arr[i];
            }
        }
        return this;
    }

    @Override
    public float dot(VectorN v) {
        float res = 0;
        if (v.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                res += v.arr[i] * arr[i];
            }
        }
        return res;
    }

    @Override
    public VectorN scl(float scalar) {
        for (int i = 0; i < dim(); i++) {
            arr[i] *= scalar;
        }
        return this;
    }

    @Override
    public VectorN scl(VectorN v) {
        if (v.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                arr[i] *= v.arr[i];
            }
        }
        return this;
    }

    @Override
    public float dst(VectorN v) {
        return (float) Math.sqrt(dst2(v));
    }

    @Override
    public float dst2(VectorN v) {
        return 0;
    }

    @Override
    public VectorN lerp(VectorN target, float alpha) {
        return null;
    }

    @Override
    public VectorN interpolate(VectorN target, float alpha, Interpolation interpolator) {
        return null;
    }

    @Override
    public VectorN setToRandomDirection() {
        return null;
    }

    @Override
    public boolean isUnit() {
        return false;
    }

    @Override
    public boolean isUnit(float margin) {
        return false;
    }

    @Override
    public boolean isZero() {
        return false;
    }

    @Override
    public boolean isZero(float margin) {
        return false;
    }

    @Override
    public boolean isOnLine(VectorN other, float epsilon) {
        return false;
    }

    @Override
    public boolean isOnLine(VectorN other) {
        return false;
    }

    @Override
    public boolean isCollinear(VectorN other, float epsilon) {
        return false;
    }

    @Override
    public boolean isCollinear(VectorN other) {
        return false;
    }

    @Override
    public boolean isCollinearOpposite(VectorN other, float epsilon) {
        return false;
    }

    @Override
    public boolean isCollinearOpposite(VectorN other) {
        return false;
    }

    @Override
    public boolean isPerpendicular(VectorN other) {
        return false;
    }

    @Override
    public boolean isPerpendicular(VectorN other, float epsilon) {
        return false;
    }

    @Override
    public boolean hasSameDirection(VectorN other) {
        return false;
    }

    @Override
    public boolean hasOppositeDirection(VectorN other) {
        return false;
    }

    @Override
    public boolean epsilonEquals(VectorN other, float epsilon) {
        return false;
    }

    @Override
    public VectorN mulAdd(VectorN v, float scalar) {
        if (v.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                arr[i] += v.arr[i] * scalar;
            }
        }
        return this;
    }

    @Override
    public VectorN mulAdd(VectorN v, VectorN mulVec) {
        if (v.dim() == dim() && mulVec.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                arr[i] += v.arr[i] * mulVec.arr[i];
            }
        }
        return this;
    }

    @Override
    public VectorN setZero() {
        Arrays.fill(arr, 0);
        return this;
    }


}
