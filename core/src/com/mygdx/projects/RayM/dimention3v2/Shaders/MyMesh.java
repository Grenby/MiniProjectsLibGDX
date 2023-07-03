package com.mygdx.projects.RayM.dimention3v2.Shaders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;

public class MyMesh {
    /**
     * Position attribute - (x, y)
     */
    private final int POSITION_COMPONENTS = 2;
    /**
     * Color attribute - (r, g, b, a)
     */
    private final int COLOR_COMPONENTS = 4;
    /**
     * Total number of components for all attributes
     */
    private final int NUM_COMPONENTS = POSITION_COMPONENTS + COLOR_COMPONENTS;
    /**
     * The "size" (total number of floats) for a single triangle
     */
    private int PRIMITIVE_SIZE;
    /**
     * The maximum number of triangles (square) our mesh will hold
     */
    private int maxTris;
    /**
     * The maximum number of vertices our mesh will hold
     */
    private int maxVertex;
    /*
    The array which holds all the data, interleaved like so:
        x, y, r, g, b, a
        x, y, r, g, b, a,
        x, y, r, g, b, a,
        ... etc ...
    */

    private float[] verts;
    //The current index that we are pushing triangles into the array
    private int idx = 0;
    //
    Mesh mesh;

    public MyMesh(int num_) {
        verts = new float[maxVertex * NUM_COMPONENTS];
    }

    public void create() {
        mesh = new Mesh(true, maxVertex, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, POSITION_COMPONENTS, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.ColorPacked, COLOR_COMPONENTS, "a_color"));
    }

    void addVertex(float x, float y, Color color) {
        if (idx == maxVertex) return;
        verts[idx++] = x;
        verts[idx++] = y;
        verts[idx++] = color.r;
        verts[idx++] = color.g;
        verts[idx++] = color.b;
        verts[idx++] = color.a;
        verts[idx++] = x;
    }


}
