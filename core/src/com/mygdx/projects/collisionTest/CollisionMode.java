package com.mygdx.projects.collisionTest;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSui;

public class CollisionMode extends ScreenAdapter {

    final int WEIGHT = Gdx.graphics.getWidth();
    final int HEIGHT = Gdx.graphics.getHeight();

    final float W = 10f;
    final float H = W / WEIGHT * HEIGHT;

    ShapeRenderer renderer = new ShapeRenderer();
    OrthographicCamera camera = new OrthographicCamera(W, H);

    Stage ui = new Stage();
    CircleWorld world = new CircleWorld(-W / 2, -H / 2, W / 2, H / 2);

    float x1 = 10;
    float x2 = 10;
    float vx = 1;
    float ax = 1;
    float time = 0;


    @Override
    public void show() {
        initUI();
        Gdx.input.setInputProcessor(ui);
        for (int i = 0; i < 500; i++) {
            CircleObj obj = new CircleObj();
            obj.radius = 0.1f;
            obj.x = MathUtils.random() * (W - 2) - (W - 2) / 2;
            obj.y = MathUtils.random() * (H - 2) - (H - 2) / 2;

            float angle = MathUtils.random() * MathUtils.PI2;
            obj.vx = MathUtils.cos(angle) * 2f;
            obj.vy = MathUtils.sin(angle) * 2f;

            world.addCircle(obj);
        }

        CircleObj obj = new CircleObj();
        obj.x = -0.1f;
        obj.y = -0.1f;
        obj.radius = 0.5f;
        obj.m = 50;
        world.addCircle(obj);
    }

    @Override
    public void render(float delta) {
//        System.out.println("Start");
//        x1 = x1 + vx*delta + ax*delta*delta/2;
//        ax -=1*delta;
//        vx = vx + delta* ax;
//        x2 = x2 + vx*delta;
//        time +=delta;
//
//        System.out.println(x1 +" " + x2 + " " + (x1-x2) +" " + vx + " " + time);
        ui.act(delta);
        world.update(delta);

        ScreenUtils.clear(Color.BLACK);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            for (CircleObj c : world.circles) {
                renderer.circle(c.x, c.y, c.radius, 50);
            }
        }
        renderer.end();
        ui.draw();
    }

    private void initUI() {
        Skin skin = Resource.getUISkin();
        VerticalGroup root = new VerticalGroup();
        VerticalGroup verticalGroup = new VerticalGroup();

        root.addActor(verticalGroup);
        HorizontalGroup horizontalGroup = new HorizontalGroup();

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

}
