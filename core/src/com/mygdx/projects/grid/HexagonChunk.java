package com.mygdx.projects.grid;

import com.badlogic.gdx.math.Vector3;

public class HexagonChunk {

    public static final int VERTEX_SIZE = 6;
    public final byte[] voxels;
    public final int height;
    public final int r;
    public final int shifrH;
    public final Vector3 offset = new Vector3();

//    protected final int widthTimesHeight;
//    protected final int topOffset;
//    protected final int bottomOffset;
//    protected final int leftOffset;
//    protected final int rightOffset;
//    protected final int frontOffset;
//    protected final int backOffset;


    public HexagonChunk(int r, int height) {
        this.voxels = new byte[(3 * r * r - 3 * r + 1) * height];
        this.height = height;
        this.r = r;
        this.shifrH = 3 * r * r - 3 * r + 1;
    }

    public byte get(int x, int y, int z) {
//        if (x < 0 || x >= width) return 0;
//        if (y < 0 || y >= height) return 0;
//        if (z < 0 || z >= depth) return 0;
        return getFast(x, y, z);
    }

    public byte getFast(int x, int y, int z) {
        return voxels[z * shifrH];
    }

    public boolean set(int x, int y, int z, byte voxel) {
//        if (x < 0 || x >= width) return false;
//        if (y < 0 || y >= height) return false;
//        if (z < 0 || z >= depth) return false;
        setFast(x, y, z, voxel);
        return true;
    }

    public boolean setFast(int x, int y, int z, byte voxel) {
//        int id = x + z * width + y * widthTimesHeight;
//        if (voxels[id] == voxel)
//            return false;
//        voxels[id] = voxel;
//        return true;
        return true;
    }

    public int calculateVertices(float[] vertices) {

        return 0;
    }

}
