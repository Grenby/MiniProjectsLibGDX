package com.mygdx.projects.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.perlinNoise.PerlinNoise;
import com.mygdx.projects.utils.MyInput;
import com.mygdx.projects.voxel.PerlinNoiseGenerator;
import com.mygdx.projects.voxel.VoxelWorld;

public class GridMode implements Screen {

    private final ShapeRenderer renderer = new ShapeRenderer();
    private final PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private FirstPersonCameraController controller;
    private float size = 24;
    boolean alongX = false;

    private HexagonGrid hexagonGrid;

    SpriteBatch spriteBatch;
    Environment lights;

    BitmapFont font;
    ModelBatch modelBatch;
    VoxelWorld voxelWorld;

    MyInput myInput = new MyInput();

    Vector3 tmp = new Vector3();
    Vector2 tmp2 = new Vector2();
    Vector2 tmp3 = new Vector2();
    Array<Integer> x = new Array<>();
    Array<Integer> y = new Array<>();

    final int maxX = 30;
    final int maxY = 30;

    GridPoint2 currentPoint = new GridPoint2(0, 0);
    int screenX = 0, screenY = 0;
    ObjectSet<GridPoint2> coloredPoints = new ObjectSet<>();

    static final int WIDTH = Gdx.graphics.getWidth();
    static final int HEIGHT = Gdx.graphics.getHeight();
    Bresenham2 bresenham2 = new Bresenham2();
    PerlinNoise noise = new PerlinNoise();
    Color color = new Color();
    private final float[][] map = new float[maxX][maxY];
    private final GridPoint2 gg = new GridPoint2();

    Color getColor(float c) {
        //c = c*c;
        color.r = c;
        color.g = c;
        color.b = c;
        color.a = 1;
        return color;
    }

    private void addNoise(int cellSize, int octaves) {
        for (int i = 0; i < maxX; i++) {
            for (int j = 0; j < maxY; j++) {
                float x = (float) i / cellSize;
                float y = (float) j / cellSize;
                float c = noise.getNoise(x, y, octaves, 0.5f);
                map[i][j] = c;
            }
        }
    }


    private boolean setVector(Ray ray, Vector3 v) {
        if (MathUtils.isEqual(ray.direction.y, 0, 0.1f)) return false;
        float t = -ray.origin.y / ray.direction.y;
        if (t < 0) return false;

        v.set(ray.direction).scl(t).add(ray.origin);
        return true;
    }

    private void initInput() {
        myInput.addCallback(Input.Keys.ESCAPE, () -> {
            Gdx.app.exit();
        });
        //myInput.addKeys(Input.Keys.W, Input.Keys.D, Input.Keys.S, Input.Keys.A);
        myInput.setMouseMoved((screenX, screenY) -> {
            this.screenX = screenX;
            this.screenY = screenY;
            if (!setVector(camera.getPickRay(screenX, screenY), tmp)) {
                currentPoint.set(0, 0);
                return;
            }
            hexagonGrid.getHexagon(tmp.z, tmp.x, currentPoint);
        });
        myInput.setTouchDown((screenX, screenY, pointer, button) -> {
            if (!setVector(camera.getPickRay(screenX, screenY), tmp)) {
                return;
            }
            GridPoint2 point2 = new GridPoint2();
            hexagonGrid.getHexagon(tmp.z, tmp.x, point2);
            System.out.println(point2);
            coloredPoints.add(point2);

        });

    }

    private void createHexagon(int x0, int z0) {
        int s = (int) (size + size / 2);
        for (int i = 0; i < s; i++) {
            for (int j = 0; j < (int) size; j++) {
                voxelWorld.set(x0 + i, 0, z0 + j, (byte) 1);
            }
        }

        int rightX = (int) (x0 + size + size * Math.cos(MathUtils.PI / 3));
        int rightZ = (int) (z0 + size);
//

    }

    private void addLine(int center, int x0, int y0, int x1, int y1, int h) {
        for (GridPoint2 g : bresenham2.line(x0, y0, x1, y1)) {
            int w = 2 * (g.x - center) + 1;
            voxelWorld.setCube(g.x - w, 0, g.y, w + 1, h, 1, (byte) 1);
        }
    }

    private void addHex(int x0, int y0, int h) {
        hexagonGrid.getCenter(x0, y0, tmp2);
        System.out.println(hexagonGrid.getX());
        System.out.println(hexagonGrid.getY());
        System.out.println(tmp2);
        tmp2.add(hexagonGrid.getOX()[4], hexagonGrid.getOY()[4]);

        float s = size + size / 2;
        voxelWorld.setCube(tmp2.y, 0, tmp2.x, s, h, size, (byte) 1);
//        for (float i=0;i<s;i++){
//            for (float j=0;j<size;j++) {
//                voxelWorld.set(tmp2.y + i, 0, tmp2.x + j, (byte) 1);
//            }
//        }

        tmp2.sub(hexagonGrid.getOX()[4], hexagonGrid.getOY()[4]);
        int center = (int) (tmp2.y + 0.5f);
        tmp2.add(hexagonGrid.getOX()[0], hexagonGrid.getOY()[0]);
        int rightX = (int) tmp2.y;
        int rightZ = (int) (tmp2.x) - 1;
        tmp2.sub(hexagonGrid.getOX()[0], hexagonGrid.getOY()[0]).add(hexagonGrid.getOX()[1], hexagonGrid.getOY()[1]);
        int x = (int) tmp2.y - 1;
        int z = (int) tmp2.x;
        addLine(center, x, z, rightX, rightZ, h);
        //addLine(x,(int)(z + size+size/2),rightX,rightZ);
        tmp2.sub(hexagonGrid.getOX()[1], hexagonGrid.getOY()[1]).add(hexagonGrid.getOX()[2], hexagonGrid.getOY()[2]);
        x = (int) tmp2.y - 1;
        z = (int) tmp2.x - 1;
        addLine(center, x, z, rightX, (int) (rightZ - 2 * size + 1), h);


    }

    void drawRing(int x0, int y0, int r) {
        if (!alongX)
            x0--;
        x0 += r;
        for (int i = 0; i < r; i++) {
            coloredPoints.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                x0--;
                y0++;
            }
        }
        x0--;
        r--;
        for (int i = 0; i < r; i++) {
            coloredPoints.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                x0--;
            }
        }
        y0--;
        for (int i = 0; i < r; i++) {
            coloredPoints.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                y0--;
            }
        }
        y0--;
        x0++;
        for (int i = 0; i < r; i++) {
            coloredPoints.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                y0--;
                x0++;
            }
        }
        x0++;
        for (int i = 0; i < r; i++) {
            coloredPoints.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                x0++;
            }
        }
        y0++;
        for (int i = 0; i < r; i++) {
            coloredPoints.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                y0++;
            }
        }
    }

    void drawHexagon(int x0, int y0, int r) {
        coloredPoints.add(new GridPoint2(x0, y0));
        for (int i = 1; i <= r; i++) {
            drawRing(x0, y0, i);
        }
    }

    void createGrid() {
//        hexagonGrid = new HexagonGrid();
//        float dx = size;
//        //dx/=2;
//        Vector2 gX = new Vector2(size/2,(size + size/2)/2);
//        Vector2 gY = new Vector2(0,size + size/2);
//
//        hexagonGrid.setOX(new float[]{dx,gX.x,-gX.x,-dx,-gX.x,gX.x,dx});
//        hexagonGrid.setOY(new float[]{0,gX.y,gX.y,0,-gX.y,-gX.y,0});
//
//        gX.add(dx,0);
//
//        hexagonGrid.set(gX,gY,new Vector2(gX).add(gY));
    }

    @Override
    public void show() {
        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.Specular, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(new DirectionalLight().set(1, 1, 1, 1, -1, 1));
        camera.near = 0.5f;
        camera.far = 500;
        DefaultShader.defaultCullFace = GL20.GL_FRONT;

        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        modelBatch = new ModelBatch();
        Texture texture = new Texture(Gdx.files.internal("tiles.png"));
        TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);
        MathUtils.random.setSeed(0);


        addNoise(100, 1);
        drawHexagon(0, 0, 5);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        controller = new FirstPersonCameraController(camera) {
            @Override
            public void update(float deltaTime) {
                super.update(deltaTime);
                if (!setVector(camera.getPickRay(screenX, screenY), tmp)) {
                    currentPoint.set(0, 0);
                    return;
                }
                hexagonGrid.getHexagon(tmp.z, tmp.x, currentPoint);
            }
        };
        initInput();
        Gdx.input.setInputProcessor(new InputMultiplexer(myInput, controller));
        renderer.getTransformMatrix()
                .rotateRad(1, 0, 0, -MathUtils.PI / 2f)
                .rotateRad(0, 0, 1, -MathUtils.PI / 2f);
        camera.position.set(20, 5, 30);
        camera.lookAt(22, 5, 28);
        camera.update();

        voxelWorld = new VoxelWorld(tiles[0], 50, 1, 50, camera.position, camera.frustum);
        byte[] h = PerlinNoiseGenerator.generateHeightMap(maxX, maxY, 1, 10, 1);
        controller.setVelocity(100);
        createGrid();
        for (int i = 1; i < maxX; i++) {
            for (int j = 1; j < maxY; j++) {
                addHex(i, j, h[i * maxX + j]);
            }
        }
    }

    GridPoint2 pp = new GridPoint2();

    void drawHex(int x, int y) {
        float h = 10;
        voxelWorld.getHighest(x, y);
        final float[] oX = hexagonGrid.getOX();
        final float[] oY = hexagonGrid.getOY();
        //   hexagonGrid.getTranslate(tmp2).mulAdd(hexagonGrid.getX(),x).mulAdd(hexagonGrid.getY(),y);
        for (int j = 0; j < 6; j++) {
            renderer.line(tmp2.x + oX[j], tmp2.y + oY[j], 0, tmp2.x + oX[j + 1], tmp2.y + oY[j + 1], 0);
            renderer.line(tmp2.x + oX[j], tmp2.y + oY[j], h, tmp2.x + oX[j + 1], tmp2.y + oY[j + 1], h);
            renderer.line(tmp2.x + oX[j], tmp2.y + oY[j], 0, tmp2.x + oX[j], tmp2.y + oY[j], h);
        }
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        ScreenUtils.clear(0.4f, 0.4f, 0.4f, 1f, true);
        renderer.setProjectionMatrix(camera.combined);
        final float[] oX = hexagonGrid.getOX();
        final float[] oY = hexagonGrid.getOY();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            //hexagonGrid.getTranslate(tmp2);
            renderer.setColor(Color.BLUE);
            renderer.line(tmp2.x, tmp2.y, 0, 100, 0, 0);

            renderer.setColor(Color.RED);
            renderer.line(tmp2.x, tmp2.y, 0, 0, 100, 0);

            renderer.setColor(Color.GREEN);
            renderer.line(tmp2.x, tmp2.y, 0, 0, 0, 100);

//		    renderer.setColor(Color.WHITE);
//            for (int i=0;i<6;i++){
//                renderer.line(oX[i],oY[i],oX[i+1],oY[i+1]);
//            }
//            renderer.line(Vector2.Zero,hexagonGrid.getX());
//            renderer.line(Vector2.Zero,hexagonGrid.getY());
//
//            for (int i=-10;i<10;i++){
//                for(int j=-10;j<10;j++){
//                    tmp2.set(hexagonGrid.getX()).scl(i).mulAdd(hexagonGrid.getY(), j);
//                    for (int k = 0; k < 6; k++) {
//                        renderer.line(tmp2.x + oX[k], tmp2.y + oY[k], tmp2.x + oX[k + 1], tmp2.y + oY[k + 1]);
//                    }
//                }
//            }

        }
        renderer.end();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        {
            renderer.setColor(Color.MAROON);
//            for(int i=0;i<x.size;i++) {
//                tmp2.set(hX).scl(x.get(i)).mulAdd(hY, y.get(i));
//                for (int k = 0; k < 6; k++) {
//                    renderer.triangle(tmp2.x,tmp2.y,tmp2.x + oX[k], tmp2.y + oY[k], tmp2.x + oX[k + 1], tmp2.y + oY[k + 1]);
//                }
//            }
//            for (final GridPoint2 point:point2s
//                 ) {
//                tmp2.set(hX).scl(point.x).mulAdd(hY, point.y);
//                for (int k = 0; k < 6; k++) {
//                    renderer.triangle(tmp2.x,tmp2.y,tmp2.x + oX[k], tmp2.y + oY[k], tmp2.x + oX[k + 1], tmp2.y + oY[k + 1]);
//                }
//
//            }
//            for (int i=-maxX+1;i<maxX;i++){
//                for (int j=-maxY+1;j<maxY;j++){
//                    renderer.setColor(getColor(map[Math.abs(i)][Math.abs(j)]));
//                    tmp2.set(hX).scl(i).mulAdd(hY,j);
//                for (int k = 0; k < 6; k++) {
//                    renderer.triangle(tmp2.x,tmp2.y,tmp2.x + oX[k], tmp2.y + oY[k], tmp2.x + oX[k + 1], tmp2.y + oY[k + 1]);
//                }
//                }
//            }
        }
        renderer.end();


        modelBatch.begin(camera);
        modelBatch.render(voxelWorld, lights);
        modelBatch.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
//           ) hexagonGrid.getHexagon(point3.)
            tmp2.set(hexagonGrid.getX()).scl(currentPoint.x).mulAdd(hexagonGrid.getY(), currentPoint.y);
            //hexagonGrid.getTranslate(tmp3);
            tmp2.add(tmp3);
            renderer.setColor(Color.GOLD);
            float h = voxelWorld.getHighest(tmp2.y, tmp2.x);
            for (int j = 0; j < 6; j++) {
                renderer.line(tmp2.x + oX[j], tmp2.y + oY[j], h, tmp2.x + oX[j + 1], tmp2.y + oY[j + 1], h);
            }
//            for (GridPoint2 g:coloredPoints
//                 ) {
//                drawHex(g.x,g.y);
//            }
        }
        renderer.end();

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
