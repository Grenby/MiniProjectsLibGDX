package com.mygdx.projects.ants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSui;

import java.util.ArrayList;

public class AntsMode implements Screen {

    final int WEIGHT = Gdx.graphics.getWidth();
    final int HEIGHT = Gdx.graphics.getHeight();

    final float W = 500;
    final float H = W / WEIGHT * HEIGHT;


    ShapeRenderer renderer = new ShapeRenderer();
    OrthographicCamera camera = new OrthographicCamera(W, H);

    ArrayList<Ant> ants = new ArrayList<>();
    ArrayList<Ant> antsTmp = new ArrayList<>();
    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();
    private final Vector2 tmp3 = new Vector2();
    private final Vector2 tmp4 = new Vector2();

    ArrayList<Vector2> path = new ArrayList<>();

    float avoidFactor = 2f;
    float matchingFactor = 2f;
    float centeringFactor = 2f;
    float pathFactor = 2f;
    float borderFactor = 2f;


    Stage ui = new Stage();

    @Override
    public void show() {
        initUI();
        for (int i = 0; i < 500; i++) {
            float x = MathUtils.random() * 100;
            float y = MathUtils.random() * 100;
            Ant ant = new Ant();
            ant.position.set(x, y);
            ants.add(ant);
            // ant.velocity.setToRandomDirection().scl(ant.getMaxLinearSpeed());
            //ant.acceleration.setToRandomDirection().scl(5);
        }
        for (int i = 0; i < 100; i++) {
            path.add(new Vector2(MathUtils.random() * W, MathUtils.random() * H));
        }
        camera.position.set(W / 2, H / 2, camera.position.z);
        camera.update();

        Gdx.input.setInputProcessor(ui);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        ui.act(delta);
        for (Ant ant : ants) {
            ant.update(delta);
        }

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            for (Ant ant : ants) {
                update(ant);
                render(ant);
            }
        }
        renderer.end();

        ui.draw();

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

    private void render(Ant ant) {
        tmp1.set(ant.position).mulAdd(ant.direction, 2);
        tmp2.set(ant.position).mulAdd(ant.direction, -0.5f).mulAdd(tmp4.set(ant.direction).rotate90(-1), 1);
        tmp3.set(ant.position).mulAdd(ant.direction, -0.5f).mulAdd(tmp4.set(ant.direction).rotate90(1), 1);
        renderer.triangle(tmp1.x, tmp1.y, tmp2.x, tmp2.y, tmp3.x, tmp3.y);
//        renderer.setColor(Color.GREEN);
//        renderer.circle(ant.position.x,ant.position.y,20);
//        renderer.setColor(Color.RED);
//        renderer.circle(ant.position.x,ant.position.y,10);
        renderer.setColor(Color.WHITE);
    }

    void update(Ant ant) {
        if (ant.position.dst2(ant.targetPosition) < 100) {
            ant.num++;
            if (ant.num == 100)
                ant.num = 0;
            ant.targetPosition.set(path.get(ant.num));
        }
        ant.resetAcceleration();

        float near = 20;
        float tooNear = 10;

        ArrayList<Ant> nears = getNear(near, ant);
        if (nears.size() == 0) {
            return;
        }

        tmp1.set(0, 0);
        tmp2.set(0, 0);
        tmp3.setZero();
        tmp4.setZero();

        near *= near;
        tooNear *= tooNear;

        int num = 0;

        for (Ant a : nears) {
            if (a == ant)
                continue;
            if (a.position.dst2(ant.position) < tooNear) {
                tmp1.add(ant.position).sub(a.position);
                continue;
            }
            num++;
            tmp2.add(a.acceleration);
            tmp3.add(a.position);
        }

        if (num == 0) {
            tmp3.set(0, 0);
            tmp2.set(0, 0);
        } else {
            tmp3.add(ant.position).scl(1f / (num + 1));
            tmp3.sub(ant.position);
            tmp2.scl(1f / num);
        }

        tmp1.nor().scl(ant.getMaxLinearAcceleration());
        tmp2.nor().scl(ant.getMaxLinearAcceleration());
        tmp3.nor().scl(ant.getMaxLinearAcceleration());
        tmp4.set(ant.targetPosition).sub(ant.position).nor().scl(ant.getMaxLinearAcceleration());
        ant.mulAddAcceleration(tmp1, avoidFactor).mulAddAcceleration(tmp2, matchingFactor).mulAddAcceleration(tmp3, centeringFactor).mulAddAcceleration(tmp4, pathFactor);
        ant.acceleration.limit(ant.getMaxLinearAcceleration());

        if (ant.position.x < 10) {
            ant.acceleration.x += borderFactor * ant.getMaxLinearSpeed();
        }
        if (ant.position.x > W - 10) {
            ant.acceleration.x -= borderFactor * ant.getMaxLinearSpeed();
        }
        if (ant.position.y > H - 10) {
            ant.acceleration.y -= borderFactor * ant.getMaxLinearSpeed();
        }
        if (ant.position.y < 10) {
            ant.acceleration.y += borderFactor * ant.getMaxLinearSpeed();
        }
    }

    ArrayList<Ant> getNear(float r, Ant ant) {
        antsTmp.clear();
        float r2 = r * r;
        for (Ant a : ants) {
            if (a == ant)
                continue;
            if (a.position.dst2(ant.position) < r2 && a.direction.dot(ant.direction) > 0)
                antsTmp.add(a);
        }
        return antsTmp;
    }

    private void initUI() {
        Skin skin = Resource.getUISkin();
        VerticalGroup root = new VerticalGroup();
        VerticalGroup verticalGroup = new VerticalGroup();
        HorizontalGroup horizontalGroup;
        Slider slider;
        root.addActor(verticalGroup);

        {
            final Label textValue = new Label(Float.toString(avoidFactor), skin);
            slider = new Slider(0.f, 5, 0.01f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    avoidFactor = getValue();
                    return set;
                }
            };
            slider.setValue(avoidFactor);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Avoid factor: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
        {
            final Label textValue = new Label(Float.toString(matchingFactor), skin);
            slider = new Slider(0.f, 5, 0.01f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    matchingFactor = getValue();
                    return set;
                }
            };
            slider.setValue(avoidFactor);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Matching factor: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
        {
            final Label textValue = new Label(Float.toString(centeringFactor), skin);
            slider = new Slider(0.f, 5f, 0.01f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    centeringFactor = getValue();
                    return set;
                }
            };
            slider.setValue(centeringFactor);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Centring factor: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }

        {
            final Label textValue = new Label(Float.toString(pathFactor), skin);
            slider = new Slider(0.f, 5f, 0.01f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    pathFactor = getValue();
                    return set;
                }
            };
            slider.setValue(centeringFactor);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Path factor: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }

        {
            final Label textValue = new Label(Float.toString(borderFactor), skin);
            slider = new Slider(0.f, 2, 0.01f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    borderFactor = getValue();
                    return set;
                }
            };
            slider.setValue(centeringFactor);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Border factor: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }

        verticalGroup.addActor(new FPSui(skin));
        verticalGroup.left();
        verticalGroup.columnLeft();
        root.setPosition(0, HEIGHT - verticalGroup.getHeight());
        root.left();
        root.columnLeft();
        ui.addActor(root);
    }


}
