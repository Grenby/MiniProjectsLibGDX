package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class PerlinMode implements Screen {

    static final float CELL_SIZE = 1f;
    static final int WIDTH = 1024;
    static final int HEIGHT = 1024;

    private final ShapeRenderer renderer = new ShapeRenderer();

    private final Pixmap pixelMap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    private final float[][] map = new float[WIDTH][HEIGHT];
    Texture t;
    SpriteBatch batch = new SpriteBatch();

    PerlinNoise noise = new PerlinNoise();
    Environment lights;


    ModelBatch modelBatch = new ModelBatch();
    PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    FirstPersonCameraController controller = new FirstPersonCameraController(camera) {
    };

    World world;

    Color color = new Color();

    Color getColor(float c) {
        //c = c*c;
        color.r = c;
        color.g = c;
        color.b = c;
        color.a = 1;
        return color;
    }


    @Override
    public void show() {
        camera.far = 1000;
        Gdx.input.setInputProcessor(controller);
        controller.setVelocity(100);
        addNoise(100, 4);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                float c = map[i][j];
                pixelMap.setColor(getColor(c));
                pixelMap.drawPixel(i, j);
            }
        }

        lights = new Environment();
        lights.set(
                new ColorAttribute(ColorAttribute.Specular, 1.0f, 1.0f, 1.0f, 1.f),
                new ColorAttribute(ColorAttribute.Diffuse, 0.5f, 0.5f, 0.5f, 1.f),
                new ColorAttribute(ColorAttribute.Ambient, 0.2f, 0.2f, 0.2f, 1.f)
        );

        lights.add(new DirectionalLight().set(1, 1, 1, 1, 1, -1));

        t = new Texture(pixelMap);
        pixelMap.dispose();
        System.out.println("adding noise is done");


        camera.up.set(0, 0, 1);
        camera.direction.set(1, 0, 0).nor();
        camera.position.set(0, 0, 10);
        camera.update();

        world = new World(map);

    }

    float qunticCurve(float t) {
        return t;
        //return t * t * t * (t * (t * 6 - 15) + 10);
    }


    private void addNoise(float cellSize, int octaves) {
        // TestNoise noise = new TestNoise();
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                float x = (float) i / cellSize;
                float y = (float) j / cellSize;
                float c = noise.getNoise(x, y, octaves, 0.7f);
                c = qunticCurve(qunticCurve(qunticCurve(c)));
//                map[i][j]/=2;
//                float old = map[i][j];
//                c = old==0? c : (c+old);
                map[i][j] = c;
            }
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();
        controller.update(delta);

        Gdx.gl.glClearColor(0f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling?GL20.GL_COVERAGE_BUFFER_BIT_NV:0));

        modelBatch.begin(camera);
        for (int i = 0; i < 1; i++) {
            world.tr.rotateRad(0, 0, 1, i * MathUtils.PI / 2);
            modelBatch.render(world, lights);
        }
        modelBatch.end();
        batch.setProjectionMatrix(camera.combined);

        renderer.setProjectionMatrix(camera.combined);
//        renderer.begin(ShapeRenderer.ShapeType.Line);
//
//        for (int i=0;i<WIDTH;i++){
//            for (int j=0;j<HEIGHT;j++){
//                float x = i * CELL_SIZE;
//                float y = j * CELL_SIZE;
//                float z = (map[i][j]-0.5f) * 100;
//                if (i>0 && i<WIDTH-1 && j>0 && j<HEIGHT-1) {
//                    float z1 = getR(map[i][j],map[i-1][j],map[i][j-1],map[i-1][j-1]) *100;
//                    float z2 = getR(map[i][j],map[i+1][j],map[i][j-1],map[i+1][j-1]) *100;
//                    float z3 = getR(map[i][j],map[i+1][j],map[i][j+1],map[i+1][j+1]) *100;
//                    float z4 = getR(map[i][j],map[i-1][j],map[i][j+1],map[i-1][j+1]) *100;
//
//                    renderer.line(x, y, z1, x + CELL_SIZE, y, z2);
//                    renderer.line(x, y, z1, x, y + CELL_SIZE, z4);
//                    renderer.line(x + CELL_SIZE, y + CELL_SIZE, z3, x + CELL_SIZE, y, z2);
//                    renderer.line(x + CELL_SIZE, y + CELL_SIZE, z3, x, y + CELL_SIZE, z4);
//                }
////                renderer.box(x,y,0,x+CELL_SIZE,y+CELL_SIZE,z);
//
//            }
//        }
//
//        renderer.end();

    }

    float getR(float... x) {
        float res = 0;
        for (float f : x) {
            res += f;
        }
        return res / x.length;
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
        t.dispose();

    }

}
