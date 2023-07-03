package com.mygdx.projects.rayMarching.dimention3.Sh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.math.Vector3;

public class MyCam {

    public int
            width = Gdx.graphics.getWidth(),
            height = Gdx.graphics.getHeight();
    public float
            angle = 67,
            l = (float) (height / 2 / Math.tan(Math.toRadians(angle / 2))),
            near = 0.01f,
            far = 1000,
            K = l / near;

    public float[] pos = new float[]{0, 0, 0};

    public Vector3
            rightScr = new Vector3(0, 0, 1f),
            upScr = new Vector3(0, 1f, 0),
            dir = new Vector3(l, 0, 0),
            tmp = new Vector3(),
            tmp1 = new Vector3();

    private float[] vertex = new float[width * height * 6];


    private boolean ready = false;
    private boolean dirUpdate = false;

    private void cameraUpdate() {
        if (dirUpdate) {
            dir.nor();
            upScr.crs(rightScr).crs(dir).nor();
            rightScr.set(dir).crs(upScr).nor();
            dir.scl(l);
            dirUpdate = false;
        }
    }

    public void lookAt(Vector3 v) {
        lookAt(v.x, v.y, v.z);
    }

    public void lookAt(float x, float y, float z) {
        tmp1.set(x - pos[0], y - pos[1], z - pos[2]);
        if (!tmp1.isZero(0.00001f) && !tmp1.isCollinear(dir)) {
            ready = true;
            dirUpdate = true;
            dir.set(x - pos[0], y - pos[1], z - pos[2]);
        }
    }

    void rotate(float x, float y, float z, float angle) {

    }

    public void setDir(float x, float y, float z) {
        lookAt(x + pos[0], y + pos[1], z + pos[2]);
    }

    public void setPos(Vector3 v) {
        setPos(v.x, v.y, v.z);
    }

    public void setPos(float x, float y, float z) {
        ready = true;
        pos[0] = x;
        pos[1] = y;
        pos[2] = z;
    }

    public void setMesh(Mesh mesh) {
        final float W = width / 2f;
        final float H = height / 2f;
        tmp.set(dir);
        tmp.sub(rightScr.x * W, rightScr.y * W, rightScr.z * W).sub(upScr.x * H, upScr.y * H, upScr.z * H);
        int id_vertex = 0;
        for (float x = 0; x < width; x++) {
            for (float y = 0; y < height; y++) {
                vertex[id_vertex++] = x / W - 1f;             //x in display
                vertex[id_vertex++] = y / H - 1f;    //y in display
                vertex[id_vertex++] = 0;                  //z in display

                vertex[id_vertex++] = tmp.x;              //x direction ray
                vertex[id_vertex++] = tmp.y;              //y direction ray
                vertex[id_vertex++] = tmp.z;              //z direction ray

                tmp.add(upScr);
            }
            tmp.sub(upScr.x * height, upScr.y * height, upScr.z * height);
            tmp.add(rightScr);
        }
        mesh.setVertices(vertex);
    }

    public void update() {
        if (ready)
            cameraUpdate();
        ready = false;
    }

}
