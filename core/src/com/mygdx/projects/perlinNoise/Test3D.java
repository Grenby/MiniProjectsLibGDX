package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

public class Test3D implements Screen {

    Renderable renderable = new Renderable();
    Mesh mesh = new Mesh(
            true,
            8,
            36,
            VertexAttribute.Position(),
            VertexAttribute.Normal()
    );
    Material material = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.RED));
    Environment lights = new Environment();
    PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    FirstPersonCameraController controller = new FirstPersonCameraController(camera);
    ModelBatch batch = new ModelBatch();

    ShapeRenderer renderer = new ShapeRenderer();

    int iId = 0;
    int vertex = 0;
    int vId = 0;
    float[] vertices = new float[8 * 6];
    short[] indices = new short[6 * 6];
    //      x y z
    Vector3 p000 = new Vector3(0, 0, 0);
    Vector3 p100 = new Vector3(1, 0, 0);
    Vector3 p110 = new Vector3(1, 1, 0);
    Vector3 p010 = new Vector3(0, 1, 0);
    Vector3 p001 = new Vector3(0, 0, 1);
    Vector3 p101 = new Vector3(1, 0, 1);
    Vector3 p111 = new Vector3(1, 1, 1);
    Vector3 p011 = new Vector3(0, 1, 1);

    Vector3 n000 = new Vector3(-1, -1, -1).nor();
    Vector3 n100 = new Vector3(1, -1, -1).nor();
    Vector3 n110 = new Vector3(1, 1, -1).nor();
    Vector3 n010 = new Vector3(-1, 1, -1).nor();
    Vector3 n001 = new Vector3(-1, -1, 1).nor();
    Vector3 n101 = new Vector3(1, -1, 1).nor();
    Vector3 n111 = new Vector3(1, 1, 1).nor();
    Vector3 n011 = new Vector3(-1, 1, 1).nor();


    void addVertex(Vector3 p, Vector3 n) {
        vertices[vId++] = p.x;
        vertices[vId++] = p.y;
        vertices[vId++] = p.z;

        vertices[vId++] = n.x;
        vertices[vId++] = n.y;
        vertices[vId++] = n.z;
        vertex++;
    }


    void addTriangle(Vector3 v1, Vector3 v2, Vector3 v3, Vector3 nor) {
        addVertex(v1, nor);
        addVertex(v2, nor);
        addVertex(v3, nor);
        indices[iId++] = (short) (vertex - 3);
        indices[iId++] = (short) (vertex - 2);
        indices[iId++] = (short) (vertex - 1);
    }

    void createBox() {
        addVertex(p000, n000);//8
        addVertex(p100, n100);//7
        addVertex(p110, n110);//6
        addVertex(p010, n010);//5

        addVertex(p001, n001);//4
        addVertex(p101, n101);//3
        addVertex(p111, n111);//2
        addVertex(p011, n011);//1
        //dowm
        {
            indices[iId++] = (short) (vertex - 8);
            indices[iId++] = (short) (vertex - 6);
            indices[iId++] = (short) (vertex - 7);
            indices[iId++] = (short) (vertex - 8);
            indices[iId++] = (short) (vertex - 5);
            indices[iId++] = (short) (vertex - 6);
        }
        //up
        {
            indices[iId++] = (short) (vertex - 4);
            indices[iId++] = (short) (vertex - 3);
            indices[iId++] = (short) (vertex - 2);
            indices[iId++] = (short) (vertex - 4);
            indices[iId++] = (short) (vertex - 2);
            indices[iId++] = (short) (vertex - 1);
        }
        //X0Z
        {
            indices[iId++] = (short) (vertex - 8);
            indices[iId++] = (short) (vertex - 7);
            indices[iId++] = (short) (vertex - 3);
            indices[iId++] = (short) (vertex - 8);
            indices[iId++] = (short) (vertex - 3);
            indices[iId++] = (short) (vertex - 4);
        }
        //X1Z
        {
            indices[iId++] = (short) (vertex - 5);
            indices[iId++] = (short) (vertex - 2);
            indices[iId++] = (short) (vertex - 6);
            indices[iId++] = (short) (vertex - 5);
            indices[iId++] = (short) (vertex - 1);
            indices[iId++] = (short) (vertex - 2);
        }
        //0YZ
        {
            indices[iId++] = (short) (vertex - 8);
            indices[iId++] = (short) (vertex - 1);
            indices[iId++] = (short) (vertex - 5);
            indices[iId++] = (short) (vertex - 8);
            indices[iId++] = (short) (vertex - 4);
            indices[iId++] = (short) (vertex - 1);
        }
        //1YZ
        {
            indices[iId++] = (short) (vertex - 7);
            indices[iId++] = (short) (vertex - 6);
            indices[iId++] = (short) (vertex - 2);
            indices[iId++] = (short) (vertex - 7);
            indices[iId++] = (short) (vertex - 2);
            indices[iId++] = (short) (vertex - 3);
        }

    }


    DirectionalLight light;

    @Override
    public void show() {

        light = new DirectionalLight().set(0.8f, 0.8f, 0.8f, new Vector3(0, 0, -1));

        lights.set(new ColorAttribute(ColorAttribute.Specular, 0.4f, 0.4f, 0.4f, 1.f));
        lights.add(light);
        Gdx.input.setInputProcessor(controller);

//        addTriangle(p001,p101,p111,new Vector3(0,0,1));
//        addTriangle(p001,p111,p011,new Vector3(0,0,1));

        createBox();
        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        renderable.meshPart.mesh = mesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = mesh.getNumIndices();
        renderable.material = material;
        renderable.environment = lights;


        camera.up.set(0, 0, 1);
        camera.direction.set(-1, 0, 0).nor();
        camera.position.set(2, 0, 0);
        camera.update();
        //System.out.println(mesh.getVertices(vertices));
    }

    Vector3 tmp = new Vector3();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        controller.update(delta);
        light.setDirection(camera.direction);

        batch.begin(camera);
        batch.render(renderable);
        batch.end();

        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(Color.GREEN);
        renderer.line(0, 0, 0, 0, 10, 0);
//
//        renderer.setColor(Color.WHITE);
//        renderer.line(p000,tmp.set(p000).add(n000));
//        renderer.line(p100,tmp.set(p100).add(n100));
//        renderer.line(p110,tmp.set(p110).add(n110));
//        renderer.line(p010,tmp.set(p010).add(n010));
//
//        renderer.line(p001,tmp.set(p001).add(n001));
//        renderer.line(p101,tmp.set(p101).add(n101));
//        renderer.line(p111,tmp.set(p111).add(n111));
//        renderer.line(p011,tmp.set(p011).add(n011));

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
