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

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.mygdx.projects.utils.GameObj;
import com.mygdx.projects.utils.OcTree;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VoxelWorld implements RenderableProvider {
    public static final int CHUNK_SIZE_X = 16;
    public static final int CHUNK_SIZE_Y = 16;
    public static final int CHUNK_SIZE_Z = 16;

    public final VoxelChunk[] chunks;
    public final Mesh[] meshes;
    public final Material[] materials;
    public final boolean[] dirty;
    public final int[] numVertices;
    public float[] vertices;
    public final int chunksX;
    public final int chunksY;
    public final int chunksZ;
    public final int voxelsX;
    public final int voxelsY;
    public final int voxelsZ;
    public int renderedChunks;
    public int numChunks;
    private final TextureRegion[] tiles;
    private Vector3 cameraPos;
    private final Worker[] workers;


    private final OcTree tree;
    private final Vector3 tmpMin = new Vector3();
    private final Vector3 tmpMax = new Vector3();
    private final boolean[] far;
    private final Frustum frustum;

    private Array<Renderable> renderables;
    private Pool<Renderable> pool;


    public VoxelWorld(TextureRegion[] tiles, int chunksX, int chunksY, int chunksZ, Vector3 cameraPos, Frustum frustum) {
        this.tiles = tiles;
        this.chunks = new VoxelChunk[chunksX * chunksY * chunksZ];
        this.chunksX = chunksX;
        this.chunksY = chunksY;
        this.chunksZ = chunksZ;
        this.numChunks = chunksX * chunksY * chunksZ;
        this.voxelsX = chunksX * CHUNK_SIZE_X;
        this.voxelsY = chunksY * CHUNK_SIZE_Y;
        this.voxelsZ = chunksZ * CHUNK_SIZE_Z;
        this.tree = new OcTree(new BoundingBox(tmpMin.set(0, 0, 0), tmpMax.set(voxelsX, voxelsY, voxelsZ)));
        this.cameraPos = cameraPos;
        this.frustum = frustum;
        VoxelChunk.pos = cameraPos;

        int i = 0;

        for (int y = 0; y < chunksY; y++) {
            for (int z = 0; z < chunksZ; z++) {
                for (int x = 0; x < chunksX; x++) {
                    VoxelChunk chunk = new VoxelChunk(CHUNK_SIZE_X, CHUNK_SIZE_Y, CHUNK_SIZE_Z);
                    chunk.offset.set(x * CHUNK_SIZE_X, y * CHUNK_SIZE_Y, z * CHUNK_SIZE_Z);
                    chunks[i++] = chunk;
                    int finalI = x;
                    int finalJ = y;
                    int finalK = z;
                    tree.insert(new GameObj() {
                        final BoundingBox box = new BoundingBox(tmpMin.set(finalI, finalJ, finalK).scl(16), tmpMax.set(finalI, finalJ, finalK).scl(16).add(16));
                        ;

                        @Override
                        public TypeBoundingVolume getTypeBoundingVolume() {
                            return null;
                        }

                        @Override
                        public BoundingBox getBoundingBox() {
                            return box;
                        }

                        @Override
                        public Sphere getSphere() {
                            return null;
                        }
                    });
                }
            }
        }
        tree.update();

        int len = CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 6 / 3;
        short[] indices = new short[len];
        short j = 0;
        for (i = 0; i < len; i += 6, j += 4) {
            indices[i + 0] = (short) (j + 0);
            indices[i + 1] = (short) (j + 1);
            indices[i + 2] = (short) (j + 2);
            indices[i + 3] = (short) (j + 2);
            indices[i + 4] = (short) (j + 3);
            indices[i + 5] = (short) (j + 0);
        }
        this.meshes = new Mesh[chunksX * chunksY * chunksZ];
        for (i = 0; i < meshes.length; i++) {
            meshes[i] = new Mesh(true, CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z * 6 * 4, CHUNK_SIZE_X * CHUNK_SIZE_Y
                    * CHUNK_SIZE_Z * 36 / 3, VertexAttribute.Position(), VertexAttribute.Normal());
            meshes[i].setIndices(indices);
        }

        this.dirty = new boolean[chunksX * chunksY * chunksZ];
        this.far = new boolean[chunksX * chunksZ * chunksY];
        for (i = 0; i < dirty.length; i++) {
            far[i] = false;
            dirty[i] = true;
        }

        this.numVertices = new int[chunksX * chunksY * chunksZ];
        for (i = 0; i < numVertices.length; i++)
            numVertices[i] = 0;
        this.vertices = new float[VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z];
        this.materials = new Material[chunksX * chunksY * chunksZ];
        for (i = 0; i < materials.length; i++) {
            materials[i] = new Material(new ColorAttribute(ColorAttribute.Diffuse, MathUtils.random(0.5f, 1f), MathUtils.random(
                    0.5f, 1f), MathUtils.random(0.5f, 1f), 1));
        }

        this.workers = new Worker[4];
        int size = VoxelChunk.VERTEX_SIZE * 6 * CHUNK_SIZE_X * CHUNK_SIZE_Y * CHUNK_SIZE_Z;
        for (i = 0; i < 4; i++) {
            workers[i] = new Worker(size, chunksX, chunksZ * chunksX, cameraPos, this);
        }
    }

    public byte fastGet(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkY = y >> 4;
        int chunkZ = z >> 4;
        return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].getFast(x & 15, y & 15, z & 15);
    }

    public void setCameraPos(Vector3 cameraPos) {
        this.cameraPos = cameraPos;
        VoxelChunk.pos = cameraPos;
    }

    public void set(float x, float y, float z, byte voxel) {
        int ix = (int) x;
        int iy = (int) y;
        int iz = (int) z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return;
        int id = chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ;
        if (chunks[id].set(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz % CHUNK_SIZE_Z,
                voxel)) {
            dirty[id] = true;
        }

    }

    public byte get(float x, float y, float z) {
        int ix = (int) x;
        int iy = (int) y;
        int iz = (int) z;
        int chunkX = ix / CHUNK_SIZE_X;
        if (chunkX < 0 || chunkX >= chunksX) return 0;
        int chunkY = iy / CHUNK_SIZE_Y;
        if (chunkY < 0 || chunkY >= chunksY) return 0;
        int chunkZ = iz / CHUNK_SIZE_Z;
        if (chunkZ < 0 || chunkZ >= chunksZ) return 0;
        return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].get(ix % CHUNK_SIZE_X, iy % CHUNK_SIZE_Y, iz
                % CHUNK_SIZE_Z);
    }

    public float getHighest(float x, float z) {
        int ix = (int) x;
        int iz = (int) z;
        if (ix < 0 || ix >= voxelsX) return 0;
        if (iz < 0 || iz >= voxelsZ) return 0;
        // FIXME optimize
        for (int y = voxelsY - 1; y > 0; y--) {
            if (get(ix, y, iz) > 0) return y + 1;
        }
        return 0;
    }

    public void setColumn(float x, float y, float z, byte voxel) {
        int ix = (int) x;
        int iy = (int) y;
        int iz = (int) z;
        if (ix < 0 || ix >= voxelsX) return;
        if (iy < 0 || iy >= voxelsY) return;
        if (iz < 0 || iz >= voxelsZ) return;
        // FIXME optimize
        for (; iy > 0; iy--) {
            set(ix, iy, iz, voxel);
        }
    }

    public void setCube(float x, float y, float z, float width, float height, float depth, byte voxel) {
        int ix = (int) x;
        int iy = (int) y;
        int iz = (int) z;
        int iwidth = (int) width;
        int iheight = (int) height;
        int idepth = (int) depth;
        int startX = Math.max(ix, 0);
        int endX = Math.min(voxelsX, ix + iwidth);
        int startY = Math.max(iy, 0);
        int endY = Math.min(voxelsY, iy + iheight);
        int startZ = Math.max(iz, 0);
        int endZ = Math.min(voxelsZ, iz + idepth);
        // FIXME optimize
        for (iy = startY; iy < endY; iy++) {
            for (iz = startZ; iz < endZ; iz++) {
                for (ix = startX; ix < endX; ix++) {
                    set(ix, iy, iz, voxel);
                }
            }
        }
    }

    protected int addChunk(int numChunk) {
        final Array<Renderable> renderables = this.renderables;
        final Pool<Renderable> pool = this.pool;
        if (numVertices[numChunk] == 0) return 0;
        Mesh mesh = meshes[numChunk];
        Renderable renderable = pool.obtain();
        renderable.material = materials[numChunk];
        renderable.meshPart.mesh = mesh;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = numVertices[numChunk];
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderables.add(renderable);
        return 1;
    }


    private void work(final BoundingBox box) {
        int x = (int) box.min.x / 16;
        int y = (int) box.min.y / 16;
        int z = (int) box.min.z / 16;
        int numChunk = x + z * chunksX + y * chunksX * chunksZ;
        if (dirty[numChunk]) {
            VoxelChunk chunk = chunks[numChunk];
            Mesh mesh = meshes[numChunk];
            int numVerts;
            if (!far[numChunk])
                numVerts = chunk.calculateVertices(vertices);
            else
                numVerts = chunk.calculateVertices(vertices, getHighest(x, z));
            numVertices[numChunk] = numVerts / 4 * 6;
            mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
            dirty[numChunk] = false;
        }
    }

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    Array<Future<Integer>> array = new Array<>(4);

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        renderedChunks = 0;
        this.renderables = renderables;
        this.pool = pool;
        Array<GameObj> boxes = tree.intersection(frustum);
//		Array<Future<Integer>> array = this.array;
//		array.clear();
//		int size = boxes.size / 4;
//		for (int i =0;i<4;i++){
//			if (i!=3 && size !=0) {
//				workers[i].set(size*i,size*(i+1),boxes);
//				array.add(executor.submit(workers[i],0));
//			}else if (i==3){
//				workers[i].set(size*i,boxes.size - 3 * size,boxes);
//				array.add(executor.submit(workers[i],0));
//			}
//		}
//		int last = 0;
//		while (array.size>0){
//			int s = runnables.size;
//			for (int i = last; i <s ; i++) {
//				runnables.get(i).run();
//			}
//			last = s;
//			for (int i=0;i<array.size;i++){
//				if (array.get(i).isDone()){
//					array.removeIndex(i);
//				}
//			}
//			try {
//				Thread.sleep(0l);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		for (int i = last; i <runnables.size ; i++) {
//			runnables.get(i).run();
//		}
//		runnables.clear();
//		for (int i=0;i<array.size;i++){
//			try {
//				System.out.println(array.get(i).isDone());
//				System.out.println(array.get(i).isCancelled());
//
//				array.get(i).get();
//				array.removeIndex(i);
//			} catch (InterruptedException | ExecutionException e) {
//				e.printStackTrace();
//			}
//		}

        for (GameObj o : boxes) {
            BoundingBox b = o.getBoundingBox();
            work(b);
            int x = (int) b.min.x / 16;
            int y = (int) b.min.y / 16;
            int z = (int) b.min.z / 16;
            renderedChunks += addChunk(x + z * chunksX + y * chunksX * chunksZ);
        }
    }

    private Array<Runnable> runnables = new Array<>();

    void add(Runnable runnable) {
        synchronized (runnables) {
            runnables.add(runnable);
        }
    }

    GridPoint3 res = new GridPoint3();

    public GridPoint3 getNear(Ray ray) {
        Array<GameObj> objs = tree.intersection(ray);
        float min = Float.MAX_VALUE;
        GameObj obj = null;
        for (GameObj o : objs) {
            o.getBoundingBox().getCenter(tmpMin);
            if (cameraPos.dst2(tmpMin) < min) {
                obj = o;
                min = cameraPos.dst2(tmpMin);
            }
        }
        if (obj == null) {
            return null;
        }
        BoundingBox b = obj.getBoundingBox();
        int x = (int) b.min.x / 16;
        int y = (int) b.min.y / 16;
        int z = (int) b.min.z / 16;
        return res.set(x, y, z);
    }


    private static class Worker implements Runnable {
        private final Vector3 cameraPos;
        private final Vector3 tmpMin = new Vector3();
        private final float[] vertices;
        private final int chunkX, chunkXZ;
        private final VoxelWorld v;

        private Array<GameObj> objs;
        private int from, to;
        private Mesh m;

        Worker(int size, int chunkX, int chunkXZ, Vector3 cameraPos, VoxelWorld voxelWorld) {
            this.vertices = new float[size];
            this.chunkX = chunkX;
            this.chunkXZ = chunkXZ;
            this.cameraPos = cameraPos;
            this.v = voxelWorld;
        }

        private void work(final BoundingBox box) {
            final float dst = box.getCenter(tmpMin).dst2(cameraPos);
            int x = (int) box.min.x >> 4;
            int y = (int) box.min.y >> 4;
            int z = (int) box.min.z >> 4;
            int numChunk = x + z * chunkX + y * chunkXZ;
            if (dst < 250000) {
                if (v.far[numChunk])
                    v.dirty[numChunk] = true;
                v.far[numChunk] = false;
            } else {
                if (!v.far[numChunk]) {
                    v.far[numChunk] = true;
                    v.dirty[numChunk] = true;
                }
            }
            if (v.dirty[numChunk]) {
                VoxelChunk chunk = v.chunks[numChunk];
                Mesh mesh = v.meshes[numChunk];
                int numVerts;
                if (!v.far[numChunk])
                    numVerts = chunk.calculateVertices(vertices);
                else
                    numVerts = chunk.calculateVertices(vertices, v.getHighest(x, z));
                v.numVertices[numChunk] = numVerts / 4 * 6;
                v.add(() -> {
                    mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
                });
                m = mesh;
                v.dirty[numChunk] = false;
            }
        }

        synchronized public void set(int from, int to, Array<GameObj> objs) {
            this.from = from;
            this.to = to;
            this.objs = objs;
        }

        @Override
        public void run() {
            for (int j = from; j < to; j++) {
                work(objs.get(j).getBoundingBox());
//				if (wasUpdate) {

//					wasUpdate = false;
//				}
            }
        }
    }

}
