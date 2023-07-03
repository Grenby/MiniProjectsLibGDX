package com.mygdx.projects.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSui;
import com.mygdx.projects.utils.MyInput;
import com.mygdx.projects.utils.b2dutils.B2DBodyBuilder;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class TestScreen3 implements Screen {

    static final int WIDTH = Gdx.graphics.getWidth();
    static final int HEIGHT = Gdx.graphics.getHeight();

    static final float PPM = 10;

    static final float cameraW = 10 * PPM;
    static final float cameraH = cameraW * HEIGHT / WIDTH;

    private final World world = new World(new Vector2(0, 0), true);
    private final Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();//new Box2DDebugRenderer(true,false,true,true,true,false);
    private final OrthographicCamera camera = new OrthographicCamera(cameraW, cameraH);
    boolean cameraUpdate = false;

    Vector3 p2 = new Vector3(0, 0, 0);

    Vector3 center = new Vector3(0, 0, 0);

    MyInput myInput = new MyInput();

    ShapeRenderer renderer = new ShapeRenderer();
    SpriteBatch batch = new SpriteBatch();
    Stage stage = new Stage();
    ShapeDrawer drawer = new ShapeDrawer(batch, Resource.getUISkin().getRegion("default-window"));
    Vector3 tmp = new Vector3();
    private boolean mouseDown = false;

    private void setVector(Ray ray, Vector3 v) {
        float t = -ray.origin.z / ray.direction.z;
        v.set(ray.direction).scl(t).add(ray.origin);
    }

    private void initInput() {
        myInput.addCallback(Input.Keys.ESCAPE, () -> {
            Gdx.app.exit();
        });
        myInput.addKeys(Input.Keys.W, Input.Keys.D, Input.Keys.S, Input.Keys.A);
        myInput.setTouchDown((screenX, screenY, pointer, button) -> {
            setVector(camera.getPickRay(screenX, screenY), p2);
            mouseDown = true;
        });
        myInput.setTouchUp((screenX, screenY, pointer, button) -> {
            mouseDown = false;
        });
        myInput.setTouchDragged((screenX, screenY, pointer) -> {
            Ray ray = camera.getPickRay(screenX, screenY);
            setVector(ray, p2);
        });
    }

    @Override
    public void show() {
        initInput();
        Gdx.input.setInputProcessor(myInput);

        B2DBodyBuilder.BodyDefParam bodyDefParam =
                new B2DBodyBuilder.BodyDefParam()
                        .staticBody()
                        .setPos(0, 0);
        B2DBodyBuilder.FixtureDefParam fixtureDefParam =
                new B2DBodyBuilder.FixtureDefParam()
                        .boxShape(1, 1, 0, 0);
        B2DBodyBuilder.instance.bodyDef(world, bodyDefParam).addFixture(fixtureDefParam).build();

        camera.far = 200;
        camera.position.set(-40, -40, 80);
        camera.up.set(0, 0, 1);
        //camera.up.set();
        camera.lookAt(0, 0, 0);
        camera.update();

        setVector(camera.getPickRay(WIDTH / 2f, HEIGHT / 2f), center);
        FPSui fps = new FPSui(Resource.getUISkin());
        fps.setMaxStep(20);
        stage.addActor(fps);

        //  renderer.setTransformMatrix(new Matrix4().rotateRad(Vector3.X, MathUtils.PI/2));
    }

    private void update(float delta) {
        stage.act(delta);

        setVector(camera.getPickRay(WIDTH / 2f, HEIGHT / 2f), center);
        if (!mouseDown) {
            if (!center.epsilonEquals(p2, 0.1f)) {
                tmp.set(p2).sub(center).nor().scl(delta * 10);
                camera.translate(tmp.x, tmp.y, 0);
                cameraUpdate = true;
            } else {
                p2.set(center);
            }
        } else {
            tmp.set(p2).sub(center).nor().scl(delta * 10);
            camera.translate(tmp);
            cameraUpdate = true;
            center.add(tmp);
            p2.add(tmp);
        }
//        ObjectSet<Integer> pressedKeys = myInput.getPressedKeys();
//        final float DELTA_ZOOM = 1.1f * delta;
//        final float DELTA_CAMERA = 1000f * delta;
//        final float minZoom = 0.1f;
//        final float maxZoom = 10;
//
//        for (int i : pressedKeys) {
//            switch (i) {
//                case Input.Keys.W:
//                    camera.position.add(0, DELTA_CAMERA, 0);
//                    cameraUpdate = true;
//                    break;
//                case Input.Keys.A:
//                    camera.position.add(-DELTA_CAMERA, 0, 0);
//                    cameraUpdate = true;
//                    break;
//                case Input.Keys.S:
//                    camera.position.add(0, -DELTA_CAMERA, 0);
//                    cameraUpdate = true;
//                    break;
//                case Input.Keys.D:
//                    camera.position.add(DELTA_CAMERA, 0, 0);
//                    cameraUpdate = true;
//                    break;
//                case Input.Keys.UP:
//                    if (camera.zoom < maxZoom)
//                        camera.zoom += DELTA_ZOOM;
//                    cameraUpdate = true;
//                    break;
//                case Input.Keys.DOWN:
//                    if (camera.zoom > minZoom)
//                        camera.zoom -= DELTA_ZOOM;
//                    cameraUpdate = true;
//                    break;
//                default:
//                    break;
//            }
//        }
        if (cameraUpdate) {
            camera.update();
            setVector(camera.getPickRay(WIDTH / 2f, HEIGHT / 2f), center);
            //cameraBuffer.put(camera.invProjectionView.getValues()).position(0);
        }
    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        debugRenderer.render(world, camera.combined);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GRAY);

        for (int i = -10; i <= 10; i++) {
            renderer.line(10 * i, -100, 0, 10 * i, 100, 0);
            renderer.line(-100, 10 * i, 0, 100, 10 * i, 0);
        }
        renderer.setColor(Color.RED);
        renderer.translate(center.x, center.y, center.z);
        renderer.line(-100, 0, 0, 100, 0, 0);
        renderer.setColor(Color.GREEN);
        renderer.line(0, -100, 0, 0, 100, 0);
        renderer.setColor(Color.BLUE);
        renderer.line(0, 0, -100, 0, 0, 100);
        renderer.translate(-center.x, -center.y, -center.z);
        renderer.setColor(Color.WHITE);
        renderer.line(center, p2);

        renderer.end();
//        batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//        {
//            float lineW = 0.1f;
//            drawer.setColor(Color.GRAY);
//            for (int i = -10; i <= 10; i++) {
//                drawer.line(10 * i, -100, 10 * i, 100,lineW);
//                drawer.line(-100, 10 * i, 100, 10 * i,lineW);
//            }
//            drawer.setColor(Color.RED);
//            drawer.line(-100 + center.x, center.y, 100+center.x, center.y,lineW);
//            drawer.setColor(Color.GREEN);
//            drawer.line(center.x, -100 + center.y,center.x, 100+center.y,lineW);
//            drawer.setColor(Color.BLUE);
//            //drawer.line(0, 0, -100, 0, 0, 100+center.y,lineW);
//            drawer.setColor(Color.WHITE);
//            drawer.line(center.x,center.y, p2.x,p2.y,lineW);
//
//        }
//        batch.end();

        stage.draw();

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
