package com.mygdx.projects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;

public class MyScreen extends ScreenAdapter {

    protected int WEIGHT = Gdx.graphics.getWidth();
    protected int HEIGHT = Gdx.graphics.getHeight();

    protected float W = 10f;
    protected float H = W / WEIGHT * HEIGHT;

    protected void resize(float w) {
        W = w;
        H = W / WEIGHT * HEIGHT;
    }


}
