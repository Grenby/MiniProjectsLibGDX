package com.mygdx.projects.utils.simpleTensor;

public class VecN {
    public float[] arr;

    public VecN(int dim) {
        arr = new float[dim];
    }


    public int dim() {
        return arr.length;
    }

    public VecN mul(MatN matN) {

        return this;
    }

    public VecN scl(VecN v) {
        if (v.dim() == dim()) {
            for (int i = 0; i < dim(); i++) {
                arr[i] *= v.arr[i];
            }
        }
        return this;
    }

    public float conv() {
        float res = 0;
        for (float f : arr) {
            res += f;
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        for (int i = 0; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != arr.length - 1)
                builder.append("f,");
            else
                builder.append("f)");
        }
        return builder.toString();
    }
}
