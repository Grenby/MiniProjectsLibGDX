package com.mygdx.projects.utils.simpleTensor;

public class Layer {
    int input, output;

    float[][] arr;
    VecN out;

    public Layer(int input, int output) {
        this.input = input;
        this.output = output;
        arr = new float[output][input + 1];
        out = new VecN(output);
    }

    public VecN apply(VecN in) {
        if (in.dim() != input)
            throw new RuntimeException("incorrect size");
        for (int i = 0; i < output; i++) {
            out.arr[i] = 0;
            for (int j = 0; j < input; j++) {
                out.arr[i] += arr[i][j] * in.arr[j];
            }
            out.arr[i] += arr[i][input];
        }
        return out;
    }

    public void rand() {
        for (int i = 0; i < output; i++) {
            for (int j = 0; j < input + 1; j++) {
                arr[i][j] = (float) Math.random();
            }
        }
    }

}
