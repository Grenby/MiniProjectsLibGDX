package com.mygdx.projects.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.projects.Resource;

public class TestUI implements Screen {

    private final Stage uiStage = new Stage();

    void initUI() {
//        Skin skin = Resource.getUISkin();
//        VerticalGroup root = new VerticalGroup();
//        VerticalGroup verticalGroup = new VerticalGroup();
//        TextButton textButton = new TextButton("info",skin,"toggle");
//
//        verticalGroup.setVisible(false);
//
//        textButton.addListener(new InputListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                verticalGroup.setVisible(!textButton.isChecked());
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        });
//
//        uiStage.addActor(textButton);
//        root.addActor(verticalGroup);
//        Slider slider;
//        HorizontalGroup horizontalGroup;
//        {
//            final Label textValue = new Label(Integer.toString(octaves), skin);
//            slider = new Slider(1, 16, 1, false, skin) {
//                @Override
//                public boolean setValue(float value) {
//                    boolean set = super.setValue(value);
//                    textValue.setText(Integer.toString((int) getValue()));
//                    octaves = (int) getValue();
//                    return set;
//                }
//            };
//            slider.setValue(octaves);
//            horizontalGroup = new HorizontalGroup();
//            horizontalGroup.addActor(new Label("Octaves: ", skin));
//            horizontalGroup.addActor(slider);
//            horizontalGroup.addActor(textValue);
//            verticalGroup.addActor(horizontalGroup);
//        }
//        {
//            final Label textValue = new Label(Float.toString(cellSize), skin);
//            slider = new Slider(1, 255, 1, false, skin) {
//                @Override
//                public boolean setValue(float value) {
//                    boolean set = super.setValue(value);
//                    textValue.setText(Integer.toString((int)getValue()));
//                    cellSize =getValue();
//                    return set;
//                }
//            };
//            slider.setValue(cellSize);
//            horizontalGroup = new HorizontalGroup();
//            horizontalGroup.addActor(new Label("Size: ", skin));
//            horizontalGroup.addActor(slider);
//            horizontalGroup.addActor(textValue);
//            verticalGroup.addActor(horizontalGroup);
//        }
//        {
//            final Label textValue = new Label(Float.toString(amplitude), skin);
//            slider = new Slider(0.01f, 0.99f, 0.01f, false, skin) {
//                @Override
//                public boolean setValue(float value) {
//                    boolean set = super.setValue(value);
//                    textValue.setText(Float.toString((int)(100*getValue())/100f));
//                    amplitude = getValue();
//                    return set;
//                }
//            };
//            slider.setValue(amplitude);
//            horizontalGroup = new HorizontalGroup();
//            horizontalGroup.addActor(new Label("Amplitude: ", skin));
//            horizontalGroup.addActor(slider);
//            horizontalGroup.addActor(textValue);
//            verticalGroup.addActor(horizontalGroup);
//        }
//        {
//            final Label textValue = new Label(Float.toString(frequency), skin);
//            slider = new Slider(1, 20, 0.05f, false, skin) {
//                @Override
//                public boolean setValue(float value) {
//                    boolean set = super.setValue(value);
//                    textValue.setText(Float.toString((int)(100*getValue())/100f));
//                    frequency = getValue();
//                    return set;
//                }
//            };
//            slider.setValue(frequency);
//            horizontalGroup = new HorizontalGroup();
//            horizontalGroup.addActor(new Label("Frequency: ", skin));
//            horizontalGroup.addActor(slider);
//            horizontalGroup.addActor(textValue);
//            verticalGroup.addActor(horizontalGroup);
//        }
//        verticalGroup.addActor(new FPSui(skin));
//        verticalGroup.left();
//        verticalGroup.columnLeft();
//        root.setPosition(0,HEIGHT - verticalGroup.getHeight());
//        root.left();
//        root.columnLeft();
//        uiStage.addActor(root);

    }

    @Override
    public void show() {
        //initUI();
        uiStage.setDebugAll(true);
        Gdx.input.setInputProcessor(uiStage);
        Skin skin = Resource.getUISkin();

        ScrollPane scrollPane = new ScrollPane(null, skin);
        {
            VerticalGroup verticalGroup = new VerticalGroup();
            scrollPane.setActor(verticalGroup);
            for (int i = 0; i < 100; i++) {
                verticalGroup.addActor(new Label("L" + 1, skin));
            }
        }

        VerticalGroup vGroup = new VerticalGroup();
        TextButton.TextButtonStyle style = skin.get(TextButton.TextButtonStyle.class);
        TextButton textButton = new TextButton("Hello", skin);
//        Table table = new Table();
//        table.
        textButton.setPosition(0, -10);
        Window window = new Window("Setting", skin);
        window.setMovable(false);
        //window.add(scrollPane);
        window.setPosition(Gdx.graphics.getWidth() - window.getPrefWidth(), Gdx.graphics.getHeight() - window.getPrefHeight());
        textButton.setPosition(0, 0);
        textButton.setWidth(100);
        textButton.setPosition(0, 0);
        vGroup.addActor(textButton);
        vGroup.setPosition(100, 100);
        textButton.getPrefHeight();

        //uiStage.addActor(new Container<>());
        uiStage.addActor(window);
    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        uiStage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
