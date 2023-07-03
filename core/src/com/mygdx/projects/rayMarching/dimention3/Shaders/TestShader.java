package com.mygdx.projects.rayMarching.dimention3.Shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class TestShader implements Screen {

    private class Sphere {
        float[] color = new float[]{Color.RED.r, Color.RED.g, Color.RED.b, Color.RED.a};
        float[] pos = new float[]{0, 0, 0};
        float r = 8;
    }

    private class Light {
        float[] color = new float[]{Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, 1};
        float[] pos = new float[]{-20, 10, 5};
    }


    private static final int WIDTH = Gdx.graphics.getWidth();
    private static final int HEIGHT = Gdx.graphics.getHeight();
    private static final float ANGLE = 67;
    private static final float L = (float) (HEIGHT / 2 / Math.tan(Math.toRadians(ANGLE / 2)));
    private static final float NEAR = 0.01f;
    private static final float FAR = 1000;
    private static final float K = L / NEAR;

    private float[] camPos = new float[3];
    private int u_r;
    private int u_pos;
    private int u_color;
    private int u_cam_pos;
    private int u_num;
    private int u_light_pos;
    private int u_light_color;
    private int id = 0;

    private float time = 0;

    private Vector3
            left = new Vector3(),
            down = new Vector3(),
            tmp = new Vector3(),
            dir = new Vector3();

    private ShaderProgram shader;

    private Mesh mesh;
    private PerspectiveCamera camera = new PerspectiveCamera(67, WIDTH, HEIGHT);
    private float[] vertex = new float[WIDTH * HEIGHT * 6];
    private Sphere sphere = new Sphere();
    private Sphere sphere1 = new Sphere();

    private Light light = new Light();

    private void setUniformSphere(Sphere sphere) {
        shader.setUniformf(u_r + id, sphere.r);
        shader.setUniform3fv(u_pos + id, sphere.pos, 0, 3);
        shader.setUniform4fv(u_color + id, sphere.color, 0, 4);
        id++;
    }

    private void setMesh() {
        final float W = WIDTH / 2f;
        final float H = HEIGHT / 2f;
        dir.set(camera.direction).scl(L);
        dir.sub(left.x * W, left.y * W, left.z * W).sub(down.x * H, down.y * H, down.z * H);
        int id_vertex = 0;
        for (float x = 0; x < WIDTH; x++) {
            for (float y = 0; y < HEIGHT; y++) {
                vertex[id_vertex++] = x / W - 1f;             //x in display
                vertex[id_vertex++] = (HEIGHT - y) / H - 1f;    //y in display
                vertex[id_vertex++] = 0;                  //z in display

                vertex[id_vertex++] = dir.x;              //x direction ray
                vertex[id_vertex++] = dir.y;              //y direction ray
                vertex[id_vertex++] = dir.z;              //z direction ray

                dir.add(down);
            }
            dir.sub(down.x * HEIGHT, down.y * HEIGHT, down.z * HEIGHT);
            dir.add(left);
        }
        mesh.setVertices(vertex);
    }

    private void setCameraParameters() {
        camera.position.set(-20, 0, 0);
        camera.lookAt(0, 0, 0);
        camera.far = FAR;
        camera.near = NEAR;
        camera.direction.nor();
        camera.update();

        Vector3[] window = camera.frustum.planePoints;
        left.set(window[0]).sub(camera.position);
        down.set(window[1]).sub(camera.position);
        tmp.set(window[2]).sub(camera.position);
        left.sub(down).scl(K / WIDTH);
        down.sub(tmp).scl(K / HEIGHT);

    }

    private void setShaderParameters() {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
                Gdx.files.internal("Sh/Sphere/rayMarch.vertex.glsl").readString(),
                Gdx.files.internal("Sh/Sphere/rayMarch.fragment.glsl").readString());
        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());

        u_r = shader.getUniformLocation("u_sphere[0].r");
        u_pos = shader.getUniformLocation("u_sphere[0].pos");
        u_color = shader.getUniformLocation("u_sphere[0].color");
        u_cam_pos = shader.getUniformLocation("u_camPos");
        u_num = shader.getUniformLocation("u_num_sphere");
        u_light_pos = shader.getUniformLocation("u_light.pos");
        u_light_color = shader.getUniformLocation("u_light.color");
    }

    private void cameraUpdate(float time) {
        float sin = (float) Math.sin(time / 3);
        float cos = (float) Math.cos(time / 3);
        camera.position.x = -30f * cos;
        camera.position.z = 30f * sin;
        camera.lookAt(0, 0, 0);
        camera.update();
        camera.direction.nor();
        camera.up.nor();
        down.set(camera.up).scl(-1);
        left.set(camera.up).crs(camera.direction).nor();
    }

    @Override
    public void show() {
        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sphere1.color[0] = Color.BROWN.r;
        sphere1.color[1] = Color.BROWN.g;
        sphere1.color[2] = Color.BROWN.b;
        sphere1.color[3] = Color.BROWN.a;
        sphere1.pos[2] += 10;
        //sphere.pos[2]-=4;

        setCameraParameters();
        setShaderParameters();

        mesh = new Mesh(true, WIDTH * HEIGHT, 0,
                VertexAttribute.Position(),
                VertexAttribute.Normal());
    }

    @Override
    public void render(float delta) {
        time += delta;
        //cameraUpdate(time);
        //sphere.r=8+(float)Math.sin(time);
        sphere.pos[2] = 8 * (float) Math.cos(time);

        System.out.println(1 / delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        setMesh();

        shader.begin();

        setUniformSphere(sphere);
        setUniformSphere(sphere1);

        shader.setUniformi(u_num, id);
        camPos[0] = camera.position.x;
        camPos[1] = camera.position.y;
        camPos[2] = camera.position.z;
        shader.setUniform3fv(u_cam_pos, camPos, 0, 3);
        shader.setUniform3fv(u_light_pos, light.pos, 0, 3);
        shader.setUniform4fv(u_light_color, light.color, 0, 4);

        mesh.render(shader, GL20.GL_POINTS);

        shader.end();
        id = 0;
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
        shader.dispose();
    }
}
