package com.mygdx.projects.utils.simpleTensor;

public class MatN {

    float[] arr;
    int n;

    public float get(int i, int j) {
        return arr[i * n + j];
    }

}
