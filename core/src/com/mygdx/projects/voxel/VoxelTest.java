/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mygdx.projects.voxel;

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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.MyInput;

public class VoxelTest implements Screen {
    SpriteBatch spriteBatch;
    BitmapFont font;
    ModelBatch modelBatch;
    PerspectiveCamera camera;
    Environment lights;
    FirstPersonCameraController controller;
    VoxelWorld voxelWorld;

    boolean cameraUpdate = false;

    float cellSize = 10;

    private Stage uiStage = new Stage();

    Vector3 p2 = new Vector3(0, 0, 0);
    Vector3 center = new Vector3(0, 0, 0);
    Vector3 tmp = new Vector3();

    MyInput myInput = new MyInput();

    ShapeRenderer renderer = new ShapeRenderer();
    Matrix4 transformRender = new Matrix4();
    Bresenham2 bresenham2 = new Bresenham2();

    private void setVector(Ray ray, Vector3 v) {
        if (MathUtils.isEqual(ray.direction.y, 0, 0.1f)) return;
        float t = -ray.origin.y / ray.direction.y;
        if (t < 0) return;

        v.set(ray.direction).scl(t).add(ray.origin);
    }

    private void initInput() {
        myInput.addCallback(Input.Keys.ESCAPE, () -> {
            Gdx.app.exit();
        });
        myInput.addKeys(Input.Keys.W, Input.Keys.D, Input.Keys.S, Input.Keys.A);
        myInput.setTouchDown((screenX, screenY, pointer, button) -> {
            setVector(camera.getPickRay(screenX, screenY), tmp);
            System.out.println(tmp);
            if (button == 0)
                voxelWorld.set((int) tmp.x, (int) tmp.y, tmp.z, (byte) 1);
            else if (button == 1) {
                voxelWorld.set((int) tmp.x, (int) tmp.y, tmp.z, (byte) 0);
            }
            //mouseDown = true;
        });
    }

    private void initUI() {
        Skin skin = Resource.getUISkin();
        VerticalGroup root = new VerticalGroup();
        VerticalGroup verticalGroup = new VerticalGroup();
        TextButton textButton = new TextButton("info", skin, "toggle");

        verticalGroup.setVisible(true);
        textButton.setChecked(true);
        textButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                verticalGroup.setVisible(!textButton.isChecked());
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        uiStage.addActor(textButton);
        root.addActor(verticalGroup);
        Slider slider;
        HorizontalGroup horizontalGroup;
        {
            final Label textValue = new Label(Float.toString(camera.far), skin);
            slider = new Slider(100, 2000, 100, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Integer.toString((int) getValue()));
                    camera.far = getValue();
                    camera.update();
                    return set;
                }
            };
            slider.setValue(camera.far);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Far: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }

        //verticalGroup.addActor(new FPSui(skin));
        verticalGroup.addActor(new Label("fps:", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                setText("fps: " + Gdx.graphics.getFramesPerSecond() + ", #visible chunks: " + voxelWorld.renderedChunks
                        + "/" + voxelWorld.numChunks);
            }
        });
        verticalGroup.left();
        verticalGroup.columnLeft();
        root.setPosition(0, Gdx.graphics.getHeight() - verticalGroup.getHeight());
        root.left();
        root.columnLeft();
        uiStage.addActor(root);
    }


    private void createHexagon(int x0, int z0) {
        int size = 10;
        for (int i = 0; i < size; i++) {
            voxelWorld.set(x0 + i, 0, z0, (byte) 1);
            voxelWorld.set(x0 + i, 0, z0 + 2 * size, (byte) 1);
        }

        int rightX = x0 + size + (int) (size * Math.cos(MathUtils.PI / 3));
        int rightZ = z0 + size;

        for (GridPoint2 g : bresenham2.line(x0 + size, z0, rightX, rightZ)) {
            voxelWorld.set(g.x, 0, g.y, (byte) 1);
        }
        for (GridPoint2 g : bresenham2.line(x0 + size, z0 + 2 * size, rightX, rightZ)) {
            voxelWorld.set(g.x, 0, g.y, (byte) 1);
        }
        for (GridPoint2 g : bresenham2.line(x0, z0, x0 - (int) (size * Math.cos(MathUtils.PI / 3)), rightZ)) {
            voxelWorld.set(g.x, 0, g.y, (byte) 1);
        }
        for (GridPoint2 g : bresenham2.line(x0, z0 + 2 * size, x0 - (int) (size * Math.cos(MathUtils.PI / 3)), rightZ)) {
            voxelWorld.set(g.x, 0, g.y, (byte) 1);
        }
    }

    @Override
    public void show() {
        //createCone();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        modelBatch = new ModelBatch();
        DefaultShader.defaultCullFace = GL20.GL_FRONT_AND_BACK;
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.5f;
        camera.far = 500;
        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(500);
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, controller, myInput));

        lights = new Environment();
        lights.set(new ColorAttribute(ColorAttribute.Specular, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(new DirectionalLight().set(1, 1, 1, 1, -1, 1));

        Texture texture = new Texture(Gdx.files.internal("tiles.png"));
        TextureRegion[][] tiles = TextureRegion.split(texture, 32, 32);
        MathUtils.random.setSeed(0);
        voxelWorld = new VoxelWorld(tiles[0], 100, 1, 100, camera.position, camera.frustum);
        //voxelWorld = new VoxelWorld(tiles[0],200,1,40);
        PerlinNoiseGenerator.generateVoxels(voxelWorld, 0, 12, 10);
        camera.position.set(50, 50, 50);
        camera.lookAt(100, 0, 100);
        camera.update();
        //renderer.getTransformMatrix().
        transformRender.translate(0, 0, 0);
        transformRender.rotateRad(1, 0, 0, -MathUtils.PI / 2f);
        transformRender.rotateRad(0, 0, 1, -MathUtils.PI / 2f);
        initInput();
        initUI();
        setVector(camera.getPickRay(Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f), center);
//		for (int i=0;i<100;i++)
//			createHexagon(10*i + 10,10);
        //createHexagon(10,10);
        //voxelWorld.setCube(0,0,0,10000,1,1000,(byte)1);

    }

    @Override
    public void render(float delta) {
        uiStage.act(delta);
        //stage.act(delta);update(delta);

        ScreenUtils.clear(0.4f, 0.4f, 0.4f, 1f, true);

        modelBatch.begin(camera);
        modelBatch.render(voxelWorld, lights);
        //modelBatch.render(modelInstances[0],lights);
        modelBatch.end();
        controller.update(delta);

        renderer.setProjectionMatrix(camera.combined);
        renderer.setTransformMatrix(transformRender);
        renderer.begin(ShapeRenderer.ShapeType.Line);

        renderer.setColor(Color.PINK);

        for (int i = 0; i <= 100; i++) {
            renderer.line(i, 0, 0, i, 100, 0);
            renderer.line(0, i, 0, 100, i, 0);
        }
        renderer.setColor(Color.BLUE);
        renderer.line(0, 0, 0, 100, 0, 0);
        renderer.setColor(Color.RED);
        renderer.line(0, 0, 0, 0, 100, 0);
        renderer.setColor(Color.GREEN);
        renderer.line(0, 0, 0, 0, 0, 100);
        renderer.setColor(Color.WHITE);
        renderer.line(center, p2);

        renderer.end();
//
        spriteBatch.begin();

        spriteBatch.end();

        uiStage.draw();

    }

    @Override
    public void resize(int width, int height) {
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
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
