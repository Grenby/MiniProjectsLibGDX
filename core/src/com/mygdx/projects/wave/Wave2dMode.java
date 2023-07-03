package com.mygdx.projects.wave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.MyScreen;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSui;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Wave2dMode extends MyScreen {

    float energy = 0;

    ShapeRenderer renderer = new ShapeRenderer();
    OrthographicCamera camera = new OrthographicCamera(W, H);

    List<Cell2d> cells = new ArrayList<>(100);

    Stage ui = new Stage();

    void update(float delta) {
        for (Cell2d cell2d : cells) {
            cell2d.update(delta);
        }

        for (Cell2d cell2d : cells) {
            cell2d.move();
        }
    }

    private void initUI() {
        Skin skin = Resource.getUISkin();
        VerticalGroup root = new VerticalGroup();
        VerticalGroup verticalGroup = new VerticalGroup();

        root.addActor(verticalGroup);
        HorizontalGroup horizontalGroup = new HorizontalGroup();
        {
            final Label textValue = new Label("Energy: " + energy, skin) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    float e = 0;
                    for (Cell2d cell2d : cells) {
                        e += cell2d.mass * cell2d.velocity * cell2d.velocity / 2;
                        if (!Objects.isNull(cell2d.l)) {
                            float d = cell2d.position.y - cell2d.l.position.y;
                            d *= d;
                            e += cell2d.k * d / 2;
                        }
                        if (!Objects.isNull(cell2d.r)) {
                            float d = cell2d.position.y - cell2d.r.position.y;
                            d *= d;
                            e += cell2d.k * d / 2;
                        }
                    }
                    setText("Energy: " + e);
                }
            };
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
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
        verticalGroup.addActor(new FPSui(skin));
        verticalGroup.left();
        verticalGroup.columnLeft();
        root.setPosition(0, HEIGHT - verticalGroup.getHeight());
        root.left();
        root.columnLeft();
        ui.addActor(root);
    }

    @Override
    public void show() {
        resize(1000f);
        Gdx.input.setInputProcessor(new InputMultiplexer(ui));
        initUI();
        camera.position.set(0, 0, 0);
        camera.update();

        for (int i = 0; i < 1000; i++) {
            Cell2d cell2d = new Cell2d();
            cell2d.position.set(-500 + i, 0);
            if (i != 0) {
                cell2d.l = cells.get(i - 1);
                cells.get(i - 1).r = cell2d;
            }
            cells.add(cell2d);
        }
        for (int i = 0; i < 50; i++) {
            cells.get(i).position.y = 50 - i;
        }
        for (int i = 0; i < 50; i++) {
            cells.get(1000 - 1 - i).isInf = true;
        }
    }

    @Override
    public void render(float delta) {
        ui.act(delta);
        for (int i = 0; i < 100; i++) {
            update(delta);
        }


        ScreenUtils.clear(Color.BLACK);


        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);

        for (Cell2d cell2d : cells) {
            Vector2 position = cell2d.position;
            float x = position.x - 0.5f;
            float y = position.y - 0.5f;
            renderer.rect(x, y, 1, 1);
        }

        renderer.end();
        ui.draw();
    }
}
