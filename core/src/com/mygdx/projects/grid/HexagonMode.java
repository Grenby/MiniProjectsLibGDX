package com.mygdx.projects.grid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.MyInput;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class HexagonMode implements Screen {

    private final ShapeRenderer renderer = new ShapeRenderer();

    private final PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private FirstPersonCameraController controller;

    private final float SIZE_CELL = 10f;
    private HexagonGrid hexagonGrid;
    private HexagonGrid chunkGrid;

    MyInput myInput = new MyInput();

    final Vector3 TMP3_1 = new Vector3();
    final Vector2 TMP2_1 = new Vector2();
    final Vector2 TMP2_2 = new Vector2();

    final GridPoint3 PP3_1 = new GridPoint3();
    final GridPoint2 PP2_1 = new GridPoint2();

    Color selectColor = Color.RED;
    GridPoint2 selectedPoint = new GridPoint2(0, 0);
    boolean select = false;

    ObjectSet<GridPoint2> coloredPoints = new ObjectSet<>();
    ObjectSet<GridPoint2> chunkCenters = new ObjectSet<>();

    ShapeDrawer shapeDrawer;

    Array<Vector3> defaultChunk = new Array<>(8);
    Array<Vector3> verticesChunk = new Array<>(8);

    Label label = new Label("text", Resource.getUISkin());
    SpriteBatch batch = new SpriteBatch();
    Stage stage = new Stage(new FillViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera));
    int r = 6;

    private void initInput() {
        myInput.addCallback(Input.Keys.ESCAPE, () -> {
            Gdx.app.exit();
        });
        myInput.addCallback(Input.Keys.R, () -> {
            selectColor = Color.RED;
        });

        myInput.addCallback(Input.Keys.Y, () -> {
            selectColor = Color.YELLOW;
        });
        myInput.setMouseMoved((screenX, screenY) -> {
            if (!HexagonUtils.intersectionOXZ(camera.getPickRay(screenX, screenY), TMP3_1)) {
                select = false;
            }
            select = true;
            hexagonGrid.getHexagon(TMP3_1.z, TMP3_1.x, selectedPoint);
        });
        myInput.setTouchDown((screenX, screenY, pointer, button) -> {
            if (!HexagonUtils.intersectionOXZ(camera.getPickRay(screenX, screenY), TMP3_1)) {
            }
            hexagonGrid.getHexagon(TMP3_1.z, TMP3_1.x, PP2_1);
            if (button == 0)
                coloredPoints.add(PP2_1);
            else if (button == 1)
                coloredPoints.remove(PP2_1);
        });

    }

    void drawRing(int x0, int y0, int r, ObjectSet<GridPoint2> set) {
        x0 += r;
        for (int i = 0; i < r; i++) {
            set.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                x0--;
                y0++;
            }
        }
        x0--;
        r--;
        for (int i = 0; i < r; i++) {
            set.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                x0--;
            }
        }
        y0--;
        for (int i = 0; i < r; i++) {
            set.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                y0--;
            }
        }
        y0--;
        x0++;
        for (int i = 0; i < r; i++) {
            set.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                y0--;
                x0++;
            }
        }
        x0++;
        for (int i = 0; i < r; i++) {
            set.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                x0++;
            }
        }
        y0++;
        for (int i = 0; i < r; i++) {
            set.add(new GridPoint2(x0, y0));
            if (i != r - 1) {
                y0++;
            }
        }
    }

    void drawHexagon(int x0, int y0, int r, ObjectSet<GridPoint2> set) {
        set.add(new GridPoint2(x0, y0));
        for (int i = 1; i <= r; i++) {
            drawRing(x0, y0, i, set);
        }
    }

    GridPoint2 small_to_big(GridPoint2 out, int r) {
        int shift = 3 * r + 2;
        int area = 3 * r * r + 3 * r + 1;
        int xh, yh, zh;
        xh = Math.floorDiv(out.y + shift * out.x, area);
        yh = Math.floorDiv(-out.x - out.y + shift * out.y, area);
        zh = Math.floorDiv(out.x - shift * (out.x + out.y), area);
        return PP2_1.set(Math.floorDiv(1 + xh - yh, 3), Math.floorDiv(1 + yh - zh, 3));
    }

    boolean visibleChunk(Vector2 pos, Frustum frustum) {
        setChunk(pos);
        Plane[] planes = frustum.planes;
        for (int i = 0, len2 = planes.length; i < len2; i++) {
            if (planes[i].testPoint(verticesChunk.get(0)) != Plane.PlaneSide.Back) continue;
            if (planes[i].testPoint(verticesChunk.get(1)) != Plane.PlaneSide.Back) continue;
            if (planes[i].testPoint(verticesChunk.get(2)) != Plane.PlaneSide.Back) continue;
            if (planes[i].testPoint(verticesChunk.get(3)) != Plane.PlaneSide.Back) continue;
            if (planes[i].testPoint(verticesChunk.get(4)) != Plane.PlaneSide.Back) continue;
            if (planes[i].testPoint(verticesChunk.get(5)) != Plane.PlaneSide.Back) continue;
            return false;
        }
        return true;
    }

    void createGrid() {
        float dx = SIZE_CELL * (float) Math.sqrt(3);
        //dx/=2;
        Vector2 gX = new Vector2(dx, 0).rotateRad(MathUtils.PI / 6);
        Vector2 gY = new Vector2(0, dx);

        hexagonGrid = new HexagonGrid(gX, gY);

        chunkGrid = new HexagonGrid(
                new Vector2(gX).scl(2 * r + 1).mulAdd(gY, -r),
                new Vector2(gX).scl(r).mulAdd(gY, r + 1)
        );

        for (int i = -10; i < 10; i++) {
            for (int j = -10; j < 10; j++) {
                small_to_big(PP2_1.set(i, j), r);
                hexagonGrid.getHexagon(chunkGrid.getCenter(PP2_1, TMP2_1), PP2_1);
                if (!chunkCenters.contains(PP2_1))
                    chunkCenters.add(new GridPoint2(PP2_1));
            }
        }
        float d = SIZE_CELL / (float) Math.sqrt(3);
        for (int i = 0; i < 6; i++) {
            verticesChunk.add(new Vector3());
            Vector3 v = new Vector3();
            if (i == 0) {
                hexagonGrid.getCenter(r, 0, TMP2_1);
                v.set(TMP2_1, 0);
            } else if (i == 1) {
                hexagonGrid.getCenter(0, r, TMP2_1);
                v.set(TMP2_1, 0);
            } else if (i == 2) {
                hexagonGrid.getCenter(-r, r, TMP2_1);
                v.set(TMP2_1, 0);
            } else if (i == 3) {
                hexagonGrid.getCenter(-r, 0, TMP2_1);
                v.set(TMP2_1, 0);
            } else if (i == 4) {
                hexagonGrid.getCenter(0, -r, TMP2_1);
                v.set(TMP2_1, 0);
            } else {
                hexagonGrid.getCenter(r, -r, TMP2_1);
                v.set(TMP2_1, 0);
            }
            // v.rotateRad(Vector3.Z,MathUtils.PI/96);
            defaultChunk.add(v);
        }

    }

    void setChunk(Vector2 pos) {
        for (int i = 0; i < 6; i++)
            verticesChunk.get(i).set(pos.x, pos.y, 0).add(defaultChunk.get(i));
    }

    @Override
    public void show() {

        {
            Pixmap pixmap = new Pixmap(10, 10, Pixmap.Format.RGB888);
            pixmap.setColor(Color.GOLD);
            pixmap.fill();
            shapeDrawer = new ShapeDrawer(new SpriteBatch(), new TextureRegion(new Texture(pixmap)));
            pixmap.dispose();
        }

        {
            camera.near = 0.1f;
            camera.far = 500;
            camera.position.set(-100, 80, 0);
            camera.lookAt(0, 80, 0);
            camera.update();
            Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
            controller = new FirstPersonCameraController(camera);
            controller.setVelocity(100);
            initInput();
            Gdx.input.setInputProcessor(new InputMultiplexer(myInput, controller));
        }

        renderer.getTransformMatrix()
                .rotateRad(1, 0, 0, -MathUtils.PI / 2f)
                .rotateRad(0, 0, 1, -MathUtils.PI / 2f);
        shapeDrawer.getBatch().getTransformMatrix()
                .rotateRad(1, 0, 0, -MathUtils.PI / 2f)
                .rotateRad(0, 0, 1, -MathUtils.PI / 2f);
        batch.getTransformMatrix()
                .rotateRad(1, 0, 0, -MathUtils.PI / 2f)
                .rotateRad(0, 0, 1, -MathUtils.PI / 2f).scale(0.1f, 0.1f, 0.1f);


        stage.addActor(label);
        createGrid();

        for (Plane p : camera.frustum.planes
        ) {
            System.out.println(p);
        }
    }

    void draw0(Vector2 p, int... nums) {
        for (int n : nums) {
            shapeDrawer.line(
                    p.x + hexagonGrid.getOX()[n], p.y + hexagonGrid.getOY()[n],
                    p.x + hexagonGrid.getOX()[n + 1], p.y + hexagonGrid.getOY()[n + 1]
            );
        }
    }

    void draw(GridPoint2 p, int r) {
        PP2_1.set(p).add(r, 0);
        draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 5, 0, 1);
        for (int i = 0; i < r; i++) {
            PP2_1.add(-1, 1);
            if (i == r - 1)
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 0, 1, 2);
            else
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 0, 1);
        }
        for (int i = 0; i < r; i++) {
            PP2_1.add(-1, 0);
            if (i == r - 1)
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 1, 2, 3);
            else
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 1, 2);
        }

        for (int i = 0; i < r; i++) {
            PP2_1.add(0, -1);
            if (i == r - 1)
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 4, 2, 3);
            else
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 3, 2);
        }
        for (int i = 0; i < r; i++) {
            PP2_1.add(1, -1);
            if (i == r - 1)
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 5, 3, 4);
            else
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 4, 3);
        }
        for (int i = 0; i < r; i++) {
            PP2_1.add(1, 0);
            if (i == r - 1)
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 0, 4, 5);
            else
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 5, 4);
        }
        for (int i = 0; i < r; i++) {
            PP2_1.add(0, 1);
            if (i != r - 1)
                draw0(hexagonGrid.getCenter(PP2_1, TMP2_1), 0, 5);
        }
    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        ScreenUtils.clear(0.4f, 0.4f, 0.4f, 1f, true);
        //frustum.boundsInFrustum()

        renderer.setProjectionMatrix(camera.combined);
        stage.getBatch().setProjectionMatrix(camera.combined);
        final float[] oX = hexagonGrid.getOX();
        final float[] oY = hexagonGrid.getOY();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        {

            renderer.setColor(Color.BLUE);

            for (final GridPoint2 point : chunkCenters) {
                TMP2_1.set(hexagonGrid.getX()).scl(point.x).mulAdd(hexagonGrid.getY(), point.y);
                for (int k = 0; k < 6; k++) {
                    renderer.triangle(TMP2_1.x, TMP2_1.y, TMP2_1.x + oX[k], TMP2_1.y + oY[k], TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1]);
                }
            }
            if (select) {
                PP2_1.set(selectedPoint);
                small_to_big(PP2_1.set(selectedPoint), r);
                TMP2_1.set(chunkGrid.getX()).scl(PP2_1.x).mulAdd(chunkGrid.getY(), PP2_1.y);
                renderer.setColor(Color.RED);
                for (int k = 0; k < 6; k++) {
                    renderer.triangle(TMP2_1.x, TMP2_1.y, TMP2_1.x + oX[k], TMP2_1.y + oY[k], TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1]);
                }


                TMP2_1.set(hexagonGrid.getX()).scl(selectedPoint.x).mulAdd(hexagonGrid.getY(), selectedPoint.y);
                renderer.setColor(Color.GOLD);
                for (int k = 0; k < 6; k++) {
                    renderer.triangle(TMP2_1.x, TMP2_1.y, TMP2_1.x + oX[k], TMP2_1.y + oY[k], TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1]);
                }
            }
        }
        renderer.end();


        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.setColor(Color.WHITE);
            for (int k = 0; k < 6; k++) {
                renderer.line(TMP2_1.x + oX[k], TMP2_1.y + oY[k], SIZE_CELL, TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1], SIZE_CELL);
                renderer.line(TMP2_1.x + oX[k], TMP2_1.y + oY[k], 0, TMP2_1.x + oX[k], TMP2_1.y + oY[k], SIZE_CELL
                );

                //                renderer.line(TMP2_1.x + oX[k], TMP2_1.y + oY[k],10, TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1],10);
            }

            for (int i = -10; i < 10; i++) {
                for (int j = -10; j < 10; j++) {
//                    if (small_to_big(pp2.set(i,j),r).dst2(0,0)==0) {
//                        getRect(pp2.set(i, j));
//                        renderer.rect(aabb.x, aabb.y, aabb.width, aabb.height);
//                    }
                    TMP2_1.set(hexagonGrid.getX()).scl(i).mulAdd(hexagonGrid.getY(), j);
                    for (int k = 0; k < 6; k++) {
                        renderer.line(TMP2_1.x + oX[k], TMP2_1.y + oY[k], TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1]);
                        //                renderer.line(TMP2_1.x + oX[k], TMP2_1.y + oY[k],10, TMP2_1.x + oX[k + 1], TMP2_1.y + oY[k + 1],10);
                    }
                }
            }

            renderer.setColor(Color.BLUE);
            renderer.line(0, 0, 0, 100, 0, 0);
            renderer.setColor(Color.RED);
            renderer.line(0, 0, 0, 0, 100, 0);
            renderer.setColor(Color.GREEN);
            renderer.line(0, 0, 0, 0, 0, 100);

            renderer.setColor(Color.WHITE);
            renderer.line(Vector2.Zero, hexagonGrid.getX());
            renderer.line(Vector2.Zero, hexagonGrid.getY());

            renderer.setColor(Color.LIME);
            //   renderer.rect(bigAABB.x, bigAABB.y, bigAABB.width, bigAABB.height);
        }
        renderer.end();

        shapeDrawer.getBatch().setProjectionMatrix(camera.combined);
        shapeDrawer.getBatch().begin();

//        shapeDrawer.setColor(Color.WHITE);
//        shapeDrawer.setDefaultLineWidth(.5f);
//        for(int i=-10;i<10;i++){
//            for (int j=-10;j<10;j++){
//                if(i==j&&i==-10)
//                    draw0(hexagonGrid.getCenter(0,0,TMP2_1),0,1,2,3,4,5,6);
//                else if (i == -10)
//            }
//
//        }

        shapeDrawer.setColor(Color.GOLD);
        shapeDrawer.setDefaultLineWidth(2);
        for (GridPoint2 point : chunkCenters) {
            //if (visibleChunk(TMP2_1.set(hexagonGrid.getX()).scl(point.x).mulAdd(hexagonGrid.getY(),point.y),camera.frustum)) {
            draw(point, r);
            //}
        }
        shapeDrawer.setDefaultLineWidth(.3f);
        shapeDrawer.setColor(Color.GREEN);
        for (GridPoint2 point : chunkCenters) {
            setChunk(TMP2_1.set(hexagonGrid.getX()).scl(point.x).mulAdd(hexagonGrid.getY(), point.y));
            for (int i = 0; i < 6; i++) {
                Vector3 v1 = verticesChunk.get(i);
                Vector3 v2 = verticesChunk.get(i == 5 ? 0 : i + 1);
                shapeDrawer.line(v1.x, v1.y, v2.x, v2.y);
            }
        }
        shapeDrawer.getBatch().end();


        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        label.draw(batch, 1);
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
