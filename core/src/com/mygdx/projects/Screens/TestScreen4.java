package com.mygdx.projects.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TestScreen4 extends InputAdapter implements Screen {
    private static String TAG = "HelloTriangleRenderer";

    private final float[] mVerticesData =
            {
                    -0.5f, -0.5f, -0.5f,//  0.0f, 0.0f,
                    0.5f, -0.5f, -0.5f, // 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, // 1.0f, 1.0f,
                    0.5f, 0.5f, -0.5f, // 1.0f, 1.0f,
                    -0.5f, 0.5f, -0.5f, // 0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, // 0.0f, 0.0f,

                    -0.5f, -0.5f, 0.5f,//  0.0f, 0.0f,
                    0.5f, -0.5f, 0.5f, // 1.0f, 0.0f,
                    0.5f, 0.5f, 0.5f, // 1.0f, 1.0f,
                    0.5f, 0.5f, 0.5f,  //1.0f, 1.0f,
                    -0.5f, 0.5f, 0.5f,  //0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f,  //0.0f, 0.0f,
//
                    -0.5f, 0.5f, 0.5f, // 1.0f, 0.0f,
                    -0.5f, 0.5f, -0.5f, // 1.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, // 0.0f, 1.0f,
                    -0.5f, -0.5f, -0.5f, // 0.0f, 1.0f,
                    -0.5f, -0.5f, 0.5f, // 0.0f, 0.0f,
                    -0.5f, 0.5f, 0.5f, // 1.0f, 0.0f,

                    0.5f, 0.5f, 0.5f, // 1.0f, 0.0f,
                    0.5f, 0.5f, -0.5f, // 1.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, // 0.0f, 1.0f,
                    0.5f, -0.5f, -0.5f, // 0.0f, 1.0f,
                    0.5f, -0.5f, 0.5f, // 0.0f, 0.0f,
                    0.5f, 0.5f, 0.5f // 1.0f, 0.0f,
//
//                    -0.5f, -0.5f, -0.5f, // 0.0f, 1.0f,
//                    0.5f, -0.5f, -0.5f, // 1.0f, 1.0f,
//                    0.5f, -0.5f,  0.5f, // 1.0f, 0.0f,
//                    0.5f, -0.5f,  0.5f, // 1.0f, 0.0f,
//                    -0.5f, -0.5f,  0.5f, // 0.0f, 0.0f,
//                    -0.5f, -0.5f, -0.5f,  //0.0f, 1.0f,
//
//                    -0.5f,  0.5f, -0.5f, // 0.0f, 1.0f,
//                    0.5f,  0.5f, -0.5f,  //1.0f, 1.0f,
//                    0.5f,  0.5f,  0.5f,  //1.0f, 0.0f,
//                    0.5f,  0.5f,  0.5f,  //1.0f, 0.0f,
//                    -0.5f,  0.5f,  0.5f, // 0.0f, 0.0f,
//                    -0.5f,  0.5f, -0.5f  //0.0f, 1.0f
            };
    short[] mIndicesArray = new short[]{
            0, 1, 2,  // face 1
            2, 1, 3,
            2, 3, 4,  // face 2
            4, 3, 5,
            4, 5, 6,  // face 3
            6, 5, 7,
            6, 7, 0,  // face 4
            0, 7, 1,
            1, 7, 3,  // face 5
            3, 7, 5,
            6, 0, 4,  // face 6
            4, 0, 2
    };
    private float[] mColorData = {
            0, 0, 1, 1,
            1, 0, 1, 1,
            0, 1, 1, 1,
            1, 0.1f, 0.1f, 1,
            0, 1, 0, 1,
            0, 0, 0, 1,
            1, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 1,
            0, 0, 1, 1,
            1, 0, 1, 1,
            0, 1, 1, 1,
            1, 0.1f, 0.1f, 1,
            0, 1, 0, 1,
            0, 0, 0, 1,
            1, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 1,
            0, 0, 1, 1,
            1, 0, 1, 1,
            0, 1, 1, 1,
            1, 0.1f, 0.1f, 1,
            0, 1, 0, 1,
            0, 0, 0, 1,
            1, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 1,
            0, 0, 1, 1,
            1, 0, 1, 1,
            0, 1, 1, 1,
            1, 0.1f, 0.1f, 1,
            0, 1, 0, 1,
            0, 0, 0, 1,
            1, 0, 0, 1,
            0.5f, 0.5f, 0.5f, 1,
            0, 0, 1, 1,
            1, 0, 1, 1,
            0, 1, 1, 1,
            1, 0.1f, 0.1f, 1

    };
    private FloatBuffer mProjectionBuffer;
    private FloatBuffer mViewBuffer;
    private Matrix4 mWorldMatrix;
    private FloatBuffer mWorldBuffer;
    private ByteBuffer mVertices;
    private ByteBuffer mIndices;
    private int mProjectionLoc;
    //    private Matrix4 mView;
    private int mWorldLoc;
    private int mViewLoc;
    //  private Matrix4 mProjection;
    private float mTime;
    private ByteBuffer mColors;
    private Vector2 v;

    private ShaderProgram shader;
    private PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

    @Override
    public void show() {
        shader = new ShaderProgram(
                Gdx.files.internal("tests/my.vertex.glsl").readString(),
                Gdx.files.internal("tests/my.fragment.glsl").readString());

        if (!shader.isCompiled())
            throw new GdxRuntimeException(shader.getLog());


        GL20 gl = Gdx.gl20;
//        mView = new Matrix4();
//        mView.setToLookAt(new Vector3(0, 3, 5), new Vector3(0, 0, 0), new Vector3(0, 1, 0));
//        mProjection = new Matrix4();
//        mProjection.setToProjection(1, 5000, 30f, Gdx.graphics.getWidth() / Gdx.graphics.getHeight());

        camera.position.set(0, 3, 5);
        camera.lookAt(0, 0, 0);
        camera.update();

        //set ByteBuffer for vertices
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4).order(ByteOrder.nativeOrder());
        mVertices.asFloatBuffer().put(mVerticesData).position(0);

        //set ByteBuffer for colors
        mColors = ByteBuffer.allocateDirect(mColorData.length * 4).order(ByteOrder.nativeOrder());
        mColors.asFloatBuffer().put(mColorData).position(0);

        //set ByteBuffer for indices
        mIndices = ByteBuffer.allocateDirect(mIndicesArray.length * 4).order(ByteOrder.nativeOrder());
        mIndices.asShortBuffer().put(mIndicesArray).position(0);

        //set ByteBuffer for proj matrix
        mProjectionBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mProjectionBuffer.put(camera.projection.val).position(0);
        //set ByteBuffer for view
        mViewBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mViewBuffer.put(camera.view.val).position(0);
        // world matrix
        mWorldBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mWorldMatrix = new Matrix4();

        //__________________________________________________________________________________________
        IntBuffer linked = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder()).asIntBuffer();

        gl.glBindAttribLocation(shader.getHandle(), 0, "position");
        gl.glBindAttribLocation(shader.getHandle(), 1, "color");
        // Link the program
        gl.glLinkProgram(shader.getHandle());
        // Check the link status
        gl.glGetProgramiv(shader.getHandle(), gl.GL_LINK_STATUS, linked);

        if (linked.get(0) == 0) {
            System.out.println("link failed");
            Gdx.app.log(TAG, "Error linking program:");
            Gdx.app.log(TAG, gl.glGetProgramInfoLog(shader.getHandle()));
            gl.glDeleteProgram(shader.getHandle());
            return;
        }

        System.out.println("position location " + gl.glGetAttribLocation(shader.getHandle(), "position"));
        System.out.println("color location " + gl.glGetAttribLocation(shader.getHandle(), "color"));
        mWorldLoc = gl.glGetUniformLocation(shader.getHandle(), "world");
        mViewLoc = gl.glGetUniformLocation(shader.getHandle(), "view");
        mProjectionLoc = gl.glGetUniformLocation(shader.getHandle(), "projection");
    }


    @Override
    public void render(float v) {
        mTime += v;
        GL20 gl = Gdx.gl20;
        gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Clear the color buffer
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Use the program object
        gl.glUseProgram(shader.getHandle());
        // Load the vertex data
        gl.glVertexAttribPointer(1, 4, GL20.GL_FLOAT, false, 0, mColors);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(0, 3, GL20.GL_FLOAT, false, 0, mVertices);
        gl.glEnableVertexAttribArray(0);
        gl.glUniformMatrix4fv(mProjectionLoc, 1, false, mProjectionBuffer);
        gl.glUniformMatrix4fv(mViewLoc, 1, false, mViewBuffer);
        mWorldMatrix.idt();
        mWorldMatrix.rotate(new Quaternion(new Vector3(0, 1, 0), 10 * mTime));
        mWorldBuffer.put(mWorldMatrix.getValues()).position(0);
        gl.glUniformMatrix4fv(mWorldLoc, 1, false, mWorldBuffer);
        //gl.glDrawElements(GL20.GL_TRIANGLES, mIndicesArray.length, GL20.GL_UNSIGNED_SHORT, mIndices);
        gl.glDrawArrays(GL20.GL_TRIANGLES, 0, mVerticesData.length);
    }

    @Override
    public void resize(int i, int i2) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void pause() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void resume() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void dispose() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

}
