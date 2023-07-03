package com.mygdx.projects.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

public class MyInput implements InputProcessor {

    private final ObjectMap<Integer, Runnable> callbacks = new ObjectMap<>();
    private final ObjectSet<Integer> keysForUpdate = new ObjectSet<>();
    private final ObjectSet<Integer> pressedKeys = new ObjectSet<>();
    private TouchDown touchDown = null;
    private TouchUp touchUp = null;
    private TouchDragged touchDragged = null;
    private MouseMoved mouseMoved = null;
    private Scrolled scrolled = null;

    public void addCallback(int keycode, Runnable runnable) {
        callbacks.put(keycode, runnable);
    }

    public void removeCallback(int keycode) {
        callbacks.remove(keycode);
    }

    public void addKey(int keycode) {
        keysForUpdate.add(keycode);
    }

    public void addKeys(int... keycodes) {
        for (int i : keycodes) {
            keysForUpdate.add(i);
        }
    }

    public void removeKey(int keycode) {
        keysForUpdate.remove(keycode);
    }

    public ObjectSet<Integer> getPressedKeys() {
        return pressedKeys;
    }

    public void setTouchDown(TouchDown touchDown) {
        this.touchDown = touchDown;
    }

    public void setTouchUp(TouchUp touchUp) {
        this.touchUp = touchUp;
    }

    public void setTouchDragged(TouchDragged touchDragged) {
        this.touchDragged = touchDragged;
    }

    public void setMouseMoved(MouseMoved mouseMoved) {
        this.mouseMoved = mouseMoved;
    }

    public void setScrolled(Scrolled scrolled) {
        this.scrolled = scrolled;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean res = false;
        if (callbacks.containsKey(keycode)) {
            callbacks.get(keycode).run();
            res = true;
        }
        pressedKeys.add(keycode);
        return res;
    }

    @Override
    public boolean keyUp(int keycode) {
        pressedKeys.remove(keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (touchDown != null) {
            touchDown.call(screenX, screenY, pointer, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (touchUp != null) {
            touchUp.call(screenX, screenY, pointer, button);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (touchDragged != null) {
            touchDragged.call(screenX, screenY, pointer);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (mouseMoved != null) {
            mouseMoved.call(screenX, screenY);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (scrolled != null) {
            scrolled.call(amountX, amountY);
            return true;
        }
        return false;
    }

    public interface Scrolled {
        void call(float amountX, float amountY);
    }

    public interface MouseMoved {
        void call(int screenX, int screenY);
    }

    public interface TouchDragged {
        void call(int screenX, int screenY, int pointer);
    }

    public interface TouchUp {
        void call(int screenX, int screenY, int pointer, int button);
    }

    public interface TouchDown {
        void call(int screenX, int screenY, int pointer, int button);
    }

}
