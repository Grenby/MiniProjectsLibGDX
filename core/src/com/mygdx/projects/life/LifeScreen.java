package com.mygdx.projects.life;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSCounter;
import com.mygdx.projects.utils.MyInput;
import space.earlygrey.shapedrawer.ShapeDrawer;


public class LifeScreen implements Screen {

    private final MyInput input = new MyInput();

    private final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private ShapeDrawer drawer;
    private final ShapeRenderer renderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final int SIZE = 4;
    private int step = 0;

    private float scroll = 0;
    private final Field f = new Field(1000, 1000);
    private final Vector2 fieldPos = new Vector2(-f.width * SIZE / 2.f, -f.height * SIZE / 2.f);

    private final FPSCounter fpsCounter = new FPSCounter(10);
    //private final BitmapFont font = Resource.getFont();
    Vector3 center = new Vector3();
    Vector3 dim = new Vector3(SIZE, SIZE, SIZE);
    private boolean updateField = false;

    private void update(float delta) {

        fpsCounter.update(delta);

        final float DELTA_ZOOM = 1.1f * delta;
        final float DELTA_CAMERA = 1000f * delta;
        final float minZoom = 0.1f;
        final float maxZoom = 10;

        if (scroll != 0) {
            if (scroll > 0 && camera.zoom < maxZoom || scroll < 0 && camera.zoom > minZoom)
                camera.zoom += scroll * DELTA_ZOOM * 5;
            scroll = 0;
        }
        ObjectSet<Integer> keyPress = input.getPressedKeys();
        if (keyPress.contains(Input.Keys.DOWN) && camera.zoom < maxZoom) {
            camera.zoom += DELTA_ZOOM;
        }
        if (keyPress.contains(Input.Keys.UP) && camera.zoom > minZoom)
            camera.zoom -= DELTA_ZOOM;
        if (keyPress.contains(Input.Keys.W))
            camera.position.add(0, DELTA_CAMERA, 0);
        if (keyPress.contains(Input.Keys.D))
            camera.position.add(DELTA_CAMERA, 0, 0);
        if (keyPress.contains(Input.Keys.S))
            camera.position.add(0, -DELTA_CAMERA, 0);
        if (keyPress.contains(Input.Keys.A))
            camera.position.add(-DELTA_CAMERA, 0, 0);
        camera.update();

        if (updateField) {
            step++;
            if (step == 10) {
                step = 0;
                f.update();
            }
        }

    }

    void inputInit() {
        Gdx.input.setInputProcessor(input);
        input.setScrolled((amountX, amountY) -> scroll = amountY);
        input.addCallback(Input.Keys.F, () -> updateField = !updateField);
    }

    void initNewCursor() {
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.drawLine(8, 0, 8, 16);
        pixmap.drawLine(0, 8, 16, 8);
        Cursor cursor = Gdx.graphics.newCursor(pixmap, 8, 8);
        Gdx.graphics.setCursor(cursor);
    }

    @Override
    public void show() {
        inputInit();
        initNewCursor();

        drawer = new ShapeDrawer(batch, Resource.getUISkin().getRegion("default-window"));

        for (int i = 0; i < f.height * f.height / 10; i++) {
            int x = (int) (Math.random() * f.width);
            int y = (int) (Math.random() * f.height);
            f.set(x, y, 1);
        }
        System.out.println(camera.position.z);
        for (int i = 0; i < camera.frustum.planePoints.length; i++) {
            System.out.println(camera.frustum.planePoints[i]);
        }

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GRAY);
//        for (float i=0; i <= f.height;i++){
//            renderer.line(fieldPos.x,fieldPos.y + i*SIZE,fieldPos.x+SIZE*f.width,fieldPos.y+i*SIZE);
//        }
//        for (float i=0; i <= f.width;i++){
//            renderer.line(fieldPos.x + i*SIZE,fieldPos.y,fieldPos.y+i*SIZE,fieldPos.x+SIZE*f.height);
//        }
        renderer.line(fieldPos.x, fieldPos.y, fieldPos.x + SIZE * f.width, fieldPos.y);
        renderer.line(fieldPos.x, fieldPos.y + f.height * SIZE, fieldPos.x + SIZE * f.width, fieldPos.y + f.width * SIZE);

        renderer.line(fieldPos.x + SIZE * f.width, fieldPos.y, fieldPos.x + SIZE * f.width, fieldPos.y + f.height * SIZE);
        renderer.line(fieldPos.x, fieldPos.y, fieldPos.x, fieldPos.y + f.height * SIZE);


        renderer.end();
        //batch.setProjectionMatrix(camera.combined);
//        batch.begin();
//        drawer.line(0,0,100,100,0.1f);
//        batch.end();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.RED);
        for (int i = 0; i < f.width; i++) {
            for (int j = 0; j < f.height; j++) {
                if (f.field[i][j] == 1) {
                    float x = fieldPos.x + i * SIZE;
                    float y = fieldPos.y + j * SIZE;
                    center.set(x, y, -10);
                    if (camera.frustum.boundsInFrustum(center, dim)) {
                        renderer.rect(x, y, SIZE, SIZE);
                    }
                }
            }
        }
        renderer.end();


        batch.begin();
        //font.draw(batch,Float.toString(fpsCounter.getFps()),0,Gdx.graphics.getHeight());
        batch.end();

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
