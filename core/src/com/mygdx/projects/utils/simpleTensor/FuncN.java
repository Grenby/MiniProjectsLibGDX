package com.mygdx.projects.utils.simpleTensor;

import java.util.function.Function;

public class FuncN {

    Function<Float, Float> function;

    public FuncN(Function<Float, Float> function) {
        this.function = function;
    }

    public VecN apply(VecN v, VecN res) {
        if (v.dim() != res.dim())
            throw new RuntimeException("Array sizes aren't equal");
        for (int i = 0; i < v.dim(); i++) {
            res.arr[i] = function.apply(v.arr[i]);
        }
        return res;
    }

}
