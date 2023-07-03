package com.mygdx.projects.ocTree;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;


public class MyGdxGame extends ApplicationAdapter {
    static SpriteBatch batch;
    static PerspectiveCamera camera;
    static ModelBatch modelBatch;
    static Environment environment;

    static Array<GameObject> gObjs = new Array<GameObject>();
    static int MAP_SIZE = 50;
    static OcTree octTree;
    static private BitmapFont font;
    FirstPersonCameraController fpsCameraControl;
    boolean useOcTree = true;

    public static void addObjects() {
        for (int i = 0; i < 1000; i++) {
            TestCube.build();
        }
    }

    public static void addObj(GameObject gObj) {
        if (!gObjs.contains(gObj, true)) {
            gObjs.add(gObj);
        }
    }

    @Override
    public void create() {

        addObjects();

        //Gdx.graphics.setDisplayMode(1024, 768, false);
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.setColor(Color.PURPLE);

        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = MAP_SIZE * 2f;
        camera.position.set(MAP_SIZE, MAP_SIZE, MAP_SIZE);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, -1, 0);
        camera.update();

        fpsCameraControl = new FirstPersonCameraController(camera);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

        modelBatch = new ModelBatch();

        octTree = new OcTree(new BoundingBox().set(new Vector3(-MAP_SIZE, -MAP_SIZE, -MAP_SIZE), new Vector3(MAP_SIZE, MAP_SIZE, MAP_SIZE)));

    }

    public void checkCollisions() {

        if (useOcTree) {
            // Octree
            for (GameObject gObj : gObjs) {
                Array<GameObject> possibleCollObjs = octTree.retrieve(gObj.getTranslatedBoundingBox());
                for (GameObject possibleCollObj : possibleCollObjs) {
                    if (gObj != possibleCollObj) {
                        if (gObj.getTranslatedBoundingBox().intersects(possibleCollObj.getTranslatedBoundingBox())) {
                            gObj.debugPaint = 1;
                            gObj.setColor(Color.PURPLE);
                        }
                    }
                }
            }
        } else {
            // Brute force
            for (int i = 0; i < gObjs.size; i++) {
                GameObject A = gObjs.get(i);
                for (GameObject gObj : gObjs) {
                    GameObject B = gObj;
                    if (A != B) {
                        if (A.getTranslatedBoundingBox().intersects(B.getTranslatedBoundingBox())) {
                            if (gObj.debugPaint == 0)
                                A.setColor(Color.BLUE); // For some reason oct-tree couldn't find this collision.
                        }
                    }
                }
            }
        }
    }

    public void checkCollisionsRay(Ray ray) {
        if (useOcTree) {
            // Octree
            Array<GameObject> possibleCollObjs = octTree.retrieveRay(ray);
            for (GameObject possibleCollObj : possibleCollObjs) {
                if (Intersector.intersectRayBoundsFast(ray, possibleCollObj.getTranslatedBoundingBox())) {
                    possibleCollObj.debugPaint = 1;
                    possibleCollObj.setColor(Color.PURPLE);
                }
            }
        } else {
            // Brute force
            for (GameObject gObj : gObjs) {
                if (Intersector.intersectRayBoundsFast(ray, gObj.getTranslatedBoundingBox())) {
                    if (gObj.debugPaint == 0)
                        gObj.setColor(Color.BLUE); // For some reason oct-tree couldn't find this collision.
                }
            }
        }
    }

    public void updateOctTree() {
        octTree.clear();
        for (GameObject gObj : gObjs)
            octTree.insert(gObj);
    }

    @Override
    public void render() {

        // Update octree
        updateOctTree();

        // Objects update and clear debug painting.
        for (GameObject gObj : gObjs) {
            gObj.refresh();
            gObj.debugPaint = 0;
            gObj.setColor(Color.GREEN);
        }

        // Input update
        if (Gdx.input.isKeyPressed(Keys.A)) {
            fpsCameraControl.keyDown(Keys.A);
        } else {
            fpsCameraControl.keyUp(Keys.A);
        }
        if (Gdx.input.isKeyPressed(Keys.S)) {
            fpsCameraControl.keyDown(Keys.S);
        } else {
            fpsCameraControl.keyUp(Keys.S);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) {
            fpsCameraControl.keyDown(Keys.D);
        } else {
            fpsCameraControl.keyUp(Keys.D);
        }
        if (Gdx.input.isKeyPressed(Keys.W)) {
            fpsCameraControl.keyDown(Keys.W);
        } else {
            fpsCameraControl.keyUp(Keys.W);
        }

        if (Gdx.input.isKeyPressed(Keys.SPACE)) {
            checkCollisions();
        }

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
            useOcTree = !useOcTree;
        }

        if (Gdx.input.isTouched()) {
            fpsCameraControl.touchDragged(Gdx.input.getDeltaX(), Gdx.input.getDeltaY(), 0);
            checkCollisionsRay(new Ray(camera.position, camera.direction));
        }

        fpsCameraControl.update();

        // Remove objects that were marked to be removed after all is done.
        Iterator<GameObject> gObjIt = gObjs.iterator();
        while (gObjIt.hasNext()) {
            GameObject gObj = gObjIt.next();
            if (gObj.isSetRemoveMe()) {
                gObjIt.remove();
            }
        }


        // Draw all.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // Models
        modelBatch.begin(camera);
        {
            for (GameObject gObj : gObjs)
                gObj.draw3D();
        }
        modelBatch.end();

        // HUD
        batch.begin();
        {
            font.setColor(Color.GREEN);
            font.draw(batch, "Objects: " + gObjs.size, 10, 20);
            font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 40);
            font.setColor(Color.BLUE);
            String modeStr = "Brute Force";
            if (useOcTree) {
                font.setColor(Color.PURPLE);
                modeStr = "OcTree";
            }
            font.draw(batch, modeStr, 10, 60);

            // Crosshair
            font.setColor(Color.GREEN);
            font.draw(batch, "+", Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        }
        batch.end();

    }
}

