package com.mygdx.projects.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.mygdx.projects.utils.MyInput;

public class TestScreen5 extends InputAdapter implements Screen {

    enum Flag {
        normal, lightPoint
    }

    private static final StringBuilder builder = new StringBuilder();

    private ShaderProgram shader;

    private final PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    private final FirstPersonCameraController controller = new FirstPersonCameraController(camera);

    private final Matrix4 ID4 = new Matrix4().idt();
    private final Matrix3 ID3 = new Matrix3().idt();
    private final Matrix3 normalMatrix = new Matrix3();
    private int u_projViewTrans;
    private int u_viewTrans;
    private int u_worldTrans;
    private int u_normalMatrix;
    private int u_mat;
    private int u_light;

    private final Matrix4 tmp = new Matrix4();

    static String getPrefix(final DefaultShader.Config config, Flag... flags) {
        for (Flag flag : flags) {
            if (flag == Flag.lightPoint) {
                if (config != null)
                    builder.append("#define ").append(flag).append("Flag ").append(config.numPointLights).append('\n');
            } else {
                builder.append("#define ").append(flag).append("Flag\n");
            }

        }
        return builder.toString();
    }

    static String getVertex() {
        return builder.append(Gdx.files.internal("Sh/tests/t1.vertex.glsl").readString()).toString();
    }

    interface Setter {
        int set(final ShaderProgram shader, int loc);
    }

    static final float[] tmpF = new float[3];

    private static float[] setVector(Vector3 v) {
        TestScreen5.tmpF[0] = v.x;
        TestScreen5.tmpF[1] = v.y;
        TestScreen5.tmpF[2] = v.z;
        return TestScreen5.tmpF;
    }

    static class LightPoint implements Setter {
        final Vector3 pos = new Vector3();
        final Vector3 ambient = new Vector3();
        final Vector3 diffuse = new Vector3();
        final Vector3 specular = new Vector3();
        final Vector3 fading = new Vector3();

        @Override
        public int set(ShaderProgram shader, int loc) {
            shader.setUniform3fv(loc++, setVector(pos), 0, 3);
            shader.setUniform3fv(loc++, setVector(ambient), 0, 3);
            shader.setUniform3fv(loc++, setVector(diffuse), 0, 3);
            shader.setUniform3fv(loc++, setVector(specular), 0, 3);
            shader.setUniform3fv(loc++, setVector(fading), 0, 3);
            return loc;
        }
    }

    static class MaterialCreator {
        static Material bronze() {
            return new Material(
                    ColorAttribute.createAmbient(0.2125f, 0.1275f, 0.054f, 1),
                    ColorAttribute.createDiffuse(0.714f, 0.4284f, 0.18144f, 1),
                    ColorAttribute.createSpecular(0.393548f, 0.271906f, 0.166721f, 1),
                    FloatAttribute.createShininess(0.3f));
        }
    }

    LightPoint[] lightPoints = new LightPoint[1];
    Array<ModelInstance> modelInstances = new Array<>();
    Array<Model> models = new Array<>();
    Environment environment = new Environment();
    MyInput myInput = new MyInput();
    ModelBatch batch;
    DirectionalShadowLight directionalShadowLight;
    ModelBatch shadow = new ModelBatch(new DepthShaderProvider());
    PerspectiveCamera shadowCamera = new PerspectiveCamera(67, 1024, 1024);

    @Override
    public void show() {
        shadowCamera.position.set(0, 4, 2);
        shadowCamera.lookAt(0, 0, 0);
        shadowCamera.update();

        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.0f));
        //environment.add(new DirectionalLight().set(Color.WHITE,0,-2,2));
        //environment.add(new PointLight().setPosition(-2,2,-2).setColor(Color.WHITE).setIntensity(100));

        directionalShadowLight = new DirectionalShadowLight(1024, 1024, 60f, 60f, .1f, 50f);
        directionalShadowLight.set(Color.WHITE, 0, -2, 1);

        environment.add(directionalShadowLight);
        environment.shadowMap = directionalShadowLight;

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(
                0.5f, 0.5f, 0.5f, GL20.GL_TRIANGLES,
                MaterialCreator.bronze(),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        models.add(model);
        for (int i = 0; i < 5; i++) {
            modelInstances.add(new ModelInstance(model, i, 0, 0));
        }

        model = modelBuilder.createRect(
                -1, 0, -1,
                -1, 0, 1,
                1, 0, 1,
                1, 0, -1,
                0, 1, 0,
                MaterialCreator.bronze(),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
        );
        modelInstances.add(new ModelInstance(model, 0, -1, 0));

        Gdx.input.setInputProcessor(new InputMultiplexer(myInput, controller));
        myInput.addCallback(Input.Keys.ESCAPE, () -> Gdx.app.exit());

        camera.position.set(0, 0, -2);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 100;
        camera.update();

//        config = new DefaultShader.Config();
//        config.numPointLights = 1;
//
//        String prefix = getPrefix(config,Flag.normal,Flag.lightPoint);
//        shader = new ShaderProgram(
//                 Gdx.files.internal("Sh/tests/t1.vertex.glsl").readString(),
//                 Gdx.files.internal("Sh/tests/t1.fragment.glsl").readString());
//        if (!shader.isCompiled())
//            throw new GdxRuntimeException(shader.getLog());
//
//        //Gdx.gl20.glDepthMask();
//        //shader.getFragmentShaderSource()
//        u_projViewTrans = shader.getUniformLocation("u_projViewTrans");
//        u_worldTrans = shader.getUniformLocation("u_worldTrans");
//        u_normalMatrix = shader.getUniformLocation("u_normalMatrix");
//        u_mat = shader.getUniformLocation("u_mat.ambient");
//        u_light = shader.getUniformLocation("u_lightPoints[0].pos");
//        u_viewTrans = shader.getUniformLocation("u_viewTrans");
//        lightPoints[0]=new LightPoint();
//        lightPoints[0].pos.set(0,2,-2);
//        lightPoints[0].ambient.set(1,1,1);
//        lightPoints[0].diffuse.set(1,1,1);
//        lightPoints[0].specular.set(1,1,1);
//        lightPoints[0].fading.set(1,0.09f,0.032f);
//
//
//        float [] v= new float[24 * 6];
//        short [] indices =  new short[model.meshes.get(0).getNumIndices()];
//        model.meshes.get(0).getIndices(indices);
//        v = model.meshes.get(0).getVertices(v);
//        Vector3 p1 = new Vector3();
//        Vector3 p2 = new Vector3();
//        Vector3 p3 = new Vector3();
//        Vector3 n = new Vector3();
//
//        for (int i=0;i<indices.length;i+=3) {
//            int id = indices[i]*6;
//            p1.set(v[id],v[id+1],v[id+2]);
//            n.set(v[id+3],v[id+4],v[id+5]);
//            System.out.println("p1"+ p1 + " nor: " +n);
//            id = indices[i+1]*6;
//            p2.set(v[id],v[id+1],v[id+2]);
//            n.set(v[id+3],v[id+4],v[id+5]);
//            System.out.println("p2"+ p2 + " nor: " +n);
//            id = indices[i+2]*6;
//            p3.set(v[id],v[id+1],v[id+2]);
//            n.set(v[id+3],v[id+4],v[id+5]);
//            System.out.println("p3"+ p3 + " nor: " +n);
//            System.out.println("nor: "+p2.sub(p1).crs(p3.sub(p1)).nor());
//        }
//        for (int i = 0; i < v.length; i++) {
//            System.out.println("pos: "+ new Vector3(v[i++],v[i++],v[i++]) + " nor: " +new Vector3(v[i++],v[i++],v[i]));
//        }
////        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
////        Gdx.gl.glCullFace(GL20.GL_BACK);
//        //Gdx.gl.glFrontFace(GL20.GL_CCW);
//        //Gdx.gl.glDepthMask(true);
//        //enable blending, for alpha
////        Gdx.gl.glEnable(GL20.GL_BLEND);
////        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        //        //shader = new DefaultShader(renderable);
//        DefaultShader.Config config = new DefaultShader.Config();
//        config.numPointLights = 1;
//        config.numDirectionalLights = 1;
//        config.defaultCullFace = GL20.GL_FRONT;
//

        batch = new ModelBatch();

    }

    @Override
    public void render(float delta) {
        controller.update(delta);
        directionalShadowLight.begin(shadowCamera);
        shadow.begin(shadowCamera);
        shadow.render(modelInstances);
        shadow.end();
        directionalShadowLight.end();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin(camera);
        batch.render(modelInstances, environment);

        batch.end();

//        shader.bind();
//
//        shader.setUniformMatrix(u_projViewTrans,camera.combined);
//        shader.setUniformMatrix(u_viewTrans,camera.view);
//        for (LightPoint p:lightPoints) {
//            p.set(shader,u_light);
//        }
//        for (ModelInstance instance:modelInstances) {
//            shader.setUniformMatrix(u_worldTrans, instance.transform);
//            tmp.set(instance.transform).mulLeft(camera.view);
//            normalMatrix.set(instance.transform).inv().transpose();
//            shader.setUniformMatrix(u_normalMatrix,normalMatrix);
//            for (Material m:instance.materials) {
//                Color a = ((ColorAttribute)m.get(ColorAttribute.Ambient)).color;
//                Color d = ((ColorAttribute)m.get(ColorAttribute.Diffuse)).color;
//                Color s = ((ColorAttribute)m.get(ColorAttribute.Specular)).color;
//                float sh = ((FloatAttribute)m.get(FloatAttribute.Shininess)).value;
//                shader.setUniformf(u_mat,a.r,a.g,a.b);
//                shader.setUniformf(u_mat+1,d.r,d.g,d.b);
//                shader.setUniformf(u_mat+2,s.r,s.g,s.b);
//                shader.setUniformf(u_mat+3,sh);
//            }
//            int id = u_light;
//            for (LightPoint point : lightPoints) {
//                id = point.set(shader, id);
//            }
//            for (Mesh m:instance.model.meshes) {
//                m.render(shader,GL20.GL_TRIANGLES);
//            }
//        }

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
        for (Model m : models) {
            m.dispose();
        }
        batch.dispose();
    }

}
