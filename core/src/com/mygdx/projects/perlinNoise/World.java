package com.mygdx.projects.perlinNoise;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;

public class World implements RenderableProvider, Disposable {

    final static int CHUNK_SIZE = 32;
    public final static float CELL_SIZE = 1f;
    public final Mesh[] meshes;
    public final Material material;

    Vector3 nor = new Vector3(), t1 = new Vector3(), t2 = new Vector3();

    int WIDTH = 0;
    int HEIGHT = 0;

    float[][] mapZ;

    float[] vertices;//= new float[CHUNK_SIZE*CHUNK_SIZE*6*6];
    short[] indices;//= new short[CHUNK_SIZE*CHUNK_SIZE*6];

    public World(float[][] map) {
        WIDTH = map.length;
        HEIGHT = map[0].length;
        createMap(map);

        int numMesh = map.length / CHUNK_SIZE * map[0].length / CHUNK_SIZE;
        meshes = new Mesh[numMesh];
        for (int i = 0; i < numMesh; i++) {
            meshes[i] = new Mesh(
                    true,
                    CHUNK_SIZE * CHUNK_SIZE * 4,
                    CHUNK_SIZE * CHUNK_SIZE * 6,
                    VertexAttribute.Position(),
                    VertexAttribute.Normal()
            );
        }

        material = new Material(
                new ColorAttribute(ColorAttribute.Diffuse, 1.0f, 0.5f, 0.31f, 1),
                new ColorAttribute(ColorAttribute.Specular, 0.5f, 0.5f, 0.5f, 1),
                new ColorAttribute(ColorAttribute.Ambient, 1.0f, 0.5f, 0.31f, 1),
                new ColorAttribute(ColorAttribute.Fog, MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1)
        );
        int wChunk = map.length / CHUNK_SIZE;
        int hChunk = map[0].length / CHUNK_SIZE;

        vertices = new float[CHUNK_SIZE * CHUNK_SIZE * 4 * 6];
        indices = new short[CHUNK_SIZE * CHUNK_SIZE * 6];

        for (int numX = 0; numX < wChunk; numX++) {
            for (int numY = 0; numY < hChunk; numY++) {
                addChunk(numX, numY);
            }
        }
    }

    public Matrix4 tr = new Matrix4();

    float getR(float... x) {
        float res = 0;
        for (float f : x) {
            res += f;
        }
        return res / x.length;
    }

    void createMap(float[][] m) {
        mapZ = new float[WIDTH + 1][HEIGHT + 1];
        for (int i = 0; i < WIDTH + 1; i++) {
            for (int j = 0; j < HEIGHT + 1; j++) {
                float z;
                if (i == 0) {
                    if (j == 0) {
                        z = m[i][j];
                    } else if (j == HEIGHT) {
                        z = m[i][j - 1];
                    } else {
                        z = getR(m[i][j - 1], m[i][j]);
                    }
                } else if (i == WIDTH) {
                    if (j == 0) {
                        z = m[i - 1][j];
                    } else if (j == HEIGHT) {
                        z = m[i - 1][j - 1];
                    } else {
                        z = getR(m[i - 1][j - 1], m[i - 1][j]);
                    }
                } else if (j == 0) {
                    z = getR(m[i][j], m[i - 1][j]);
                } else if (j == HEIGHT) {
                    z = getR(m[i][j - 1], m[i - 1][j - 1]);
                } else {
                    z = getR(m[i][j], m[i - 1][j], m[i][j - 1], m[i - 1][j - 1]);
                }
                mapZ[i][j] = z * 100;
            }
        }
    }

    void addChunk(int cX, int cY) {
        int vID = 0;
        int iID = 0;
        int vertexNum = 0;
        for (int x = 0; x < CHUNK_SIZE; x++) {
            for (int y = 0; y < CHUNK_SIZE; y++) {
                int i = x + cX * CHUNK_SIZE;
                int j = y + cY * CHUNK_SIZE;
                float x0 = i * (CELL_SIZE);
                float y0 = j * (CELL_SIZE);

                vertices[vID++] = x0;
                vertices[vID++] = y0;
                vertices[vID++] = mapZ[i][j];
                getNormal(nor, i, j);
                randNormal(nor);
                vertices[vID++] = nor.x;
                vertices[vID++] = nor.y;
                vertices[vID++] = nor.z;
                vertexNum++;

                vertices[vID++] = x0 + CELL_SIZE;
                vertices[vID++] = y0;
                vertices[vID++] = mapZ[i + 1][j];
                getNormal(nor, i + 1, j);
                randNormal(nor);

                vertices[vID++] = nor.x;
                vertices[vID++] = nor.y;
                vertices[vID++] = nor.z;
                vertexNum++;

                vertices[vID++] = x0 + CELL_SIZE;
                vertices[vID++] = y0 + CELL_SIZE;
                vertices[vID++] = mapZ[i + 1][j + 1];
                getNormal(nor, i + 1, j + 1);
                randNormal(nor);
                vertices[vID++] = nor.x;
                vertices[vID++] = nor.y;
                vertices[vID++] = nor.z;
                vertexNum++;


                vertices[vID++] = x0;
                vertices[vID++] = y0 + CELL_SIZE;
                vertices[vID++] = mapZ[i][j + 1];
                getNormal(nor, i, j + 1);
                randNormal(nor);
                vertices[vID++] = nor.x;
                vertices[vID++] = nor.y;
                vertices[vID++] = nor.z;
                vertexNum++;

                indices[iID++] = (short) (vertexNum - 4);
                indices[iID++] = (short) (vertexNum - 3);
                indices[iID++] = (short) (vertexNum - 2);

                indices[iID++] = (short) (vertexNum - 4);
                indices[iID++] = (short) (vertexNum - 2);
                indices[iID++] = (short) (vertexNum - 1);
            }
        }
        int num = cX * HEIGHT / CHUNK_SIZE + cY;
        meshes[num].setVertices(vertices);
        meshes[num].setIndices(indices);

    }

    RandomXS128 random = new RandomXS128();

    void randNormal(Vector3 out) {
//        float r = random.nextFloat()/16;
//        float angle = random.nextFloat() * MathUtils.PI2;
//
//        float y = r * (float) Math.sin(angle);
//        float z = r * (float) Math.cos(angle);
//        float x = (float) Math.sqrt(1 - y*y - z*z);
//
//        t1.set(0,out.z,-out.y).nor();
//        t2.set(t1).crs(out).nor();
//        out.scl(x);
//        out.mulAdd(t1,y).mulAdd(t2,z);
//        out.nor();
    }

    void getNormal(Vector3 out, int x, int y) {
        out.setZero();
        float x0 = x * CELL_SIZE;
        float y0 = y * CELL_SIZE;
        if (x > 0 && x < WIDTH && y > 0 && y < HEIGHT) {
            t1.set(x0 + CELL_SIZE, y0, mapZ[x + 1][y]).sub(x0, y0, mapZ[x][y]);
            t2.set(x0 + CELL_SIZE, y0 + CELL_SIZE, mapZ[x + 1][y + 1]).sub(x0, y0, mapZ[x][y]);
            t1.crs(t2).nor();
            out.add(t1);

            t1.set(x0 + CELL_SIZE, y0 + CELL_SIZE, mapZ[x + 1][y + 1]).sub(x0, y0, mapZ[x][y]);
            t2.set(x0, y0 + CELL_SIZE, mapZ[x][y + 1]).sub(x0, y0, mapZ[x][y]);
            t1.crs(t2).nor();
            out.add(t1);


            t1.set(x0, y0 - CELL_SIZE, mapZ[x][y - 1]).sub(x0, y0, mapZ[x][y]);
            t2.set(x0 + CELL_SIZE, y0, mapZ[x + 1][y]).sub(x0, y0, mapZ[x][y]);
            t1.crs(t2).nor();
            out.add(t1);

            t1.set(x0 - CELL_SIZE, y0, mapZ[x - 1][y]).sub(x0, y0, mapZ[x][y]);
            t2.set(x0 - CELL_SIZE, y0 - CELL_SIZE, mapZ[x - 1][y - 1]).sub(x0, y0, mapZ[x][y]);
            t1.crs(t2).nor();
            out.add(t1);


            t1.set(x0 - CELL_SIZE, y0 - CELL_SIZE, mapZ[x - 1][y - 1]).sub(x0, y0, mapZ[x][y]);
            t2.set(x0, y0 - CELL_SIZE, mapZ[x][y - 1]).sub(x0, y0, mapZ[x][y]);
            t1.crs(t2).nor();
            out.add(t1);

            t1.set(x0, y0 + CELL_SIZE, mapZ[x][y + 1]).sub(x0, y0, mapZ[x][y]);
            t2.set(x0 - CELL_SIZE, y0, mapZ[x - 1][y]).sub(x0, y0, mapZ[x][y]);
            t1.crs(t2).nor();
            out.add(t1);

            out.nor();
            //System.out.println(out);
        } else {
            out.set(1, 0, 0);
        }
    }

    @Override
    public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
        for (Mesh m : meshes) {
            Renderable renderable = pool.obtain();
            renderable.worldTransform.set(tr);
            renderable.meshPart.mesh = m;

            renderable.meshPart.offset = 0;
            renderable.meshPart.size = CHUNK_SIZE * CHUNK_SIZE * 6;
            renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
            renderable.material = material;
            renderables.add(renderable);
        }
    }

    @Override
    public void dispose() {
        for (Mesh m : meshes) {
            m.dispose();
        }
    }
}
