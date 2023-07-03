package com.mygdx.projects.mazeGen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.projects.MyScreen;
import com.mygdx.projects.utils.garphs.GraphImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MazeMode extends MyScreen {

    private final Set<Integer> keysForUpdate = new HashSet<>(Arrays.asList(
            Input.Keys.W, Input.Keys.D, Input.Keys.S, Input.Keys.A, Input.Keys.UP, Input.Keys.DOWN
    ));

    private final Set<Integer> keyPress = new HashSet<>();
    private float scroll = 0;

    Vector2 firstNode;
    GraphImpl<Vector2> graph;
    Graph<Vector2> maze;

    int mazeWidth = 30;
    int mazeHeight = 30;
    float cellSize = 40;

    Vector2[][] nodes = new Vector2[mazeWidth][mazeHeight];
    float[] oX;
    float[] oY;
    ShapeRenderer renderer = new ShapeRenderer();
    OrthographicCamera camera = new OrthographicCamera(WIDTH, HEIGHT);
    ObjectMap<Vector2, Vector2> randomVectors = new ObjectMap<>(mazeWidth * mazeHeight);

    Texture t;
    SpriteBatch batch = new SpriteBatch();
    Color color = new Color();

    Color getColor(float c) {
        color.r = c;
        color.g = c;
        color.b = c;
        color.a = 1;
        return color;
    }


    public MazeMode() {
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keysForUpdate.contains(keycode)) {
            keyPress.add(keycode);
            return true;
        }
        switch (keycode) {
            case Input.Keys.ESCAPE:
                dispose();
                Gdx.app.exit();
                break;
            case Input.Keys.F:
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keysForUpdate.contains(keycode)) {
            keyPress.remove(keycode);
            return true;
        }
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        scroll = amountY;
        return false;
    }


    void update(float delta) {

        final float DELTA_ZOOM = 1.1f * delta;
        final float DELTA_CAMERA = 1000f * delta;
        final float minZoom = 0.1f;
        final float maxZoom = 10;
        boolean cameraUpdate = false;
        if (scroll != 0) {
            camera.zoom += scroll * DELTA_ZOOM;
            scroll = 0;
            cameraUpdate = true;
        }
        if (keyPress.contains(Input.Keys.DOWN) && camera.zoom < maxZoom) {
            camera.zoom += DELTA_ZOOM;
            cameraUpdate = true;
        }
        if (keyPress.contains(Input.Keys.UP) && camera.zoom > minZoom) {
            camera.zoom -= DELTA_ZOOM;
            cameraUpdate = true;
        }
        if (keyPress.contains(Input.Keys.W)) {
            camera.position.add(0, DELTA_CAMERA, 0);
            cameraUpdate = true;
        }
        if (keyPress.contains(Input.Keys.D)) {
            camera.position.add(DELTA_CAMERA, 0, 0);
            cameraUpdate = true;
        }
        if (keyPress.contains(Input.Keys.S)) {
            camera.position.add(0, -DELTA_CAMERA, 0);
            cameraUpdate = true;
        }
        if (keyPress.contains(Input.Keys.A)) {
            camera.position.add(-DELTA_CAMERA, 0, 0);
            cameraUpdate = true;
        }
        if (cameraUpdate) {
            camera.update();
            //        cameraBuffer.put(camera.invProjectionView.getValues()).position(0);
        }
    }

    private void init6() {
        float v = (float) (cellSize * Math.sqrt(3));


        Vector2 smooth1 = new Vector2(MathUtils.cos(MathUtils.PI / 3), MathUtils.sin(MathUtils.PI / 3));
        smooth1.scl(cellSize);

        oX = new float[]{cellSize, smooth1.x, -smooth1.x, -cellSize, -smooth1.x, smooth1.x, cellSize};
        oY = new float[]{0, smooth1.y, smooth1.y, 0, -smooth1.y, -smooth1.y, 0};

        smooth1.set(MathUtils.cos(MathUtils.PI / 6), -MathUtils.sin(MathUtils.PI / 6));
        smooth1.scl(v);

        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                if (x == 0 & y == 0) {
                    nodes[x][y] = new Vector2(0, 0);
                } else {
                    if (x % 2 == 0)
                        nodes[x][y] = new Vector2(nodes[0][0]).add(3 * cellSize * x / 2, v * y);
                    else
                        nodes[x][y] = new Vector2(nodes[0][0]).add(3 * cellSize * (x - 1) / 2, v * y).add(smooth1);
                }
            }
        }

        firstNode = nodes[0][0];
        Array<Connection<Vector2>> connections = new Array<>(6);

        for (int y = 0; y < mazeHeight; y++) {
            for (int x = 0; x < mazeWidth; x++) {
                connections.clear();
                Vector2 pos = nodes[x][y];
                if (y == 0) {
                    if (x == 0) {
                        connections.add(new DefaultConnection<>(pos, nodes[1][0]));
                        connections.add(new DefaultConnection<>(pos, nodes[1][1]));
                        connections.add(new DefaultConnection<>(pos, nodes[0][1]));
                    } else if (x == mazeWidth - 1) {
                        connections.add(new DefaultConnection<>(pos, nodes[x - 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x][y + 1]));
                        if (x % 2 == 0)
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y + 1]));
                    } else {
                        connections.add(new DefaultConnection<>(pos, nodes[x - 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x + 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x][y + 1]));
                        if (x % 2 == 0) {
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y + 1]));
                            connections.add(new DefaultConnection<>(pos, nodes[x + 1][y + 1]));
                        }
                    }
                } else if (y == mazeHeight - 1) {
                    if (x == 0) {
                        connections.add(new DefaultConnection<>(pos, nodes[x][y - 1]));
                        connections.add(new DefaultConnection<>(pos, nodes[x + 1][y]));
                    } else if (x == mazeWidth - 1) {
                        connections.add(new DefaultConnection<>(pos, nodes[x][y - 1]));
                        connections.add(new DefaultConnection<>(pos, nodes[x - 1][y]));
                        if (x % 2 == 1)
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y - 1]));
                    } else {
                        connections.add(new DefaultConnection<>(pos, nodes[x + 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x - 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x][y - 1]));
                        if (x % 2 == 1) {
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y - 1]));
                            connections.add(new DefaultConnection<>(pos, nodes[x + 1][y - 1]));
                        }
                    }
                } else {
                    connections.add(new DefaultConnection<>(pos, nodes[x][y - 1]));
                    connections.add(new DefaultConnection<>(pos, nodes[x][y + 1]));
                    if (x == 0) {
                        connections.add(new DefaultConnection<>(pos, nodes[x + 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x + 1][y + 1]));
                    } else if (x == mazeWidth - 1) {
                        connections.add(new DefaultConnection<>(pos, nodes[x - 1][y]));
                        if (x % 2 == 1)
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y - 1]));
                        else
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y + 1]));
                    } else {
                        connections.add(new DefaultConnection<>(pos, nodes[x - 1][y]));
                        connections.add(new DefaultConnection<>(pos, nodes[x + 1][y]));
                        if (x % 2 == 1) {
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y - 1]));
                            connections.add(new DefaultConnection<>(pos, nodes[x + 1][y - 1]));
                        } else {
                            connections.add(new DefaultConnection<>(pos, nodes[x - 1][y + 1]));
                            connections.add(new DefaultConnection<>(pos, nodes[x + 1][y + 1]));
                        }
                    }
                }
                connections.forEach(c -> graph.addConnection(c));
            }
        }


    }

    private float func(float r) {
        return (float) 1 / (1 + r);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        graph = new GraphImpl<>();

        init6();

        camera.position.set(mazeWidth * cellSize / 2, mazeHeight * cellSize / 2, 0);
        camera.update();

        MazeGenerator<Vector2> genMaze = new MazeGenerator<>();
        maze = genMaze.generate(graph, firstNode, mazeWidth * mazeHeight);

        Vector2 tmp = new Vector2();

        for (int i = 0; i < mazeHeight; i++) {
            for (int j = 0; j < mazeHeight; j++) {
                Vector2 v = nodes[i][j];
                Vector2 dir = new Vector2(0, 0);
                for (Connection<Vector2> c : maze.getConnections(v)) {
                    tmp.set(v).sub(c.getToNode()).scl(-1);
                    dir.add(tmp);
                }
                dir.nor();
                randomVectors.put(v, dir);
            }
        }

        Pixmap pixmap = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);

        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                float x = i + camera.position.x;
                float y = j + camera.position.y;
                Vector2 near = getNearNode(x, y);
                Vector2 dir = randomVectors.get(near);
                tmp.set(x, y).sub(near).nor();
                float color = 0;
                float A = func(Vector2.dst(x, y, near.x, near.y)) * Vector2.dot(dir.x, dir.y, tmp.x, tmp.y);
                int num = 0;
                for (Connection<Vector2> c : maze.getConnections(near)) {
                    num++;
                    Vector2 to = c.getToNode();
                    dir = randomVectors.get(to);
                    float B = func(Vector2.dst(x, y, to.x, to.y)) * Vector2.dot(dir.x, dir.y, tmp.x, tmp.y);

                    tmp.set(to).sub(near).scl(1 / tmp.len2()).scl(Vector2.dot(x - near.x, y - near.y, to.x - near.x, to.y - near.y));
                    color += (B - A) / (cellSize * (float) Math.sqrt(3)) * tmp.len();
                }
                color /= num;
                //color /= 6*func(0);
                color = (1 + color) / 2f;
                //System.out.println(color);
                pixmap.setColor(getColor(color));
                pixmap.drawPixel(i, j);
            }
        }
        t = new Texture(pixmap);
        pixmap.dispose();
    }

    private Vector2 getNearNode(float x, float y) {
        float dist = Float.MAX_VALUE;
        Vector2 res = null;
        for (Vector2 v : randomVectors.keys()) {
            float d = Vector2.dst2(x, y, v.x, v.y);
            if (d < dist) {
                dist = d;
                res = v;
            }
        }
        return res;
    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        batch.draw(t, 0, 0);
        batch.end();
        renderer.setColor(Color.RED);

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (Vector2 v : graph.getNodes()) {
            Vector2 dir = randomVectors.get(v);
            if (dir != null) {
                float size = 20;
                renderer.line(v.x, v.y, v.x + size * dir.x, v.y + size * dir.y);
            } else {
                System.out.println("ERROR");
            }
            int a = 0;
            for (Connection<Vector2> c : maze.getConnections(v)) {
                Vector2 to = c.getToNode();
                //renderer.line(v,to);
                if (v.x == to.x) {
                    if (v.y > to.y) a |= 8;
                    else a |= 1;
                } else if (v.x > to.x) {
                    if (v.y > to.y) a |= 16;
                    else a |= 32;
                } else {
                    if (v.y > to.y) a |= 4;
                    else a |= 2;
                }
            }
            if ((a & 1) == 0) {
                renderer.line(v.x + oX[1], v.y + oY[1], v.x + oX[2], v.y + oY[2]);
            }
            if ((a & 2) == 0) {
                renderer.line(v.x + oX[0], v.y + oY[0], v.x + oX[1], v.y + oY[1]);
            }
            if ((a & 4) == 0) {
                renderer.line(v.x + oX[5], v.y + oY[5], v.x + oX[0], v.y + oY[0]);
            }
            if ((a & 8) == 0) {
                renderer.line(v.x + oX[4], v.y + oY[4], v.x + oX[5], v.y + oY[5]);
            }
            if ((a & 16) == 0) {
                renderer.line(v.x + oX[3], v.y + oY[3], v.x + oX[4], v.y + oY[4]);
            }
            if ((a & 32) == 0) {
                renderer.line(v.x + oX[2], v.y + oY[2], v.x + oX[3], v.y + oY[3]);
            }
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
