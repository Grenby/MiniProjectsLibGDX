package com.mygdx.projects.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.StringBuilder;

public class FPSui extends Label {

    private final String F = "fps:";
    private final FPSCounter counter = new FPSCounter(5);
    private final StringBuilder builder = new StringBuilder(9);
    private float fps = counter.getFps();

    public FPSui(Skin skin) {
        super(Float.toString(0), skin);
    }

    public FPSui(Skin skin, String styleName) {
        super(Float.toString(0), skin, styleName);
    }

    public FPSui(Skin skin, String fontName, Color color) {
        super(Float.toString(0), skin, fontName, color);
    }

    public FPSui(Skin skin, String fontName, String colorName) {
        super(Float.toString(0), skin, fontName, colorName);
    }

    public FPSui(LabelStyle style) {
        super(Float.toString(0), style);
    }

    public float getFps() {
        return counter.getFps();
    }

    public int getMaxStep() {
        return counter.getMaxStep();
    }

    public void setMaxStep(int maxStep) {
        counter.setMaxStep(maxStep);
    }

    @Override
    public void act(float delta) {
        if (builder.length == 0) {
            builder.append("fps: 00.0");
        }
        counter.update(delta);
        float newFPS = counter.getFps();
        if (fps != newFPS) {
            fps = newFPS;
            builder.clear();
            builder.append(F);
            builder.append(fps);
            setText(builder.toString());
        }
        super.act(delta);
    }
}
