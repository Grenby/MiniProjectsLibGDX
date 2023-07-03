package com.mygdx.projects.testAI.utils;

import com.badlogic.gdx.math.GridPoint2;

public class PotentialGrid {

    float[] nodes;
    int w, h;
    int[] ox = new int[]{1, 0, -1, -1, 0, 0, 1, 1};
    int[] oy = new int[]{0, 1, 0, 0, -1, -1, 0, 0};

    public PotentialGrid(int w, int h) {
        nodes = new float[w * h];
        this.w = w;
        this.h = h;
    }

    public void add(int x, int y, float val) {
        nodes[x * h + y] += val;
    }

    public void sub(int x, int y, float val) {
        if (Math.abs(val) >= 0.1f)
            nodes[x * h + y] -= val;
    }

    public float getVal(int x, int y) {
        return nodes[x * h + y];
    }

    public int getW() {
        return w;
    }

    public void clear() {
        for (int i = 0; i < w * h; i++) {
            nodes[i] = 0;
        }
    }

    public int getH() {
        return h;
    }

    public GridPoint2 getMin(int x, int y) {
        float v = getVal(x, y);
        GridPoint2 g = new GridPoint2(x, y);
        for (int i = 0; i < ox.length; i++) {
            x += ox[i];
            y += oy[i];
            if (x >= 0 && x < w && y >= 0 && y < h && getVal(x, y) < v) {
                g.set(x, y);
                v = getVal(x, y);
            }
        }
        return g;
    }

    public boolean isCorrect(int x, int y) {
        return (x >= 0) && (x < w) && (y >= 0) && (y < h);
    }

//    public Vector2 getDir(int x, int y){
//        float v = getVal(x,y);
//        GridPoint2 g = new GridPoint2(x,y);
//        for (int i=0;i<ox.length;i++){
//            x+=ox[i];
//            y+=oy[i];
//            if (x>=0 && x<w && y>=0 && y<h && getVal(x,y) < v){
//                g.set(x,y);
//                v = getVal(x,y);
//            }
//        }
//        return g;
//    }

}
