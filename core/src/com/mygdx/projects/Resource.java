package com.mygdx.projects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.Logger;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.projects.utils.ColoredLogger;

public class Resource {

    public static int WIDTH = Gdx.graphics.getWidth();
    public static int HEIGHT = Gdx.graphics.getHeight();

    public static final Logger LOG = new ColoredLogger();

    private final String MY_TAG = Resource.class.getSimpleName();

    private static final String FONT = "fonts/font.ttf";

    private static Resource res;
    private static boolean initRes = false;

    private final AssetManager assetManager;
    private final Skin uiSkin;

    private Resource() {
        assetManager = new AssetManager();

        AssetManager manager = this.assetManager;
        manager.load("source/uiskin.json", Skin.class,
                new SkinLoader.SkinParameter("source/uiskin.atlas"));
        while (!manager.update(10)) {
            LOG.info(MY_TAG, "load...");
        }
        uiSkin = manager.get("source/uiskin.json");

    }

    public void loadFont() {
        AssetManager manager = this.assetManager;
        if (manager.isLoaded(FONT))
            return;
        //set loader
        FileHandleResolver resolver = new InternalFileHandleResolver();
        manager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        manager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        //load font
        FreetypeFontLoader.FreeTypeFontLoaderParameter mySmallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        mySmallFont.fontFileName = FONT;
        mySmallFont.fontParameters.size = 20;
        manager.load(FONT, BitmapFont.class, mySmallFont);
        while (!manager.update(10)) {
            LOG.info(MY_TAG, "load fonts...");
        }
        LOG.info(MY_TAG, "font loaded");
    }

    public static AssetManager manager() {
        return res.assetManager;
    }

    public static void init() {
        if (!initRes) {
            initRes = true;
            res = new Resource();
            //res.loadFont();
        }
    }

    public static Resource instance() {
        return res;
    }

    public static BitmapFont getFont() {
        return res.assetManager.get(FONT, BitmapFont.class);
    }

    public static Skin getUISkin() {
        return res.uiSkin;
    }

}
