package com.mygdx.projects.RayM.dimension2;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

public class Input implements InputProcessor {

    private RayMScreen1 screen;
    private final Vector2 tmp = new Vector2(0, 0);

    private boolean[] keys = new boolean[4];

    Input(RayMScreen1 screen) {
        this.screen = screen;
    }

    @Override
    public boolean keyDown(int keycode) {
        tmp.setZero();
        switch (keycode) {
            case com.badlogic.gdx.Input.Keys.ESCAPE: {
                Gdx.app.exit();
                break;
            }
            case com.badlogic.gdx.Input.Keys.UP: {
                keys[0] = true;
                break;
            }
            case com.badlogic.gdx.Input.Keys.RIGHT: {
                keys[1] = true;
                break;
            }
            case com.badlogic.gdx.Input.Keys.DOWN: {
                keys[2] = true;
                break;
            }
            case com.badlogic.gdx.Input.Keys.LEFT: {
                keys[3] = true;
                break;
            }
        }
        if (!tmp.isZero()) {
            screen.addPosition(tmp.nor().scl(2));
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case com.badlogic.gdx.Input.Keys.UP: {
                keys[0] = false;
                break;
            }
            case com.badlogic.gdx.Input.Keys.RIGHT: {
                keys[1] = false;
                break;
            }
            case com.badlogic.gdx.Input.Keys.DOWN: {
                keys[2] = false;
                break;
            }
            case com.badlogic.gdx.Input.Keys.LEFT: {
                keys[3] = false;
                break;
            }
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screen.move(screenX, screenY);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        screen.setPoint(screenX, screenY);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public Vector2 getAdd() {
        tmp.setZero();
        if (keys[0]) tmp.add(0, 1);
        if (keys[1]) tmp.add(1, 0);
        if (keys[2]) tmp.add(0, -1);
        if (keys[3]) tmp.add(-1, 0);
        if (!tmp.isZero()) tmp.nor();
        return tmp;
    }

    public void update() {

    }

}
