package com.mygdx.projects.voxel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Vector3;

public class VoxelWorld2 extends VoxelWorld {
    public VoxelWorld2(TextureRegion[] tiles, int chunksX, int chunksY, int chunksZ, Vector3 cameraPos, Frustum frustum) {
        super(tiles, chunksX, chunksY, chunksZ, cameraPos, frustum);
    }

    //    private Vector3 cameraPos;
//
//    private OcTree tree;
//    private final Vector3 tmpMin = new Vector3();
//    private final Vector3 tmpMax = new Vector3();
//    private final boolean[] far;
//    private final Frustum frustum;
//
//    public VoxelWorld2 (TextureRegion[] tiles, int chunksX, int chunksY, int chunksZ,Vector3 cameraPos,Frustum frustum) {
//        this.cameraPos = cameraPos;
//        VoxelChunk.pos = cameraPos;
//        this.frustum = frustum;
//        tree = new OcTree(new BoundingBox(tmpMin.set(0,0,0),tmpMax.set(chunksX,chunksY,chunksZ).scl(16)));
//        for (int i = 0; i < chunksX; i++) {
//            for (int j = 0; j < chunksY; j++) {
//                for (int k = 0; k < chunksZ; k++) {
//                    int finalI = i;
//                    int finalJ = j;
//                    int finalK = k;
//                    tree.insert(new GameObj() {
//                        final BoundingBox box =  new BoundingBox(tmpMin.set(finalI, finalJ, finalK).scl(16),tmpMax.set(finalI, finalJ, finalK).scl(16).add(16));;
//                        @Override
//                        public TypeBoundingVolume getTypeBoundingVolume() {
//                            return null;
//                        }
//
//                        @Override
//                        public BoundingBox getBoundingBox() {
//                            return box;
//                        }
//
//                        @Override
//                        public Sphere getSphere() {
//                            return null;
//                        }
//                    });
//                }
//            }
//        }
//        tree.update();
//        far = new boolean[chunksX*chunksZ*chunksY];
//    }
//
//    public byte fastGet(int x,int y,int z){
//        int chunkX = x >>4;
//        int chunkY = y >>4;
//        int chunkZ = z >>4;
//        return chunks[chunkX + chunkZ * chunksX + chunkY * chunksX * chunksZ].getFast(x&15, y&15, z&15);
//    }
//
//    public void setCameraPos(Vector3 cameraPos) {
//        this.cameraPos = cameraPos;
//        VoxelChunk.pos = cameraPos;
//    }
//
//    @Override
//    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
////        renderedChunks = 0;
////        for (int i = 0; i < chunks.length; i++) {
////            if (dirty[i]) {
////                VoxelChunk chunk = chunks[i];
////                Mesh mesh = meshes[i];
////                int numVerts = chunk.calculateVertices(vertices);
////                numVertices[i] = numVerts / 4 * 6;
////                mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
////                dirty[i] = false;
////            }
////            addChunk(i,renderables,pool);
////        }
//        Array<GameObj> boxes = tree.intersection(frustum);
//        for (GameObj o: boxes) {
//            BoundingBox b = o.getBoundingBox();
//            final float dst = b.getCenter(tmpMin).dst2(cameraPos);
//            int x = (int) b.min.x / 16;
//            int y = (int) b.min.y / 16;
//            int z = (int) b.min.z / 16;
//            int numChunk = x + z * chunksX + y * chunksX * chunksZ;
//            if (dst<250000) {
//                if (far[numChunk])
//                    dirty[numChunk] = true;
//                far[numChunk] = false;
//            }else if (!far[numChunk]){
//                far[numChunk] = true;
//                dirty[numChunk] = true;
//            }
//            if (dirty[numChunk]) {
//                VoxelChunk chunk = chunks[numChunk];
//                Mesh mesh = meshes[numChunk];
//                int numVerts;
//                if (!far[numChunk])
//                    numVerts= chunk.calculateVertices(vertices);
//                else
//                    numVerts = chunk.calculateVertices(vertices,getHighest(x,z));
//                numVertices[numChunk] = numVerts / 4 * 6;
//                mesh.setVertices(vertices, 0, numVerts * VoxelChunk.VERTEX_SIZE);
//                dirty[numChunk] = false;
//            }
//            addChunk(numChunk, renderables, pool);
//        }
//    }
}
