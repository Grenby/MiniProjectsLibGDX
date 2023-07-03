package com.mygdx.projects.Light;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NewScreen implements Screen, InputProcessor {

    BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"), Gdx.files.internal("fonts/font.png"), false);

    Array<Vector2> vertices = new Array<>();
    Array<Vector2> visible = new Array<>();
    Vector2 light = new Vector2(-100, -100);
    ShapeRenderer renderer = new ShapeRenderer();

    Node first = null;
    boolean step = false;

    boolean start = false;
    boolean end = false;

    Vector2 tmp = new Vector2();
    Vector2 tmp1 = new Vector2();

    static class Node {
        Node next = null;
        Node prev = null;
        int id;
        Vector2 vertices = null;
        Vector2 nor = null;
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        vertices.add(new Vector2(638.0f, 1120.0f));
        vertices.add(new Vector2(671.0f, 878.0f));
        vertices.add(new Vector2(724.0f, 728.0f));
        vertices.add(new Vector2(851.0f, 702.0f));
        vertices.add(new Vector2(903.0f, 924.0f));
        vertices.add(new Vector2(872.0f, 1058.0f));
        vertices.add(new Vector2(936.0f, 1154.0f));
        vertices.add(new Vector2(1011.0f, 1123.0f));
        vertices.add(new Vector2(1041.0f, 919.0f));
        vertices.add(new Vector2(932.0f, 786.0f));
        vertices.add(new Vector2(947.0f, 613.0f));
        vertices.add(new Vector2(927.0f, 512.0f));
        vertices.add(new Vector2(710.0f, 449.0f));
        vertices.add(new Vector2(537.0f, 449.0f));
        vertices.add(new Vector2(228.0f, 543.0f));
        vertices.add(new Vector2(140.0f, 465.0f));
        vertices.add(new Vector2(93.0f, 642.0f));
        vertices.add(new Vector2(104.0f, 842.0f));
        vertices.add(new Vector2(198.0f, 1078.0f));
        vertices.add(new Vector2(265.0f, 1012.0f));
        vertices.add(new Vector2(203.0f, 869.0f));
        vertices.add(new Vector2(151.0f, 668.0f));
        vertices.add(new Vector2(222.0f, 621.0f));
        vertices.add(new Vector2(297.0f, 622.0f));
        vertices.add(new Vector2(348.0f, 763.0f));
        vertices.add(new Vector2(340.0f, 866.0f));
        vertices.add(new Vector2(324.0f, 937.0f));
        vertices.add(new Vector2(315.0f, 1059.0f));
        vertices.add(new Vector2(348.0f, 1161.0f));
        vertices.add(new Vector2(441.0f, 1030.0f));
        vertices.add(new Vector2(446.0f, 915.0f));
        vertices.add(new Vector2(400.0f, 777.0f));
        vertices.add(new Vector2(384.0f, 734.0f));
        vertices.add(new Vector2(339.0f, 634.0f));
        vertices.add(new Vector2(327.0f, 585.0f));
        vertices.add(new Vector2(348.0f, 552.0f));
        vertices.add(new Vector2(367.0f, 559.0f));
        vertices.add(new Vector2(380.0f, 631.0f));
        vertices.add(new Vector2(426.0f, 688.0f));
        vertices.add(new Vector2(568.0f, 659.0f));
        vertices.add(new Vector2(585.0f, 582.0f));
        vertices.add(new Vector2(595.0f, 527.0f));
        vertices.add(new Vector2(666.0f, 534.0f));
        vertices.add(new Vector2(694.0f, 618.0f));
        vertices.add(new Vector2(609.0f, 767.0f));
        vertices.add(new Vector2(556.0f, 842.0f));
        vertices.add(new Vector2(532.0f, 916.0f));
        vertices.add(new Vector2(622.0f, 843.0f));
        vertices.add(new Vector2(673.0f, 765.0f));
        vertices.add(new Vector2(696.0f, 704.0f));
        vertices.add(new Vector2(728.0f, 614.0f));
        vertices.add(new Vector2(723.0f, 525.0f));
        vertices.add(new Vector2(603.0f, 497.0f));
        vertices.add(new Vector2(621.0f, 479.0f));
        vertices.add(new Vector2(762.0f, 482.0f));
        vertices.add(new Vector2(790.0f, 568.0f));
        vertices.add(new Vector2(763.0f, 640.0f));
        vertices.add(new Vector2(704.0f, 725.0f));
        vertices.add(new Vector2(685.0f, 772.0f));
        vertices.add(new Vector2(646.0f, 854.0f));
        vertices.add(new Vector2(587.0f, 958.0f));
        vertices.add(new Vector2(553.0f, 1121.0f));
        light.set(699.0f, 566.0f);


    }

    boolean right(Node n) {
        Vector2 v = n.vertices;
        Vector2 l = n.prev.vertices;
        Vector2 r = n.next.vertices;

        tmp.set(v).sub(l);
        tmp1.set(r).sub(l);

        return tmp.crs(tmp1) < 0;
    }

    void rend(Node start) {
        Node s = start;
        do {
            renderer.line(start.vertices, start.next.vertices);
            renderer.circle(start.vertices.x, start.vertices.y, 5);
            start = start.next;
        } while (s != start);
    }

    boolean canVisible(Node n1, Node n2) {
        if (n2.nor.isCollinear(n1.nor, 0.001f))
            return true;
        tmp.set(n2.vertices).sub(n1.vertices);
        tmp.rotate90(-1);
        float d = tmp.dot(n1.vertices);
        return d <= 0;
    }

    Vector2 intersect(Vector2 p1, Vector2 p2, Vector2 p) {
        final float k1 = p1.crs(p);
        final float k2 = p.crs(p2);
        if (k2 == 0) return p2.cpy();
        final float x = Math.abs(k1 / k2);
        Vector2 v = new Vector2(p1.x + x * p2.x, p1.y + x * p2.y).scl(1 / (1 + x));
        //System.out.println(p1+" "+p2+" "+p+" "+v);
        return v;
    }

    Node goBack(Node n) {
        Node prev = n.prev;
        tmp.set(n.vertices).rotate90(-1);
        while (prev.vertices.dot(tmp) > 0 || n.vertices.dot(prev.vertices) < 0) {
            prev = prev.prev;
        }
        Vector2 v = intersect(prev.vertices, prev.next.vertices, n.vertices);
        if (v.len2() < n.vertices.len2()) {
            Node m = prev;
            prev = prev.next;
            while (prev != n) {
                double d = m.vertices.crs(prev.vertices);
                if (d < 0) {
                    m = prev.vertices.len2() < n.vertices.len2() ? prev : m;
                }
                prev = prev.next;
            }
            m.next = n;
            n.prev = m;
            return m;
        } else {
            prev = prev.next;
            prev.vertices.set(v);
            prev.nor.set(v).nor();
            prev.next = n;
            n.prev = prev;
            return n;
        }
    }

    Node goForward(Node n) {
        Node next = n.next;
        tmp.set(n.vertices).rotate90(-1);
        while (next.vertices.dot(tmp) < 0 || n.vertices.dot(next.vertices) < 0) {
            next = next.next;
        }
        next = next.prev;
        Vector2 v = intersect(next.vertices, next.next.vertices, n.vertices);
        if (v.len2() < n.vertices.len2()) {
            Node m = next;
            next = next.prev;
            while (next != n) {
                double d = m.vertices.crs(next.vertices);
                if (d < 0) {
                    m = next.vertices.len2() < n.vertices.len2() ? next : m;
                }
                next = next.prev;
            }
            m.prev = n;
            n.next = m;
            return n;
        } else {
            next.vertices.set(v);
            next.nor.set(v).nor();
            n.next = next;
            next.prev = n;

            return n;
        }
    }

    void doThisShit() {
        int aa = 0;
        Node first = new Node();
        Node tmp = first;
        for (int i = 0; i < vertices.size; i++) {
            first.id = aa;
            aa++;
            first.vertices = new Vector2(vertices.get(i)).sub(light);
            first.nor = new Vector2(first.vertices).nor();
            if (i == vertices.size - 1) {
                first.next = tmp;
                tmp.prev = first;
            } else {
                first.next = new Node();
                first.next.prev = first;
            }
            first = first.next;
        }
        System.out.println("add");
        while (!canVisible(first, first.next)) {
            first = first.next;
        }
        tmp = first;
//        first = first.next;
//        do{
//            if (!canVisible(first,first.next) && !canVisible(first.next,first.next.next)){
//                first.next = first.next.next;
//                first.next.prev = first;
//            }else {
//                first = first.next;
//            }
//        }while (tmp!=first);

        while (!canVisible(first, first.next)) {
            first = first.next;
        }


        int size = 2 * vertices.size;
        while (size > 0) {
            while (!step) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            step = false;
            if (!canVisible(first, first.next)) {
                if (right(first)) {
                    first = first.next;
                    first = goBack(first);
                } else {
                    goForward(first);
                    //first =first.next;
                }
            } else {
                first = first.next;
                size--;
            }
            //rend(first);
        }
        tmp = first.prev;
        visible.clear();
        visible.add(first.vertices.add(light));
        while (tmp != first) {
            first = first.next;
            visible.add(first.vertices.add(light));
        }

    }


    @Override
    public void render(float delta) {


        Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        renderer.begin(ShapeRenderer.ShapeType.Line);

        renderer.setColor(Color.WHITE);
        for (int i = 0; i < vertices.size; i++) {
            if (i != vertices.size - 1) {

                renderer.setColor(Color.WHITE);
                renderer.line(vertices.get(i), vertices.get(i + 1));
            }
            if (i == 0)
                renderer.setColor(Color.BLUE);
            else
                renderer.setColor(Color.WHITE);
            renderer.circle(vertices.get(i).x, vertices.get(i).y, 5);
        }
        if (start) {
            renderer.line(vertices.get(0), vertices.get(vertices.size - 1));
        }
        renderer.setColor(Color.GREEN);
        renderer.circle(light.x, light.y, 20);

        if (end && visible != null) {
            renderer.setColor(Color.RED);
            //System.out.println(visible.size);
            for (int i = 0; i < visible.size; i++) {
                renderer.line(visible.get(i), visible.get((i + 1) % visible.size));
            }
        }

        renderer.end();
        if (start && !end) {
            Thread thread = new Thread(this::doThisShit);
            end = true;
            thread.start();
        }
        if (first != null) {
            if (time < 5) {

            } else {
                step = true;
            }
            time += delta;
        }
        batch.begin();
        //font.setScale(.2f);
        font.setColor(Color.WHITE);
        for (int i = 0; i < vertices.size; i++) {
            font.draw(batch, Integer.toString(i), vertices.get(i).x - 10, vertices.get(i).y + 30);

        }
        batch.end();

    }

    float time = 0;
    SpriteBatch batch = new SpriteBatch();

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

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys
                    .R: {
                if (vertices.size > 0)
                    vertices.removeIndex(vertices.size - 1);
                visible.clear();
                break;
            }
            case Input.Keys.T: {
                start = true;
                break;
            }
            case Input.Keys.Y: {
                end = false;
                start = false;
                vertices.clear();
                visible.clear();
                break;
            }
            case Input.Keys.P: {
                for (Vector2 v : vertices) {
                    System.out.println("vertices.add(new Vector2(" + v.x + "f," + v.y + "f));");
                }
                System.out.println("light.set(" + light.x + "f," + light.y + "f);");
                break;
            }
        }
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

    boolean rightDown = false;

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0) {
            vertices.add(new Vector2(screenX, Gdx.graphics.getHeight() - screenY));
        } else {
            end = false;
            rightDown = true;
            visible.clear();
            light.set(screenX, Gdx.graphics.getHeight() - screenY);
            System.out.println(light);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 1) {
            rightDown = false;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (rightDown) {
            end = false;
            //visible.clear();
            light.set(screenX, Gdx.graphics.getHeight() - screenY);
            System.out.println(light);
        }
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
}
