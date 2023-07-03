package com.mygdx.projects.microProjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.mygdx.projects.perlinNoise.noise.PerlinNoiseSquare;
import com.mygdx.projects.utils.MyInput;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class SquaresScreen extends InputAdapter implements Screen {


    static final float WW = 100;
    static final float HH = WW * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

    private static final OrthographicCamera camera = new OrthographicCamera(WW, HH);
    private static final MyInput myInput = new MyInput();

    final PerlinNoiseSquare noise = new PerlinNoiseSquare();
    PerlinNoiseSquare up;
    final ShapeRenderer renderer = new ShapeRenderer();

    final Vector2 tmp = new Vector2();

    final float maxZoom = 12;
    final float minZoom = 0.01f;

    float cellSize = 5f;
    int cellW = 100;
    int cellH = 100;
    int[][] nodesVal = new int[cellW][cellH];

    private final Pixmap pixelMap = new Pixmap((int) (cellW), (int) (cellH), Pixmap.Format.RGBA8888);
    Texture t1, t2;
    SpriteBatch batch = new SpriteBatch();
    ShapeDrawer drawer;

    float zoom = 0;
    RandomXS128 randomXS128 = new RandomXS128();

    Color color = new Color();

    Color getColor(float c) {
        //c = c*c;
        color.r = c;
        color.g = c;
        color.b = c;
        color.a = 1;
        return color;
    }

    void initInput() {
        Gdx.input.setInputProcessor(myInput);
        myInput.addCallback(Input.Keys.ESCAPE, () -> Gdx.app.exit());
        myInput.addKeys(Input.Keys.W);
        myInput.addKeys(Input.Keys.S);
        myInput.addKeys(Input.Keys.D);
        myInput.addKeys(Input.Keys.A);
        myInput.addKeys(Input.Keys.UP);
        myInput.addKeys(Input.Keys.DOWN);
    }

    void updateInput(float delta) {
        tmp.set(0, 0);
        float s = 2;
        ObjectSet<Integer> pressed = myInput.getPressedKeys();
        float zz = zoom;
        if (pressed.contains(Input.Keys.W))
            tmp.y += s;
        if (pressed.contains(Input.Keys.D))
            tmp.x += s;
        if (pressed.contains(Input.Keys.S))
            tmp.y -= s;
        if (pressed.contains(Input.Keys.A))
            tmp.x -= s;
        if (pressed.contains(Input.Keys.UP))
            zoom -= .01f;
        if (pressed.contains(Input.Keys.DOWN))
            zoom += .01f;
        boolean update = false;
        if (!tmp.isZero()) {
            tmp.nor().scl(delta * 50);
            camera.position.add(tmp.x, tmp.y, 0);
            update = true;
        }
        if (zoom != zz) {
            camera.zoom = getZoom(zoom);
            update = true;
        }
        if (update)
            camera.update();
    }

    float getZoom(float zoom) {
        return (float) (Math.atan(zoom) * 2 / Math.PI + 1) / 2f * (maxZoom - minZoom) + minZoom;
    }

    float getInvZoom(float zoom) {
        zoom -= minZoom;
        zoom /= (maxZoom - minZoom);
        zoom *= 2;
        zoom -= 1;
        zoom = (float) Math.tan(zoom / 2 * Math.PI);
        return zoom;
    }


    float f(float x, float y) {
        //color.set(pixelMap.getPixel((int)x,(int)y));
        return nodesVal[(int) x][(int) y];
    }

    float qunticCurve(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    Texture getNoise(PerlinNoiseSquare noise) {
        Pixmap pixelMap = new Pixmap(cellW, cellH, Pixmap.Format.RGB888);
        for (int i = 0; i < cellW; i++) {
            for (int j = 0; j < cellH; j++) {
                float x = i / cellSize;
                float y = j / cellSize;
                float c = noise.getNoise(x, y, 8);
                c = qunticCurve(c);
                pixelMap.setColor(getColor(c));
                pixelMap.drawPixel(i, j);
                nodesVal[i][cellH - j - 1] = Math.round(1 - c);
            }
        }
        Texture t = new Texture(pixelMap);
        pixelMap.dispose();
        return t;
    }

    @Override
    public void show() {
        initInput();
        camera.position.set(0, 0, 0);
        zoom = getInvZoom(camera.zoom);
        camera.update();

        {
            Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGB888);
            pixmap.setColor(Color.GOLD);
            pixmap.fill();
            drawer = new ShapeDrawer(new SpriteBatch(), new TextureRegion(new Texture(pixmap)));
            pixmap.dispose();
        }

        up = new PerlinNoiseSquare.Builder().addDown(noise).create();

        t1 = getNoise(noise);
        t2 = getNoise(up);

        System.out.println("adding noise is done");
    }

    @Override
    public void render(float delta) {
        updateInput(delta);
        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        drawer.getBatch().setProjectionMatrix(camera.combined);

        batch.begin();
        {
            batch.draw(t1, 0, 0, cellW * cellSize, cellH * cellSize);
            batch.draw(t2, 0, cellH * cellSize, cellW * cellSize, cellH * cellSize);
        }
        batch.end();
//
//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        {
//            float d = cellSize/2f;
//            float r = cellSize/10f;
//            rectangle.setSize(cellSize, cellSize);
//            for (int i = 0; i < cellW; i++) {
//                for (int j = 0; j < cellH; j++) {
//                    float xx = i * cellSize + d;
//                    float yy = j * cellSize + d;
//                    //rectangle.setCenter(i);
//                    if (camera.frustum.sphereInFrustum(xx, yy, 0, r)) {
//                        if (nodesVal[i][j] >= 1)
//                            renderer.setColor(Color.BLACK);
//                        else
//                            renderer.setColor(Color.WHITE);
//                        renderer.circle(xx, yy, r);
//                    }
//                }
//            }
//        }renderer.end();

        drawer.setColor(Color.GREEN);
        drawer.getBatch().begin();
        {
            float r = cellSize / 2f;
            for (int i = 0; i < cellW - 1; i++) {
                for (int j = 0; j < cellH - 1; j++) {
                    float xx = i * cellSize + r;
                    float yy = j * cellSize + r;
                    float x0y0 = f(i, j);
                    float x0y1 = f(i, j + 1);
                    float x1y0 = f(i + 1, j);
                    float x1y1 = f(i + 1, j + 1);

                    int c = (x0y0 > 0 ? 1 : 0) +
                            (x0y1 > 0 ? 8 : 0) +
                            (x1y1 > 0 ? 4 : 0) +
                            (x1y0 > 0 ? 2 : 0);

                    switch (c) {
                        case 0:
                        case 15:
                            break;
                        case 1:
                        case 14:
                            drawer.line(xx + r, yy, xx, yy + r);
                            break;
                        case 2:
                        case 13:
                            drawer.line(xx + r, yy, xx + 2 * r, yy + r);
                            break;
                        case 3:
                        case 12:
                            drawer.line(xx, yy + r, xx + 2 * r, yy + r);
                            break;
                        case 4:
                        case 11:
                            drawer.line(xx + r, yy + 2 * r, xx + 2 * r, yy + r);
                            break;
                        case 5:
                            drawer.line(xx, yy + r, xx + r, yy + 2 * r);
                            drawer.line(xx + r, yy, xx + 2 * r, yy + r);
                            break;
                        case 6:
                        case 9:
                            drawer.line(xx + r, yy, xx + r, yy + 2 * r);
                            break;
                        case 7:
                        case 8:
                            drawer.line(xx, yy + r, xx + r, yy + 2 * r);
                            break;
                        case 10:
                            drawer.line(xx + r, yy, xx, yy + r);
                            drawer.line(xx + r, yy + 2 * r, xx + 2 * r, yy + r);
                            break;
                    }
                }
            }
        }
        drawer.getBatch().end();
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
