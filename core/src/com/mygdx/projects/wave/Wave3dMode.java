package com.mygdx.projects.wave;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSui;

import java.util.Objects;

public class Wave3dMode extends ScreenAdapter {

    final int WEIGHT = Gdx.graphics.getWidth();
    final int HEIGHT = Gdx.graphics.getHeight();

    final float W = 100f;
    final float H = W / WEIGHT * HEIGHT;

    float energy = 0;

    ShapeRenderer renderer = new ShapeRenderer();
    private final PerspectiveCamera camera = new PerspectiveCamera(67, W, H);
    private FirstPersonCameraController controller;

    Cell3d[][] cells = new Cell3d[100][100];

    Stage ui = new Stage();


    void update(float delta) {
        for (Cell3d[] cells1 : cells) {
            for (Cell3d cell3d : cells1)
                cell3d.update(delta);
        }

        for (Cell3d[] cells1 : cells) {
            for (Cell3d cell3d : cells1)
                cell3d.move();
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
                    for (Cell3d[] cell : cells) {
                        for (Cell3d cell2d : cell) {
                            e += cell2d.mass * cell2d.velocity * cell2d.velocity / 2;
                            if (!Objects.isNull(cell2d.l)) {
                                float d = cell2d.position.z - cell2d.l.position.z;
                                d *= d;
                                e += cell2d.k * d / 2;
                            }
                            if (!Objects.isNull(cell2d.r)) {
                                float d = cell2d.position.z - cell2d.r.position.z;
                                d *= d;
                                e += cell2d.k * d / 2;
                            }
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
        controller = new FirstPersonCameraController(camera);
        Gdx.input.setInputProcessor(new InputMultiplexer(ui, controller));

        initUI();
        camera.position.set(0, 0, 0);
        camera.update();

        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                Cell3d c = new Cell3d();
                c.position.set(i, j, 0);
                if (i != 0) {
                    c.l = cells[i - 1][j];
                    cells[i - 1][j].r = c;
                }
                if (j != 0) {
                    c.d = cells[i][j - 1];
                    cells[i][j - 1].u = c;
                }
                cells[i][j] = c;
            }
        }
        cells[0][0].position.z = 5;
        cells[99][99].position.z = -5;
        renderer.getTransformMatrix()
                .rotateRad(1, 0, 0, -MathUtils.PI / 2f)
                .rotateRad(0, 0, 1, -MathUtils.PI / 2f);
        camera.position.set(20, 5, 30);
        camera.lookAt(22, 5, 28);
        camera.update();
    }

    @Override
    public void render(float delta) {
        ui.act(delta);
        for (int i = 0; i < 1; i++) {
            update(delta);
        }
        controller.update();

        ScreenUtils.clear(Color.BLACK);


        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.WHITE);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                Cell3d c = cells[i][j];
                float x = c.position.x - 0.5f;
                float y = c.position.y - 0.5f;
                float z = c.position.z - 0.5f;
                renderer.box(x, y, z, 1, 1, 1);
            }
        }

        renderer.end();
        ui.draw();
    }
}
