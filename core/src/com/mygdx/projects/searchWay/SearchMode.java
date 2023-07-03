package com.mygdx.projects.searchWay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.mazeGen.MazeGenerator;
import com.mygdx.projects.utils.garphs.GraphImpl;
import com.mygdx.projects.utils.garphs.IndexedGraphImpl;
import voronoi.Voronoi;
import voronoi.graph.Graph;
import voronoi.graph.Point;

import java.util.ArrayList;
import java.util.List;

public class SearchMode implements Screen {


    final int WEIGHT = Gdx.graphics.getWidth();
    final int HEIGHT = Gdx.graphics.getHeight();

    final float W = 150f;
    final float H = W / WEIGHT * HEIGHT;


    ShapeRenderer renderer = new ShapeRenderer();
    OrthographicCamera camera = new OrthographicCamera(W, H);

    private final Vector2 tmp1 = new Vector2();
    private final Vector2 tmp2 = new Vector2();
    private final Vector2 tmp3 = new Vector2();
    private final Vector2 tmp4 = new Vector2();

    boolean m = false;

    Stage ui = new Stage();

    IndexedGraphImpl<Vector2> graph = new IndexedGraphImpl<>();

    IndexedGraphImpl<Vector2> maze = new IndexedGraphImpl<>();
    Graph g;

    @Override
    public void show() {
        List<Point> points = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            float x = MathUtils.random() * (200) - 100;
            float y = MathUtils.random() * (200) - 100;
            points.add(new Point(x, y));
        }
        Voronoi voronoi = new Voronoi(points);
        g = voronoi.getGraph();

        g.edgeStream().forEach(
                edge -> {
                    Vector2 p1 = new Vector2((float) edge.getSite1().x, (float) edge.getSite1().y);
                    Vector2 p2 = new Vector2((float) edge.getSite2().x, (float) edge.getSite2().y);
                    graph.addDoubleConnection(p1, p2);
                }
        );

        Vector2 node = null;
        for (Vector2 v : graph.getNodes()
        ) {
            node = v;
            break;
        }

        MazeGenerator<Vector2> generator = new MazeGenerator<>();
        GraphImpl<Vector2> graph1 = generator.generate(graph, node, graph.getNodeCount());
        for (Vector2 v : graph1.getNodes()) {
            for (Connection<Vector2> c : graph1.getConnections(v)) {
                maze.addConnection(c);
            }
        }

    }

    @Override
    public void render(float delta) {
        updateCamera(delta);
        ScreenUtils.clear(Color.BLACK);

        renderer.setProjectionMatrix(camera.combined);

        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (Vector2 v : graph.getNodes()) {
            //renderer.circle(v.x,v.y,.5f);
            renderer.setColor(Color.RED);
            if (m) {
                for (Connection<Vector2> connection : maze.getConnections(v)) {
                    renderer.line(connection.getFromNode(), connection.getToNode());

                }
            } else {
                for (Connection<Vector2> connection : graph.getConnections(v)) {
                    renderer.line(connection.getFromNode(), connection.getToNode());

                }
            }
            renderer.setColor(Color.WHITE);
        }
//        for (Vector2 v:p){
//            renderer.circle(v.x,v.y,1);
//        }
//        for (Point p : g.getSitePoints()){
//            renderer.circle((float) p.x,(float) p.y,0.5f);
//        }
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

    void updateCamera(float delta) {
        tmp1.set(0, 0);
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            tmp1.add(0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            tmp1.add(1, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            tmp1.add(0, -1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            tmp1.add(-1, 0);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.zoom = Math.max(0.1f, camera.zoom - delta * 10);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.zoom = Math.min(5, camera.zoom + delta * 10);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            m = !m;
        }
        tmp1.scl(30 * delta);
        camera.position.add(tmp1.x, tmp1.y, 0);
        camera.update();
    }

}
