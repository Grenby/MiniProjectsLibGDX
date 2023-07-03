package com.mygdx.projects.voxel;

import com.badlogic.gdx.math.Vector3;

public abstract class Chunk {

    public static final int VERTEX_SIZE = 6;
    public final byte[] voxels;
    public final int width;
    public final int height;
    public final int depth;
    public final Vector3 offset = new Vector3();
    protected final int widthTimesHeight;
    protected final int topOffset;
    protected final int bottomOffset;
    protected final int leftOffset;
    protected final int rightOffset;
    protected final int frontOffset;
    protected final int backOffset;

    public Chunk(int width, int height, int depth) {
        this.voxels = new byte[width * height * depth];
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.topOffset = width * depth;
        this.bottomOffset = -width * depth;
        this.leftOffset = -1;
        this.rightOffset = 1;
        this.frontOffset = -width;
        this.backOffset = width;
        this.widthTimesHeight = width * height;
    }

    public byte get(int x, int y, int z) {
        if (x < 0 || x >= width) return 0;
        if (y < 0 || y >= height) return 0;
        if (z < 0 || z >= depth) return 0;
        return getFast(x, y, z);
    }

    public byte getFast(int x, int y, int z) {
        return voxels[x + z * width + y * widthTimesHeight];
    }

    public boolean set(int x, int y, int z, byte voxel) {
        if (x < 0 || x >= width) return false;
        if (y < 0 || y >= height) return false;
        if (z < 0 || z >= depth) return false;
        setFast(x, y, z, voxel);
        return true;
    }

    public boolean setFast(int x, int y, int z, byte voxel) {
        int id = x + z * width + y * widthTimesHeight;
        if (voxels[id] == voxel)
            return false;
        voxels[id] = voxel;
        return true;
    }

    /**
     * Creates a mesh out of the chunk, returning the number of indices produced
     *
     * @return the number of vertices produced
     */
    public abstract int calculateVertices(float[] vertices);

}
