package com.mygdx.projects.utils;

public class FPSCounter {

    private float time = 0;
    private int step = 0;
    private int maxStep = 30;
    private float fps = 30.0f;

    public FPSCounter() {
    }

    public FPSCounter(int maxStep) {
        this.maxStep = maxStep;
    }

    public float getFps() {
        return (int) (10 * fps) / 10.f;
    }

    public int getMaxStep() {
        return maxStep;
    }

    public void setMaxStep(int maxStep) {
        this.maxStep = maxStep;
    }

    public void update(float delta) {
        time += delta;
        step++;
        if (step >= maxStep) {
            if (time != 0) {
                fps = step / time;
            }
            time = 0;
            step = 0;
        }
    }

}
