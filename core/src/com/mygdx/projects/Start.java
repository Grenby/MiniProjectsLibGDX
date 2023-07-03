package com.mygdx.projects;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.mygdx.projects.sphere.SphereMode;
import com.mygdx.projects.utils.Screenshot;


public class Start extends Game {

    String currentPackage = "";

    public Start() {
    }

    public Start(Screen screen) {
        setScreen(screen);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        Resource.WIDTH = width;
        Resource.HEIGHT = height;
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.PRINT_SCREEN)) {
            Screenshot.save(currentPackage);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (getScreen() != null)
                getScreen().dispose();
            Gdx.app.exit();
        } else {
            super.render();
        }
    }


    @Override
    public void create() {
        Resource.init();
        //Resource.instance().loadDefaultTextures();
        setScreen(new SphereMode());
    }

    @Override
    public void setScreen(Screen screen) {
        super.setScreen(screen);
        String[] name = screen.getClass().getPackage().getName().split("\\.");
        currentPackage = name[name.length-1];
    }
}
