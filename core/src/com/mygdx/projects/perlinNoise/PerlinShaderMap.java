package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PerlinShaderMap implements Screen {

    static final int WIDTH = Gdx.graphics.getWidth();
    static final int HEIGHT = Gdx.graphics.getHeight();

    private final RandomXS128 random = new RandomXS128();

    PerspectiveCamera camera = new PerspectiveCamera(67, WIDTH, HEIGHT);
    FirstPersonCameraController controller = new FirstPersonCameraController(camera);

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


    public PerlinShaderMap() {
        Vector2 tmp = new Vector2();
        int NUM = 256;
        int[] transitions = new int[NUM * 2];
        float[] directions = new float[NUM * 2];

        for (int i = 0; i < NUM; i++) {
            transitions[i] = i;
            tmp.set(1, 0).rotateRad(getAngle());
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


        camera.up.set(0, 0, 1);
        camera.direction.set(-1, 0, 0).nor();
        camera.update();

        cameraBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        cameraBuffer.put(camera.invProjectionView.getValues()).position(0);

        //     frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888,WIDTH,HEIGHT,false,false);

    }

    private float getAngle() {
        return MathUtils.PI2 * random.nextFloat();
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
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage, controller));
        camera.near = 0.01f;
        initUI();

        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shader = new ShaderProgram(
                Gdx.files.internal("Sh/perlinNoise/map/vertex.glsl").readString(),
                Gdx.files.internal("Sh/perlinNoise/map/fragment.glsl").readString());

        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());

        u_octaves = shader.getUniformLocation("u_octaves");
        u_cellSize = shader.getUniformLocation("u_cellSize");
        int u_transitions = shader.getUniformLocation("u_transitions[0]");
        int u_directions = shader.getUniformLocation("u_directions[0]");
        u_frequency = shader.getUniformLocation("u_frequency");
        u_amplitude = shader.getUniformLocation("u_amplitude");
        u_projection = shader.getUniformLocation("u_projection");

        final GL20 gl = Gdx.gl20;

        shader.bind();
        gl.glUniform1iv(u_transitions, 1, transposeBuffer);
        gl.glUniform2fv(u_directions, 1, directBuffer);


        buildMeshMap();
    }

    short getVertexIndices(int x, int y, int h) {
        if ((x * (h + 1) + y > 0xFFFF)) {
            System.out.println((x * (h + 1) + y));
        }
        return (short) ((x * (h + 1) + y) & 0xFFFF);
    }

    //кол-во вершим = (w+1)(h+1)
    float[] buildGrid(int w, int h, float size) {
        w++;
        h++;
        final float[] vertices = new float[w * h * 3];
        int vID = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                vertices[vID++] = i * size;
                vertices[vID++] = j * size;
                vertices[vID++] = 0;
            }
        }
        return vertices;
    }

    short[] buildIndices(int w, int h) {
//        int n = 0xFFFF;
//        System.out.println(n);
//        System.out.println(0xFFFF);
//        System.out.println((short)(n & 0xbFFFF));
        short[] indices = new short[w * h * 6];
        int iID = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                indices[iID++] = getVertexIndices(i, j, h);
                indices[iID++] = getVertexIndices(i + 1, j, h);
                indices[iID++] = getVertexIndices(i + 1, j + 1, h);

                indices[iID++] = getVertexIndices(i, j, h);
                indices[iID++] = getVertexIndices(i + 1, j + 1, h);
                indices[iID++] = getVertexIndices(i, j + 1, h);
            }
        }
        return indices;
    }

    ModelBatch batch = new ModelBatch();

    void buildMeshMap() {
        int w = 999;
        int h = 64;
        float[] vertices = buildGrid(w, h, 0.05f);
        short[] indices = buildIndices(w, h);

        mesh = new Mesh(
                true,
                vertices.length / 3,
                indices.length,
                VertexAttribute.Position()
        );
        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        renderable.meshPart.mesh = mesh;
        renderable.meshPart.size = indices.length;
        renderable.meshPart.offset = 0;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.material = new Material(
                new ColorAttribute(ColorAttribute.Diffuse, Color.GOLD)
        );
    }

    void update(float delta) {
        uiStage.act(delta);
        controller.update(delta);
        cameraBuffer.put(camera.combined.getValues()).position(0);
    }

    Renderable renderable = new Renderable();

    ShapeRenderer renderer = new ShapeRenderer();

    @Override
    public void render(float delta) {
        update(delta);

        final GL20 gl = Gdx.gl20;

//        gl.glClearColor(0, 0, 0, 1);
//        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.gl.glClearColor(0f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


//        shader.bind();
//
//        gl.glUniformMatrix4fv(u_projection, 1, false, cameraBuffer);
//
//
//        gl.glUniform1i(u_octaves, octaves);
//        gl.glUniform1f(u_cellSize, cellSize);
//        gl.glUniform1f(u_amplitude, amplitude);
//        gl.glUniform1f(u_frequency,frequency);
//
//        mesh.render(shader, GL20.GL_TRIANGLES);

        batch.begin(camera);
        batch.render(renderable);
        batch.end();
        uiStage.draw();

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.RED);
        renderer.line(-10, 0, 0, -10, 0, 10);
        renderer.line(0, 0, 0, 0, 60 * 0.05f, 0);
        renderer.line(1000 * 0.05f, 0, 0, 1000 * 0.05f, 60 * 0.05f, 0);
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
