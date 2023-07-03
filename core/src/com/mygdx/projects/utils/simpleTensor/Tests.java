package com.mygdx.projects.utils.simpleTensor;

public class Tests {

    Layer[] layers = new Layer[3];

    public static void main(String[] arg) {
        Tests tests = new Tests();
        tests.todo();

    }

    void todo() {
        int[] size = new int[]{2, 4, 4, 2};
        VecN input = new VecN(size[0]);
        input.arr[0] = 1;
        input.arr[1] = 1;


        for (int i = 0; i < layers.length; i++) {
            layers[i] = new Layer(size[i], size[i + 1]);
            layers[i].rand();
        }

        FuncN funcN = new FuncN((f) -> (float) (1 / (1 + Math.exp(f))));
        //Function<Float, Float> err (vec)
        VecN out = input;

        for (Layer layer : layers) {
            out = layer.apply(out);
            out = funcN.apply(out, out);
        }
        System.out.println(out);


    }

}
