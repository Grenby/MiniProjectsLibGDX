package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.projects.Resource;
import com.mygdx.projects.utils.FPSui;
import com.mygdx.projects.utils.MyMath;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PerlinShaderMode extends InputAdapter implements Screen {


    static final int WIDTH = Gdx.graphics.getWidth();
    static final int HEIGHT = Gdx.graphics.getHeight();

    private final Set<Integer> keysForUpdate = new HashSet<>(Arrays.asList(
            Input.Keys.W, Input.Keys.D, Input.Keys.S, Input.Keys.A, Input.Keys.UP, Input.Keys.DOWN
    ));

    private final float[] windowCoords = new float[3 * WIDTH * HEIGHT];

    private final RandomXS128 random = new RandomXS128();

    private final OrthographicCamera camera = new OrthographicCamera(WIDTH, HEIGHT);

    private final Set<Integer> keyPress = new HashSet<>();
    private float scroll = 0;

    private final IntBuffer transposeBuffer;
    private final FloatBuffer directBuffer;
    private final FloatBuffer cameraBuffer;
    private ShaderProgram shader;

    private float frequency = 2f;
    private float amplitude = 0.5f;
    private float cellSize = 100;
    private int octaves = 8;


    private int u_octaves;
    private int u_cellSize;
    private int u_amplitude;
    private int u_frequency;
    private int u_projection;

    private Mesh mesh;

    private final Stage uiStage = new Stage();
    int NUM = 256;
    int[] transitions = new int[NUM * 2];


    public PerlinShaderMode() {
        Vector2 tmp = new Vector2();
        float[] directions = new float[NUM * 2];

        for (int i = 0; i < NUM; i++) {
            transitions[i] = i;
            MyMath.angleToVector(tmp, getAngle());
            directions[2 * i] = tmp.x;
            directions[2 * i + 1] = tmp.y;
        }
        for (int i = 0; i < NUM; i++) {
            int j = transitions[i];
            transitions[i] = transitions[random.nextInt(NUM)];
            transitions[transitions[i]] = j;
        }
        System.arraycopy(transitions, 0, transitions, NUM, NUM);

        transposeBuffer = ByteBuffer.allocateDirect(transitions.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        transposeBuffer.put(transitions).position(0);

        directBuffer = ByteBuffer.allocateDirect(directions.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        directBuffer.put(directions).position(0);

        camera.position.set(WIDTH / 2f, HEIGHT / 2f, 0);
        camera.update();

        cameraBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        cameraBuffer.put(camera.invProjectionView.getValues()).position(0);

        //     frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,WIDTH,HEIGHT,false,false);

    }

    private float getAngle() {
        return MathUtils.PI2 * random.nextFloat();
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

    private void initUI() {
        Skin skin = Resource.getUISkin();
        VerticalGroup root = new VerticalGroup();
        VerticalGroup verticalGroup = new VerticalGroup();
        TextButton textButton = new TextButton("info", skin, "toggle");

        verticalGroup.setVisible(false);

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
            final Label textValue = new Label(Integer.toString(octaves), skin);
            slider = new Slider(1, 16, 1, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Integer.toString((int) getValue()));
                    octaves = (int) getValue();
                    return set;
                }
            };
            slider.setValue(octaves);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Octaves: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
        {
            final Label textValue = new Label(Float.toString(cellSize), skin);
            slider = new Slider(1, 255, 1, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Integer.toString((int) getValue()));
                    cellSize = getValue();
                    return set;
                }
            };
            slider.setValue(cellSize);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Size: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
        {
            final Label textValue = new Label(Float.toString(amplitude), skin);
            slider = new Slider(0.01f, 0.99f, 0.01f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    amplitude = getValue();
                    return set;
                }
            };
            slider.setValue(amplitude);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Amplitude: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
        {
            final Label textValue = new Label(Float.toString(frequency), skin);
            slider = new Slider(1, 20, 0.05f, false, skin) {
                @Override
                public boolean setValue(float value) {
                    boolean set = super.setValue(value);
                    textValue.setText(Float.toString((int) (100 * getValue()) / 100f));
                    frequency = getValue();
                    return set;
                }
            };
            slider.setValue(frequency);
            horizontalGroup = new HorizontalGroup();
            horizontalGroup.addActor(new Label("Frequency: ", skin));
            horizontalGroup.addActor(slider);
            horizontalGroup.addActor(textValue);
            verticalGroup.addActor(horizontalGroup);
        }
        verticalGroup.addActor(new FPSui(skin));
        verticalGroup.left();
        verticalGroup.columnLeft();
        root.setPosition(0, HEIGHT - verticalGroup.getHeight());
        root.left();
        root.columnLeft();
        uiStage.addActor(root);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, this));

        initUI();

        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shader = new ShaderProgram(
                Gdx.files.internal("Sh/perlinNoise/vertex.glsl").readString(),
                Gdx.files.internal("Sh/perlinNoise/fragment.glsl").readString());

        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());

        u_octaves = shader.getUniformLocation("u_octaves");
        u_cellSize = shader.getUniformLocation("u_cellSize");
        int u_transitions = shader.getUniformLocation("u_transitions[0]");
        int u_directions = shader.getUniformLocation("u_directions[0]");
        u_frequency = shader.getUniformLocation("u_frequency");
        u_amplitude = shader.getUniformLocation("u_amplitude");

        u_projection = shader.getUniformLocation("u_projection");

        mesh = new Mesh(true, WIDTH * HEIGHT, 0, VertexAttribute.Position());
        updateMesh();


        shader.bind();
        final GL20 gl = Gdx.gl20;
        gl.glUniform1iv(u_transitions, 1, transposeBuffer);
        gl.glUniform2fv(u_directions, 1, directBuffer);

    }

    void updateMesh() {
        int id_vertex = 0;
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                windowCoords[id_vertex++] = 2f * i / WIDTH - 1;
                windowCoords[id_vertex++] = 2f * j / HEIGHT - 1;
                windowCoords[id_vertex++] = 0;
            }
        }
        mesh.setVertices(windowCoords);
    }

    void update(float delta) {

        uiStage.act(delta);

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
            cameraBuffer.put(camera.invProjectionView.getValues()).position(0);
        }
    }

    @Override
    public void render(float delta) {

        update(delta);

        final GL20 gl = Gdx.gl20;

        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        shader.bind();

        gl.glUniformMatrix4fv(u_projection, 1, false, cameraBuffer);

        gl.glUniform1i(u_octaves, octaves);
        gl.glUniform1f(u_cellSize, cellSize);
//        for (int i=0;i<transitions.length;i++){
//            gl.glUniform1i(u_transitions+i,transitions[i]);
//        }
        gl.glUniform1f(u_amplitude, amplitude);
        gl.glUniform1f(u_frequency, frequency);
        //gl.glUniform1f(u_1,arr[0]);
        mesh.render(shader, GL20.GL_POINTS);
        shader.end();
        uiStage.draw();
        ShapeRenderer renderer = new ShapeRenderer();
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
