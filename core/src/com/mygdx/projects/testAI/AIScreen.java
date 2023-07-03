package com.mygdx.projects.testAI;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.Resource;
import com.mygdx.projects.testAI.utils.PotentialField;
import com.mygdx.projects.utils.MyInput;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.function.Function;

public class AIScreen implements Screen {


    static final int WIDTH = Gdx.graphics.getWidth();
    static final int HEIGHT = Gdx.graphics.getHeight();

    final String TAG = AIScreen.class.getSimpleName();

    int gridW = 100;
    int gridH = 100;
    float size = 1f;

    MyWorld myWorld;

    ShapeDrawer drawer;
    public ShapeRenderer renderer = new ShapeRenderer();

    private final PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private FirstPersonCameraController controller;

    Stage uiStage = new Stage();


    boolean updateGrid = false;
    boolean renderGrid = false;
    boolean renderField = true;

    PotentialField field = new PotentialField();
    Color gridColor = new Color();

    void initUI() {
        Skin skin = Resource.getUISkin();
        HorizontalGroup root = new HorizontalGroup();

        {
            TextButton renderFieldButton = new TextButton("Render Filed", skin, "toggle");
            renderFieldButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    renderField = !renderField;
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            renderFieldButton.setChecked(renderField);
            root.addActor(renderFieldButton);
        }

        {
            TextButton renderGridButton = new TextButton("Render Grid", skin, "toggle");
            renderGridButton.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    renderGrid = !renderGrid;
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            renderGridButton.setChecked(renderGrid);
            root.addActor(renderGridButton);
        }

        float h = root.getChild(0).getHeight();
        //System.out.println(h);
        root.setPosition(0, h / 2);
        uiStage.addActor(root);

    }

    void initInput() {
        MyInput input = new MyInput();

        input.addCallback(Input.Keys.ESCAPE, () -> Gdx.app.exit());

        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(100);

        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, input, controller));
    }

    void setTransformMatrix(Matrix4 matrix) {
        matrix.rotateRad(1, 0, 0, -MathUtils.PI / 2f)
                .rotateRad(0, 0, 1, -MathUtils.PI / 2f);
    }

    void initField() {
        field.addFunc(new Function<Vector2, Float>() {
            final Vector2 center = new Vector2(gridW, gridH).scl(size).scl(0.5f);

            @Override
            public Float apply(Vector2 vector2) {
                return 1000 / (1 + center.dst(vector2));
            }
        });

        field.addFunc(new Function<Vector2, Float>() {
            final Vector2 center = new Vector2(gridW / 2f, gridH).scl(size).scl(0.5f);

            @Override
            public Float apply(Vector2 vector2) {
                return -10000 / (1 + center.dst(vector2));
            }
        });
    }

    void initUnits() {

    }

    void initCamera() {
        camera.near = 0.1f;
        camera.far = 5000;
        /*
        camera x = world y
        camera y = world z
        camera z = world x

         */

        camera.up.set(0, 0, 1);
        camera.direction.set(1, 0, 0).nor();
        camera.position.set(0, 0, 10);
        camera.update();

        camera.update();
    }

    @Override
    public void show() {

        Holder.field = field;

        drawer = new ShapeDrawer(new SpriteBatch(), Resource.getUISkin().getRegion("default-window"));
        myWorld = new MyWorld();

        gridW = (int) (myWorld.dim.x / size);
        gridH = (int) (myWorld.dim.y / size);

        initUI();
        initInput();
        initField();
        initUnits();

//        setTransformMatrix(drawer.getBatch().getTransformMatrix());
//        setTransformMatrix(renderer.getTransformMatrix());

        initCamera();
    }

    Color color = new Color();

    Color getColor(float h) {
        float max = 1000;
        float min = -1000;
        float t = h / (max - min);
        if (t > 1) t = 1;
        if (t < -1) t = -1;
        t = (t + 1) / 2;
        color.set(Color.BLUE).lerp(Color.RED, t);
        return color;
    }

    void renderEnv() {

        drawer.getBatch().begin();
        if (renderField) {
            for (int i = 0; i < gridW; i++) {
                for (int j = 0; j < gridH; j++) {
                    drawer.setColor(getColor(field.getVal(i * size + size / 2f, j * size + size / 2f)));
                    drawer.filledRectangle(i * size, j * size, size, size);
                }
            }
        } else {
            drawer.setColor(Color.WHITE);
            drawer.filledRectangle(0, 0, size * gridW, size * gridH);
        }

        drawer.getBatch().end();

        if (renderGrid) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            float w = size * gridW;
            float h = size * gridH;
            renderer.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= gridW; i++) {
                renderer.line(i * size, 0, i * size, h);
            }
            for (int j = 0; j <= gridH; j++) {
                renderer.line(0, j * size, w, j * size);
            }
            renderer.end();
        }


    }

    void renderUnits(float delta) {
        myWorld.update(delta);
        myWorld.render(drawer);
    }

    void renderAxis() {
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLUE);
        renderer.line(0, 0, 0, 100, 0, 0);

        renderer.setColor(Color.RED);
        renderer.line(0, 0, 0, 0, 100, 0);

        renderer.setColor(Color.GREEN);
        renderer.line(0, 0, 0, 0, 0, 100);

        renderer.end();
    }

    @Override
    public void render(float delta) {
        controller.update(delta);

        drawer.getBatch().setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(camera.combined);

        ScreenUtils.clear(0, 0, 0, 1);

        renderEnv();
        renderUnits(delta);
        renderAxis();
        uiStage.act(delta);
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
