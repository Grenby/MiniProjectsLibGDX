package com.mygdx.projects.RayM.dimention3.Shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.mygdx.projects.RayM.dimention3.Sh.MyCam;

public class TestLight1 implements Screen, InputProcessor {

    private float[] move = new float[]{0, 0, 0};


    private class Sphere {
        float[] color = new float[]{Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a};
        float[] pos = new float[]{0, 0, 0};
        float r = 8;
    }

    private class Light {
        float[] color = new float[]{Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, 1};
        float[] pos = new float[]{-20, 10, 5};
    }

    private int u_r;
    private int u_pos;
    private int u_color;
    private int u_cam_pos;
    private int u_num;
    private int u_light_pos;
    private int u_light_color;
    private int id = 0;

    private float time = 0;

    private ShaderProgram shader;

    private MyCam cam = new MyCam();
    private float r;

    {
        final float r1 = (cam.width / 2f) * (cam.width / 2f) + (cam.height / 2f) * (cam.height / 2f);
        r = (float) Math.sqrt(r1 + cam.l * cam.l);
    }

    private Mesh mesh;
    private Sphere sphere = new Sphere();
    private Sphere sphere1 = new Sphere();

    private Light light = new Light();

    private Vector3 start = new Vector3(-1, -1, 0),
            end = new Vector3(-1, -1, 0),
            tmp = new Vector3();
    private int roundX = 0, roundY = 0;

    private void setUniformSphere(Sphere sphere) {
        shader.setUniformf(u_r + id, sphere.r);
        shader.setUniform3fv(u_pos + id, sphere.pos, 0, 3);
        shader.setUniform4fv(u_color + id, sphere.color, 0, 4);
        id++;
    }

    private void setShaderParameters() {
        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(
                Gdx.files.internal("Sh/LightTest/light.vertex.glsl").readString(),
                Gdx.files.internal("Sh/LightTest/light.fragment.glsl").readString());

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

    private void cameraUpdate(float delta) {
        float k = 0;
        if (move[0] != 0) k++;
        if (move[1] != 0) k++;
        if (move[2] != 0) k++;
        if (k > 0) {
            System.out.println(move[2]);
            k = 1 / (float) Math.sqrt(k);
            for (int i = 0; i < 3; i++) cam.pos[i] += move[i] * k;
        }
        //cam.lookAt(10,0,0);
        /*
        if (end.x!=-1){

            start.sub(end);
            start.y=-start.y;
            float angle=3*2*(float)Math.atan2(start.len(),cam.dir.len());
            start.crs(cam.dir);
            cam.dir.rotate(start,angle);
            start.set(-1,-1,-1);
            end.set(-1,-1,-1);
        }
        */
        cam.update();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
        //no need for depth...
        Gdx.gl.glDepthMask(false);

        //enable blending, for alpha
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sphere1.color[0] = Color.BROWN.r;
        sphere1.color[1] = Color.BROWN.g;
        sphere1.color[2] = Color.BROWN.b;
        sphere1.color[3] = Color.BROWN.a;
        sphere1.pos[0] += 30;
        sphere1.pos[2] += 20;
        sphere1.r = 10;
        sphere.pos[0] = +30;
        cam.setPos(0, 0, 0);

        setShaderParameters();

        mesh = new Mesh(true, cam.width * cam.height, 0,
                VertexAttribute.Position(),
                VertexAttribute.Normal());
    }

    @Override
    public void render(float delta) {
        time += delta;
        cameraUpdate(time);
        //sphere.r=8+(float)Math.sin(time);
        //sphere.pos[2]=8*(float)Math.cos(time);

        //System.out.println(1/delta);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        cam.setMesh(mesh);

        shader.begin();

        setUniformSphere(sphere);
        setUniformSphere(sphere1);
        shader.setUniformi(u_num, id);
        shader.setUniform3fv(u_cam_pos, cam.pos, 0, 3);
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

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case com.badlogic.gdx.Input.Keys.W:
                move[0] += 1;
                break;
            case com.badlogic.gdx.Input.Keys.S:
                move[0] -= 1;
                break;
            case com.badlogic.gdx.Input.Keys.A:
                move[2] -= 1;
                break;
            case com.badlogic.gdx.Input.Keys.D:
                move[2] += 1;
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case com.badlogic.gdx.Input.Keys.W:
                move[0] -= 1;
                break;
            case com.badlogic.gdx.Input.Keys.S:
                move[0] += 1;
                break;
            case com.badlogic.gdx.Input.Keys.A:
                move[2] += 1;
                break;
            case com.badlogic.gdx.Input.Keys.D:
                move[2] -= 1;
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
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
        if (screenX < 5) {
            Gdx.input.setCursorPosition(cam.width - 5, screenY);
            roundX--;
        }
        if (screenX > cam.width - 5) {
            Gdx.input.setCursorPosition(5, screenY);
            roundX++;
        }
        if (screenY < 5) {
            Gdx.input.setCursorPosition(screenX, cam.height - 80);
            roundY--;
        }
        if (screenY > cam.height - 80) {
            Gdx.input.setCursorPosition(screenX, 5);
            roundX++;
        }
        //start.set(screenX+roundX*cam.width,screenY+roundY*cam.height);
        if (start.y == -1) start.set(0, screenY + roundY * cam.height, screenX + roundX * cam.width);
        else end.set(0, screenY + roundY * cam.height, screenX + roundX * cam.width);
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }


}
