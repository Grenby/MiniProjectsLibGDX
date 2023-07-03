package com.mygdx.projects.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class RoomScreen1 implements Screen, InputProcessor {

    private static class Node implements Pool.Poolable {
        final Vector2
                data = new Vector2();
        Node
                previous = this,
                next = this;

        @Override
        public void reset() {
            previous = this;
            next = this;
        }
    }


    private static final int WIDTH = Gdx.graphics.getWidth();
    private static final int HEIGHT = Gdx.graphics.getHeight();

    private final ShapeRenderer renderer = new ShapeRenderer();

    private final Pool<Node> nodePool = new Pool<Node>() {
        @Override
        protected Node newObject() {
            return new Node();
        }
    };

    private final Node firstVertex = new Node();

    private final Vector2
            start = new Vector2(WIDTH / 2f, HEIGHT / 2f),
            tmp1 = new Vector2(),
            tmp2 = new Vector2(),
            tmp3 = new Vector2();

    private Node
            lastVertex = firstVertex,
            firstVisibleVertex;


    private int num = 0;
    //press R for render visible vertices
    private boolean renderVisibleVertices = false;

    private Node connect(Node current, Node previous, Node next) {

        current.next = next;
        next.previous = current;

        current.previous = previous;
        previous.next = current;

        return current;
    }

    private float oneSide(Vector2 point, Vector2 v1, Vector2 v2) {

        tmp1.set(v2).sub(v1);
        tmp1.rotate90(1);

        tmp2.set(point).sub(v1);

        return tmp1.dot(tmp2);
    }

    /**
     * @param dir point on ray
     * @param v1  first point of segment
     * @param v2  second point if segment
     * @return tmp3 vector for setting
     */
    private Vector2 intersection(Vector2 dir, Vector2 v1, Vector2 v2) {
        final float k1 = v1.crs(dir);
        final float k2 = dir.crs(v2);
        if (k2 == 0) return tmp3.set(v2);
        final float k = k1 * k2 > 0 ? k1 / k2 : -k1 / k2;
        return tmp3.set(v1).mulAdd(v2, k).scl(1 / (1 + k));
    }

    private void goBack(Node current) {
        Node search = current;
        do {
            search = search.previous;
        } while (oneSide(search.data, Vector2.Zero, current.data) >= 0);
        tmp1.set(intersection(current.data, search.data, search.next.data));
        if (tmp1.len2() > current.data.len2()) connect(search.next, search, current).data.set(tmp1);
        else {
            connect(search.next, search, current);
            goStraight(search.next);
        }
    }

    private void goStraight(Node current) {
        Node search = current;
        do {
            search = search.next;
        } while (oneSide(search.data, Vector2.Zero, current.data) <= 0);
        connect(current.next, current, search).data.set(intersection(current.data, search.previous.data, search.data));
    }

    private void calculate() {
        Node node = firstVertex;
        firstVisibleVertex = nodePool.obtain();
        firstVisibleVertex.data.set(node.data);
        node = node.next;
        while (node != firstVertex) {
            firstVisibleVertex = connect(nodePool.obtain(), firstVisibleVertex, firstVisibleVertex.next);
            firstVisibleVertex.data.set(node.data);
            node = node.next;
        }

        int processed = 0;
        node = firstVisibleVertex;
        while (processed <= num) {
            if (oneSide(node.next.data, Vector2.Zero, node.data) >= 0) {
                node = node.next;
                processed++;
            } else {
                if (oneSide(node.next.data, node.previous.data, node.data) > 0) {
                    node = node.next;
                    goBack(node);
                } else {
                    goStraight(node);
                    //num = num - goStraight(iterator) + 1;
                    node = node.next;
                }
            }
        }
    }

    private void reset() {
        num = 0;
        Node node = firstVertex.next;
        while (node != firstVertex) {
            nodePool.free(node);
            node = node.next;
        }
        node = firstVisibleVertex.next;
        while (node != firstVisibleVertex) {
            Node free = node;
            node = node.next;
            nodePool.free(free);
        }
        nodePool.free(firstVisibleVertex);

        firstVisibleVertex = null;
        renderVisibleVertices = false;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.R:
                renderVisibleVertices = !renderVisibleVertices;
                break;
            case Input.Keys.D:
                reset();
        }
        if (renderVisibleVertices) calculate();
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenY = HEIGHT - screenY;
        renderVisibleVertices = false;
        switch (button) {
            case 0: {
                if (num != 0) lastVertex = connect(nodePool.obtain(), lastVertex, firstVertex);
                lastVertex.data.set(screenX, screenY).sub(start);
                num++;
                break;
            }
            case 1: {
                break;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.BLUE);
        Node node = firstVertex;
        do {
            renderer.line(tmp1.set(node.data).add(start), tmp2.set(node.next.data).add(start));
            node = node.next;
        } while (node != firstVertex);

        if (renderVisibleVertices) {
            renderer.setColor(Color.WHITE);
            node = firstVisibleVertex;
            do {
                renderer.line(tmp1.set(node.data).add(start), tmp2.set(node.next.data).add(start));
                node = node.next;
            } while (node != firstVisibleVertex);
        }
        renderer.end();
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.WHITE);
        renderer.circle(start.x, start.y, 10);
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
