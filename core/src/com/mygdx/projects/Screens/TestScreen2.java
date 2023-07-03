package com.mygdx.projects.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class TestScreen2 implements Screen {

    private static String TAG = "HelloTriangleRenderer";

    private final float[] mVerticesData =
            {-0.5f, -0.5f, 0.5f,  // vertex 0
                    0.5f, -0.5f, 0.5f,  // vertex 1
                    -0.5f, 0.5f, 0.5f,  // vertex 2
                    0.5f, 0.5f, 0.5f,  // vertex 3
                    -0.5f, 0.5f, -0.5f,  // vertex 4
                    0.5f, 0.5f, -0.5f,  // vertex 5
                    -0.5f, -0.5f, -0.5f,  // vertex 6
                    0.5f, -0.5f, -0.5f   // vertex 7
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
            0.5f, 0.5f, 0.5f, 1

    };
    private FloatBuffer mProjectionBuffer;
    private FloatBuffer mViewBuffer;
    private Matrix4 mWorldMatrix;
    private FloatBuffer mWorldBuffer;
    private int mProgramObject;
    private ByteBuffer mVertices;
    private ByteBuffer mIndices;
    private int mProjectionLoc;
    private Matrix4 mView;
    private int mWorldLoc;
    private int mViewLoc;
    private Matrix4 mProjection;
    private float mTime;
    private ByteBuffer mColors;
    private Vector2 v;

    @Override
    public void show() {
        GL20 gl = Gdx.gl20;
        mView = new Matrix4();
        mView.setToLookAt(new Vector3(0, 3, 5), new Vector3(0, 0, 0), new Vector3(0, 1, 0));
        mProjection = new Matrix4();
        mProjection.setToProjection(1, 5000, 30f, Gdx.graphics.getWidth() / Gdx.graphics.getHeight());

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
        mProjectionBuffer.put(mProjection.getValues()).position(0);
        //set ByteBuffer for view
        mViewBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mViewBuffer.put(mView.getValues()).position(0);
        // world matrix
        mWorldBuffer = ByteBuffer.allocateDirect(16 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mWorldMatrix = new Matrix4();

        String vShaderStr =
                "  // World View Projection matrix that will transform the input vertices\n" +
                        "  // to screen space.\n" +
                        "  attribute vec4 position;\n" +
                        " attribute vec4 color; \n" +
                        "\n" +
                        "  uniform mat4 world;\n" +
                        "  uniform mat4 view;\n" +
                        "  uniform mat4 projection;\n" +
                        "  varying vec4 v_color;\n" +
                        "\n" +
                        "  /**\n" +
                        "   * The vertex shader simply transforms the input vertices to screen space.\n" +
                        "   */\n" +
                        "  void main() {\n" +
                        "    // Multiply the vertex positions by the worldViewProjection matrix to\n" +
                        "    // transform them to screen space.\n" +
                        "    v_color.x = color.x/2; \n" +
                        "    v_color.y = color.y/2; \n" +
                        "    v_color.z = color.z/2; \n" +
                        "    v_color.w =1; \n" +
                        "    gl_Position = projection * view * world * position;\n" +
                        "  }";

        String fShaderStr =
                "/**\n" +
                        "   * This pixel shader just returns the color red.\n" +
                        "   */\n" +
                        " varying vec4 v_color;\n" +
                        "  void main() {\n" +
                        "    gl_FragColor =v_color;  // Red.\n" +
                        "  }";
        System.out.println(fShaderStr);
        System.out.println(vShaderStr);

        int vertexShader;
        int fragmentShader;
        int programObject;
        //__________________________________________________________________________________________
        IntBuffer linked = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder()).asIntBuffer();
        vertexShader = loadShader(GL20.GL_VERTEX_SHADER, vShaderStr);
        fragmentShader = loadShader(GL20.GL_FRAGMENT_SHADER, fShaderStr);
        programObject = gl.glCreateProgram();
        if (programObject == 0) {
            System.out.println("program failed");
            Gdx.app.log(TAG, "Error creating programObject");
        }
        gl.glAttachShader(programObject, vertexShader);
        gl.glAttachShader(programObject, fragmentShader);

        gl.glBindAttribLocation(programObject, 0, "position");
        gl.glBindAttribLocation(programObject, 1, "color");
        // Link the program
        gl.glLinkProgram(programObject);
        checkGlError("linking");
        // Check the link status
        gl.glGetProgramiv(programObject, gl.GL_LINK_STATUS, linked);

        if (linked.get(0) == 0) {
            System.out.println("link failed");
            Gdx.app.log(TAG, "Error linking program:");
            Gdx.app.log(TAG, gl.glGetProgramInfoLog(programObject));
            gl.glDeleteProgram(programObject);
            return;
        }
        mProgramObject = programObject;

        System.out.println("position location" + gl.glGetAttribLocation(programObject, "position"));
        System.out.println("color location" + gl.glGetAttribLocation(programObject, "color"));
        mWorldLoc = gl.glGetUniformLocation(programObject, "world");
        mViewLoc = gl.glGetUniformLocation(programObject, "view");
        mProjectionLoc = gl.glGetUniformLocation(programObject, "projection");

        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        IntBuffer i = ByteBuffer.allocateDirect(4).asIntBuffer();
        gl.glGetProgramiv(mProgramObject, GL20.GL_ACTIVE_ATTRIBUTES, i);
        System.out.println("-->" + i.get(0));
    }


    @Override
    public void render(float v) {
        mTime += v;
        GL20 gl = Gdx.gl20;
        gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        // Clear the color buffer
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Use the program object
        gl.glUseProgram(mProgramObject);
        // Load the vertex data
        gl.glVertexAttribPointer(1, 4, GL20.GL_FLOAT, false, 0, mColors);
        checkGlError("set colors");
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(0, 3, GL20.GL_FLOAT, false, 0, mVertices);
        checkGlError("set position");
        gl.glEnableVertexAttribArray(0);
        checkGlError("set position");
        gl.glUniformMatrix4fv(mProjectionLoc, 1, false, mProjectionBuffer);
        gl.glUniformMatrix4fv(mViewLoc, 1, false, mViewBuffer);
        mWorldMatrix.idt();
        mWorldMatrix.rotate(new Quaternion(new Vector3(0, 1, 0), 10 * mTime));
        mWorldBuffer.put(mWorldMatrix.getValues()).position(0);
        gl.glUniformMatrix4fv(mWorldLoc, 1, false, mWorldBuffer);
        checkGlError("uniformsetting");
        gl.glDrawElements(GL20.GL_TRIANGLES, mIndicesArray.length, GL20.GL_UNSIGNED_SHORT, mIndices);
        checkGlError("draw");
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

    ///
    // Create a shader object, load the shader source, and
    // compile the shader.
    //
    private int loadShader(int type, String shaderSrc) {
        GL20 gl = Gdx.graphics.getGL20();
        int shader;
        IntBuffer compiled = ByteBuffer.allocateDirect(8).order(ByteOrder.nativeOrder()).asIntBuffer();

        // Create the shader object
        shader = gl.glCreateShader(type);

        if (shader == 0) {
            return 0;
        }

        // Load the shader source
        gl.glShaderSource(shader, shaderSrc);

        // Compile the shader
        gl.glCompileShader(shader);

        // Check the compile status
        gl.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, compiled);

        if (compiled.get(0) == 0) {
            System.out.println("shader compilation failed");
            Gdx.app.log(TAG, gl.glGetShaderInfoLog(shader));
            gl.glDeleteShader(shader);
            return 0;
        }
        return shader;
    }

    private void checkGlError(String op) {
        GL20 gl = Gdx.gl20;
        int error;
        while ((error = gl.glGetError()) != gl.GL_NO_ERROR) {
            Gdx.app.log("opengl", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }
}
