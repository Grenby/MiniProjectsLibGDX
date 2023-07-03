package com.mygdx.projects.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.mygdx.projects.Resource;

public final class Screenshot {

    private static int count = 0;

    private Screenshot() {

    }

    public static void save() {
        FileHandle fh;
        do {
            fh = new FileHandle(Gdx.files.getLocalStoragePath() + "screenshots/screenshot_" + count++ + ".png");
        } while (fh.exists());
        Resource.LOG.info("Screenshot", "screenshot_" + (count - 1) + ".png");
        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        for (int i = 0; i < pixmap.getHeight() / 2; i++) {
            for (int j = 0; j < pixmap.getWidth(); j++) {
                int a = pixmap.getPixel(j, i);
                pixmap.drawPixel(j, i, pixmap.getPixel(j, pixmap.getHeight() - 1 - i));
                pixmap.drawPixel(j, pixmap.getHeight() - i - 1, a);
            }
        }
        PixmapIO.writePNG(fh, pixmap);
        pixmap.dispose();
    }

}
