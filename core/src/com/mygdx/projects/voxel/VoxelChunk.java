/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mygdx.projects.voxel;

import com.badlogic.gdx.math.Vector3;

public class VoxelChunk extends Chunk {

    public static Vector3 pos = null;

    public VoxelChunk(int width, int height, int depth) {
        super(width, height, depth);
    }

    public static int createTop(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createBottom(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createLeft(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createRight(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public static int createFront(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        return vertexOffset;
    }

    public static int createBack(Vector3 offset, int x, int y, int z, float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + x;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y + 1;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + x + 1;
        vertices[vertexOffset++] = offset.y + y;
        vertices[vertexOffset++] = offset.z + z + 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        return vertexOffset;
    }

    private boolean visible(float x, float y, float z, float nx, float ny, float nz) {
        return true;
    }

    /**
     * Creates a mesh out of the chunk, returning the number of indices produced
     *
     * @return the number of vertices produced
     */
    public int calculateVertices(float[] vertices) {
        int i = 0;
        int vertexOffset = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++, i++) {
                    byte voxel = voxels[i];
                    float dx = x + offset.x - pos.x;
                    float dy = y + offset.y - pos.y;
                    float dz = z + offset.z - pos.z;
                    if (voxel == 0) continue;
                    if (visible(dx + 0.5f, dy + 1, dz + 0.5f, 0, 1, 0))
                        if (y < height - 1) {
                            if (voxels[i + topOffset] == 0)
                                vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset);
                        } else {
                            vertexOffset = createTop(offset, x, y, z, vertices, vertexOffset);
                        }
                    if (visible(dx + 0.5f, dy, dz + 0.5f, 0, -1, 0))
                        if (y > 0) {
                            if (voxels[i + bottomOffset] == 0)
                                vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset);
                        } else {
                            vertexOffset = createBottom(offset, x, y, z, vertices, vertexOffset);
                        }
                    if (visible(dx, dy + 0.5f, dz + 0.5f, -1, 0, 0))
                        if (x > 0) {
                            if (voxels[i + leftOffset] == 0)
                                vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset);
                        } else {
                            vertexOffset = createLeft(offset, x, y, z, vertices, vertexOffset);
                        }
                    if (visible(dx + 1, dy + 0.5f, dz + 0.5f, 1, 0, 0))
                        if (x < width - 1) {
                            if (voxels[i + rightOffset] == 0)
                                vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset);
                        } else {
                            vertexOffset = createRight(offset, x, y, z, vertices, vertexOffset);
                        }
                    if (visible(dx + 0.5f, dy + 0.5f, dz + 1, 0, 0, 1))
                        if (z > 0) {
                            if (voxels[i + frontOffset] == 0)
                                vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset);
                        } else {
                            vertexOffset = createFront(offset, x, y, z, vertices, vertexOffset);
                        }
                    if (visible(dx + 0.5f, dy + 0.5f, dz, 0, 0, -1))
                        if (z < depth - 1) {
                            if (voxels[i + backOffset] == 0)
                                vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset);
                        } else {
                            vertexOffset = createBack(offset, x, y, z, vertices, vertexOffset);
                        }
                }
            }
        }
        return vertexOffset / VERTEX_SIZE;
    }

    public int calculateVertices(float[] vertices, float maxY) {
        int vertexOffset = 0;
        final float dx = offset.x - pos.x + 8;
        final float dy = offset.y - pos.y + maxY / 2f;
        final float dz = offset.z - pos.z + 8;
        if (visible(dx, dy, dz, 0, 1, 0))
            vertexOffset = createChunkTop(vertices, vertexOffset, maxY);
        if (visible(dx, dy, dz, 0, -1, 0))
            vertexOffset = createChunkBottom(vertices, vertexOffset);
        if (visible(dx, dy, dz, 1, 0, 0))
            vertexOffset = createChunkRight(vertices, vertexOffset, maxY);
        if (visible(dx, dy, dz, -1, 0, 0))
            vertexOffset = createChunkLeft(vertices, vertexOffset, maxY);
        if (visible(dx, dy, dz, 0, 0, 1))
            vertexOffset = createChunkFront(vertices, vertexOffset, maxY);
        if (visible(dx, dy, dz, 0, 0, -1))
            vertexOffset = createChunkBack(vertices, vertexOffset, maxY);

        return vertexOffset / VERTEX_SIZE;
    }

    public int createChunkTop(float[] vertices, int vertexOffset, float maxY) {
        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public int createChunkBottom(float[] vertices, int vertexOffset) {
        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public int createChunkLeft(float[] vertices, int vertexOffset, float maxY) {
        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = -1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public int createChunkRight(float[] vertices, int vertexOffset, float maxY) {
        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 1;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        return vertexOffset;
    }

    public int createChunkFront(float[] vertices, int vertexOffset, float maxY) {
        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 1;
        return vertexOffset;
    }

    public int createChunkBack(float[] vertices, int vertexOffset, float maxY) {
        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y + maxY;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;

        vertices[vertexOffset++] = offset.x + 16;
        vertices[vertexOffset++] = offset.y;
        vertices[vertexOffset++] = offset.z + 16;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = 0;
        vertices[vertexOffset++] = -1;
        return vertexOffset;
    }

}
