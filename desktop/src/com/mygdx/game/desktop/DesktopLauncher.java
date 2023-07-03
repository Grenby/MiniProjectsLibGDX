package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.projects.Start;

public class DesktopLauncher {


    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //config.fullscreen = true;
        //config.vSyncEnabled = true;
        //config.width = 1920;
        //config.height = 1080;
        new LwjglApplication(new Start(), config);
    }

}
