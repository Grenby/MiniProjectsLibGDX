package com.mygdx.projects.sphere;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.mygdx.projects.MyScreen;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

public class SphereMode extends MyScreen {

    PerspectiveCamera camera = new PerspectiveCamera(67, W, H);
    FirstPersonCameraController controller = new FirstPersonCameraController(camera);

    ShapeRenderer renderer = new ShapeRenderer();

    Environment lights;
    ModelBatch modelBatch = new ModelBatch();

    TriangleSphere sphere;
    Renderable renderable = new Renderable();
    Renderable renderableTriangle = new Renderable();

    ArrayList<Triangle> points = new ArrayList<>();

    Vector3 center = new Vector3(0, 0, 0);

    ArrayList<NoiseTriangle> initialPoints = new ArrayList<>();


    Mesh mesh;
    float r = 10;

    float[] vertices;
    short[] indices;

    private void update(float delta) {
        controller.update(delta);
    }


    static class Triangle {
        Vector3 v1, v2, v3;

        public Triangle(Vector3 v1, Vector3 v2, Vector3 v3) {
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }
    }

    static class NoiseTriangle extends Triangle {
        Vector2 r1, r2, r3;

        public NoiseTriangle(Vector3 v1, Vector3 v2, Vector3 v3, Vector2 r1, Vector2 r2, Vector2 r3) {
            super(v1, v2, v3);
            this.r1 = r1;
            this.r2 = r2;
            this.r3 = r3;
        }
    }


    Triangle triangle = null;

    int ss = 0;

    @Override
    public void render(float delta) {
        update(delta);
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (ss < -1) {
                step();
                ;
            } else {
                newStep();
            }
            ss++;
            System.out.println(points.size() * 3);
        }


        if (Gdx.input.isButtonJustPressed(0)) {
            triangle = getNear(camera.getPickRay(Gdx.input.getX(0), Gdx.input.getY(0)));
            if (triangle != null) {
                Mesh mesh1 = new Mesh(
                        true,
                        3,
                        3,
                        VertexAttribute.Position(),
                        VertexAttribute.Normal()
                );
                Vector3 nor = getNormal(triangle.v1, triangle.v2, triangle.v3);

                float[] vertices = {
                        triangle.v1.x, triangle.v1.y, triangle.v1.z, nor.x, nor.y, nor.z,
                        triangle.v2.x, triangle.v2.y, triangle.v2.z, nor.x, nor.y, nor.z,
                        triangle.v3.x, triangle.v3.y, triangle.v3.z, nor.x, nor.y, nor.z,
                };
                short[] indices = {0, 1, 2};

                mesh1.setVertices(vertices);
                mesh1.setIndices(indices);

                renderableTriangle.meshPart.mesh = mesh1;
                renderableTriangle.meshPart.primitiveType = GL20.GL_TRIANGLES;
                renderableTriangle.meshPart.offset = 0;
                renderableTriangle.meshPart.size = 3;
            }
        }

        Gdx.gl.glClearColor(0f, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        {
            renderer.setColor(Color.RED);
            renderer.line(0, 0, 0, 10, 0, 0);
            renderer.setColor(Color.GREEN);
            renderer.line(0, 0, 0, 0, 10, 0);

            renderer.setColor(Color.WHITE);
            for (int i = 0; i < 11; i++) {
                //     renderer.line(i,0,i,10);
                //   renderer.line(0,i,10,i);
            }

            for (int i = 0; i < points.size(); i++) {
                // drawTriangle(points.get(i).v1,points.get(i).v2,points.get(i).v3);
            }

        }
        renderer.end();

        modelBatch.begin(camera);
//        for (int i=0;i<1;i++) {
//            //world.tr.rotateRad(0,0,1,i* MathUtils.PI/2);
//           // modelBatch.render(sphere, lights);
//        }

        modelBatch.render(renderable);
        if (triangle != null) {
            modelBatch.render(renderableTriangle);
        }
        modelBatch.end();

        renderer.begin(ShapeRenderer.ShapeType.Line);
        for (Polygon p : polygonHashMap.values()) {
            for (int i = 0; i < p.points.size(); i++) {
                //renderer.line(p.points.get(i),p.points.get(i == p.points.size()-1 ? 0 : i+1 ));
            }
        }
        renderer.end();

        //drawColorTriangle();

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(controller);
        lights = new Environment();
        lights.set(
                new ColorAttribute(ColorAttribute.Specular, 1.0f, 1.0f, 1.0f, 1.f),
                new ColorAttribute(ColorAttribute.Diffuse, 0.5f, 0.5f, 0.5f, 1.f),
                new ColorAttribute(ColorAttribute.Ambient, 0.2f, 0.2f, 0.2f, 1.f)
        );
        lights.add(new DirectionalLight().set(1, 1, 1, 1, 1, -1));

        renderable.material = new Material(
                new ColorAttribute(ColorAttribute.Diffuse, 1.0f, 0.5f, 0.31f, 1),
                new ColorAttribute(ColorAttribute.Specular, 0.5f, 0.5f, 0.5f, 1),
                new ColorAttribute(ColorAttribute.Ambient, 1.0f, 0.5f, 0.31f, 1),
                new ColorAttribute(ColorAttribute.Fog, MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1)
        );
        renderable.environment = lights;

        renderableTriangle.material = new Material(
                new ColorAttribute(ColorAttribute.Diffuse, 1.0f, 0, 0, 1),
                new ColorAttribute(ColorAttribute.Specular, 1, 0, 0, 1),
                new ColorAttribute(ColorAttribute.Ambient, 1, 0, 0, 1),
                new ColorAttribute(ColorAttribute.Fog, MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1)
        );
        renderableTriangle.environment = lights;

        controller.setVelocity(10);

        camera.position.set(-10, -10, 10);
        camera.lookAt(0, 0, 0);
        camera.up.set(0, 0, 1);
        camera.update();


        createDefault3();

        for (Triangle t : points) {
            Vector2 rand1 = new Vector2().setToRandomDirection();
            Vector2 rand2 = new Vector2().setToRandomDirection();
            Vector2 rand3 = new Vector2().setToRandomDirection();

            initialPoints.add(new NoiseTriangle(t.v1, t.v2, t.v3, rand1, rand2, rand3));
        }

        createMesh();
    }

    void createDefault3() {
        final float X = (float) 0.525731112119133606;
        final float Z = (float) 0.850650808352039932;

        final Vector3[] vdata = {
                new Vector3(-X, 0, Z), new Vector3(X, 0, Z), new Vector3(-X, 0, -Z), new Vector3(X, 0, -Z),
                new Vector3(0, Z, X), new Vector3(0, Z, -X), new Vector3(0, -Z, X), new Vector3(0, -Z, -X),
                new Vector3(Z, X, 0), new Vector3(-Z, X, 0), new Vector3(Z, -X, 0), new Vector3(-Z, -X, 0)
        };

        int[][] tindices = {
                {0, 4, 1}, {0, 9, 4}, {9, 5, 4}, {4, 5, 8}, {4, 8, 1},
                {8, 10, 1}, {8, 3, 10}, {5, 3, 8}, {5, 2, 3}, {2, 7, 3},
                {7, 10, 3}, {7, 6, 10}, {7, 11, 6}, {11, 0, 6}, {0, 1, 6},
                {6, 1, 10}, {9, 0, 11}, {9, 11, 2}, {9, 2, 5}, {7, 2, 11}
        };


        for (int i = 0; i < 20; i++) {
            Vector3 v1 = vdata[tindices[i][2]];
            Vector3 v2 = vdata[tindices[i][1]];
            Vector3 v3 = vdata[tindices[i][0]];
            points.add(new Triangle(v1.cpy().scl(r), v2.cpy().scl(r), v3.cpy().scl(r)));
        }

        /*
        FUN
        for(int i = 0; i < 20; i++) {
            Vector3 v1 = vdata[tindices[i][2]].scl(r);
            Vector3 v2 = vdata[tindices[i][1]].scl(r);
            Vector3 v3 = vdata[tindices[i][0]].scl(r);
            points.add(new Triangle(v1,v2,v3));
        }
         */

    }


    void add(ArrayList<Vector3> pp, int i, int j, int k) {
        points.add(new Triangle(pp.get(i), pp.get(j), pp.get(k)));
    }

    void step() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        for (Triangle t : points) {
            Vector3 p12, p13, p23;

            p12 = new Vector3().add(t.v1).add(t.v2).scl(0.5f);
            p13 = new Vector3().add(t.v1).add(t.v3).scl(0.5f);
            p23 = new Vector3().add(t.v2).add(t.v3).scl(0.5f);

            toSphere(p12);
            toSphere(p13);
            toSphere(p23);

            triangles.add(new Triangle(t.v1, p12, p13));
            triangles.add(new Triangle(t.v2, p23, p12));
            triangles.add(new Triangle(t.v3, p13, p23));
            triangles.add(new Triangle(p12, p23, p13));
            //break;
        }

        points = triangles;

        polygonHashMap.clear();
        findAll5();
        createPolygonMesh();
        //createMesh();
    }

    void newStep() {
        ArrayList<Triangle> triangles = new ArrayList<>();
        for (Triangle t : points) {
            Vector3 p121, p122, p131, p132, p231, p232, center;

            p121 = new Vector3().mulAdd(t.v1, 2 / 3f).mulAdd(t.v2, 1 / 3f);
            p122 = new Vector3().mulAdd(t.v1, 1 / 3f).mulAdd(t.v2, 2 / 3f);

            p131 = new Vector3().mulAdd(t.v1, 2 / 3f).mulAdd(t.v3, 1 / 3f);
            p132 = new Vector3().mulAdd(t.v1, 1 / 3f).mulAdd(t.v3, 2 / 3f);

            p231 = new Vector3().mulAdd(t.v2, 2 / 3f).mulAdd(t.v3, 1 / 3f);
            p232 = new Vector3().mulAdd(t.v2, 1 / 3f).mulAdd(t.v3, 2 / 3f);

            center = new Vector3().add(t.v1).add(t.v2).add(t.v3).scl(1 / 3f);

            toSphere(p121);
            toSphere(p122);
            toSphere(p131);
            toSphere(p132);
            toSphere(p231);
            toSphere(p232);
            toSphere(center);


            triangles.add(new Triangle(t.v1, p121, p131));
            triangles.add(new Triangle(t.v2, p231, p122));
            triangles.add(new Triangle(t.v3, p132, p232));

            triangles.add(new Triangle(center, p232, p132));
            triangles.add(new Triangle(center, p132, p131));
            triangles.add(new Triangle(center, p131, p121));
            triangles.add(new Triangle(center, p121, p122));
            triangles.add(new Triangle(center, p122, p231));
            triangles.add(new Triangle(center, p231, p232));

            //break;
        }

        points = triangles;

        polygonHashMap.clear();
        findAll5();
        createPolygonMesh();
        //createMesh();
    }

    void applyNoise() {
        for (Triangle t : points) {
            Vector3 v1 = t.v1;
            Vector3 v2 = t.v2;
            Vector3 v3 = t.v3;

            applyNoise(v1);
            applyNoise(v2);
            applyNoise(v3);

        }
    }

    private float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    float area(float x1, float y1, float x2, float y2, float x3, float y3) {
        return Math.abs((x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1));
    }

    float area(Vector3 v1, Vector3 v2, Vector3 v3) {
        return tmp.set(v2).sub(v1).crs(tmp1.set(v3).sub(v1)).len() / 2;
    }

    void applyNoise(Vector3 v) {
        NoiseTriangle t = (NoiseTriangle) getNear(initialPoints, v);

        float val = area(t.v1, t.v2, t.v3);
        float p12 = area(t.v1, t.v2, v) / val;
        float p23 = area(t.v2, t.v3, v) / val;
        float p31 = area(t.v3, t.v1, v) / val;

        float d1 = t.r1.dot(p12, -p31);
        float d2 = t.r2.dot(-p12, p23);
        float d3 = t.r3.dot(-p23, p31);

        float u1 = qunticCurve(p12);
        float u2 = qunticCurve(p23);
        float u3 = qunticCurve(p31);

        float res = (u1 * d1 + u2 * d2 + u3 * d3) / (u1 + u2 + u3);
        res += 1;
        res /= 8;
        //  System.out.println(res);
        //v.sub(center).setLength(v.len() + res).add(center);
    }

    private float qunticCurve(float t) {
        //return t*t + Math.signum(t) * t -1;
        //return (float)Math.sqrt( t * t * t * (t * (t * 6 - 15) + 10) * ((float) Math.cos(t*Math.PI/2)+1)/2);
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    Vector3 tmp = new Vector3();
    Vector3 tmp1 = new Vector3();
    Vector3 tmp2 = new Vector3();

    void toSphere(Vector3 p) {
        p.sub(center).nor().scl(r).add(center);
    }

    void drawTriangle(Vector3 v1, Vector3 v2, Vector3 v3) {
        renderer.line(v1, v2);
        renderer.line(v2, v3);
        renderer.line(v3, v1);
    }

    void createMesh() {
        int numVertices = points.size() * 3;
        mesh = new Mesh(
                true,
                numVertices,
                numVertices,
                VertexAttribute.Position(),
                VertexAttribute.Normal()
        );
        vertices = new float[numVertices * 6];
        indices = new short[numVertices];
        int vertex = 0;
        int index = 0;
        int point = 0;

        for (Triangle t : points) {
            Vector3 not = getNormal(t.v1, t.v2, t.v3);
            not.set(t.v1).sub(center).nor();
            vertices[vertex++] = t.v1.x;
            vertices[vertex++] = t.v1.y;
            vertices[vertex++] = t.v1.z;
            vertices[vertex++] = not.x;
            vertices[vertex++] = not.y;
            vertices[vertex++] = not.z;
            not.set(t.v2).sub(center).nor();
            vertices[vertex++] = t.v2.x;
            vertices[vertex++] = t.v2.y;
            vertices[vertex++] = t.v2.z;
            vertices[vertex++] = not.x;
            vertices[vertex++] = not.y;
            vertices[vertex++] = not.z;
            not.set(t.v3).sub(center).nor();
            vertices[vertex++] = t.v3.x;
            vertices[vertex++] = t.v3.y;
            vertices[vertex++] = t.v3.z;
            vertices[vertex++] = not.x;
            vertices[vertex++] = not.y;
            vertices[vertex++] = not.z;

            point += 3;

            indices[index++] = (short) (point - 3);
            indices[index++] = (short) (point - 2);
            indices[index++] = (short) (point - 1);
        }

        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        renderable.meshPart.mesh = mesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = numVertices;

    }

    Vector3 getNormal(Vector3 v1, Vector3 v2, Vector3 v3) {
        tmp.set(v2).sub(v1);
        tmp1.set(v3).sub(v1);
        return tmp.crs(tmp1).nor();
    }

    Triangle getNear(Ray ray) {
        if (Intersector.intersectRaySphere(ray, center, r, tmp)) {
            return getNear(points, tmp);
        }
        return null;
    }

    Triangle getNear(ArrayList<? extends Triangle> triangles, Vector3 point) {
        Triangle res = null;
        float d = Float.MAX_VALUE;
        for (Triangle t : triangles) {
            float dist = t.v1.dst2(point) + t.v2.dst2(point) + t.v3.dst2(point);
            if (dist < d) {
                d = dist;
                res = t;
            }
        }
        return res;
    }

    Matrix4 IDT = new Matrix4().idt();
    Matrix4 tr = new Matrix4();
    Vector3 ox = new Vector3();
    Vector3 oy = new Vector3();
    Vector3 oz = new Vector3();

    void drawColorTriangle() {
        if (triangle != null) {
            Vector3 v1 = triangle.v1;
            Vector3 v2 = triangle.v2;
            Vector3 v3 = triangle.v3;
            renderer.begin(ShapeRenderer.ShapeType.Line);
            {
                renderer.setColor(Color.RED);
                renderer.point(v1.x, v1.y, v1.z);
                renderer.point(v2.x, v2.y, v2.z);
                renderer.point(v3.x, v3.y, v3.z);
            }
            renderer.end();
            ox.set(v2).sub(v1).nor();
            oy.set(v3).sub(v1).nor();

            oz.set(ox).crs(oy).nor();

            tr.set(ox, oy, oz, v1);
            tr.inv();
            tr.setToTranslation(v1);
            System.out.println(new Vector3(0, 0, 0).mul(tr) + " " + v1);
            System.out.println(new Vector3(1, 0, 0).mul(tr).sub(v1).nor().scl(v2.dst(v1)).add(v1) + " " + v2);
            System.out.println(new Vector3(0, 1, 0).mul(tr) + " " + v3);

            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(Color.GREEN);
            renderer.line(new Vector3(1, 0, 0).mul(tr), v1);

            renderer.end();

            renderer.setTransformMatrix(tr);

            renderer.begin(ShapeRenderer.ShapeType.Filled);
            {

                renderer.setColor(Color.RED);
                renderer.triangle(0, 0, 1, 0, 0, 1);
            }
            renderer.end();
            renderer.setTransformMatrix(IDT);
        }
    }

    static class Polygon {
        ArrayList<Vector3> points = new ArrayList<>();

        boolean contains(Vector3 point) {
            for (Vector3 p : points) {
                if (p.equals(point)) {
                    return true;
                }
            }
            return false;
        }

        void sort(Vector3 center) {
            points.replaceAll(Vector3::cpy);
            for (Vector3 v : points) {
                v.sub(center);
                // v.scl(0.5f);
            }

            for (int i = 1; i < points.size(); i++) {
                Vector3 ox = new Vector3(points.get(i - 1));
                Vector3 oz = new Vector3(center);
                Vector3 oy = new Vector3(oz).crs(ox);

                for (int j = i; j < points.size(); j++) {
                    float xj = points.get(j).dot(ox);
                    float yj = points.get(j).dot(oy);
                    if (xj > 0 && yj > 0) {
                        Vector3 tmp = points.get(i);
                        points.set(i, points.get(j));
                        points.set(j, tmp);
                        break;
                    }
                }
            }

            for (Vector3 v : points) {
                v.add(center);
            }

        }

    }

    HashMap<Triangle, Polygon> polygonHashMap = new HashMap<>();
    ArrayList<ArrayList<Triangle>> five = new ArrayList<>();
    ArrayList<Triangle> dop = new ArrayList<>();
    Queue<Triangle> q = new ArrayDeque<>();
    ArrayList<Triangle> dop1 = new ArrayList<>();

    void add5(Vector3 v) {
        Polygon polygon = new Polygon();
        for (Triangle tr : dop) {
            if (!tr.v1.equals(v) && !polygon.contains(tr.v1)) {
                polygon.points.add(tr.v1);
            }
            if (!tr.v2.equals(v) && !polygon.contains(tr.v2)) {
                polygon.points.add(tr.v2);
            }
            if (!tr.v3.equals(v) && !polygon.contains(tr.v3)) {
                polygon.points.add(tr.v3);
            }
        }
        polygon.sort(v);
        for (Triangle tr : dop) {
            polygonHashMap.put(tr, polygon);
        }

        for (Triangle tr : dop) {
            for (Triangle t : getNeighbour(tr)) {
                if (!polygonHashMap.containsKey(t)) {
                    q.add(t);
                }
            }
        }

    }

    void findAll5() {
        polygonHashMap.clear();
        int num = 0;
        for (Triangle tr : points) {

            if (!polygonHashMap.containsKey(tr)) {
                if (check(tr.v1).size() == 5) {
                    add5(tr.v1);
                    num++;
                    continue;
                }
                if (check(tr.v2).size() == 5) {
                    add5(tr.v2);
                    num++;
                    continue;
                }
                if (check(tr.v3).size() == 5) {
                    num++;
                    add5(tr.v3);
                }
            }
        }
        System.out.println(num);
        System.out.println(polygonHashMap.size());
        while (!q.isEmpty()) {
            add(q.poll());
        }

//

    }

    void add(Triangle t) {
        if (polygonHashMap.containsKey(t))
            return;
        if (map(t.v1)) {
            return;
        }
        if (map(t.v2)) {
            return;
        }
        map(t.v3);
    }

    ArrayList<Triangle> getNeighbour(Triangle t) {
        dop1.clear();
        for (Triangle r : points) {
            if (r != t && isNeighbour(t, r)) {
                dop1.add(r);
            }
        }
        return dop1;
    }

    boolean isNeighbour(Triangle t1, Triangle t2) {
        if (t1.v1.epsilonEquals(t2.v1) || t1.v1.epsilonEquals(t2.v2) || t1.v1.epsilonEquals(t2.v3)) {
            if (t1.v2.epsilonEquals(t2.v1) || t1.v2.epsilonEquals(t2.v2) || t1.v2.epsilonEquals(t2.v3)) {
                return true;
            }
            if (t1.v3.epsilonEquals(t2.v1) || t1.v3.epsilonEquals(t2.v2) || t1.v3.epsilonEquals(t2.v3)) {
                return true;
            }
            return false;
        }
        if (t1.v2.epsilonEquals(t2.v1) || t1.v2.epsilonEquals(t2.v2) || t1.v2.epsilonEquals(t2.v3)) {
            if (t1.v3.epsilonEquals(t2.v1) || t1.v3.epsilonEquals(t2.v2) || t1.v3.epsilonEquals(t2.v3)) {
                return true;
            }
            return false;
        }

        return false;
    }

    boolean map(Vector3 vertex) {
        for (Triangle tr : check(vertex)) {
            if (polygonHashMap.containsKey(tr)) {
                return false;
            }
        }
        Polygon polygon = new Polygon();
        for (Triangle tr : dop) {
            if (!tr.v1.equals(vertex) && !polygon.contains(tr.v1)) {
                polygon.points.add(tr.v1);
            }
            if (!tr.v2.equals(vertex) && !polygon.contains(tr.v2)) {
                polygon.points.add(tr.v2);
            }
            if (!tr.v3.equals(vertex) && !polygon.contains(tr.v3)) {
                polygon.points.add(tr.v3);
            }
        }
        polygon.sort(vertex);
        for (Triangle tr : dop) {
            polygonHashMap.put(tr, polygon);
        }
        for (Triangle tr : dop) {
            for (Triangle t : getNeighbour(tr)) {
                if (!polygonHashMap.containsKey(t)) {
                    q.add(t);
                }
            }
        }
        return true;
    }

    ArrayList<Triangle> check(Vector3 point) {
        dop.clear();
        for (Triangle r : points) {
            if (r.v1.epsilonEquals(point) || r.v2.epsilonEquals(point) || r.v3.epsilonEquals(point)) {
                dop.add(r);
            }
        }
        return dop;
    }

    void createPolygonMesh() {
        int numVertices = 0;
        for (Polygon polygon : polygonHashMap.values()) {
            numVertices += (polygon.points.size() - 2) * 3;
        }
        mesh = new Mesh(
                true,
                numVertices,
                numVertices,
                VertexAttribute.Position(),
                VertexAttribute.Normal()
        );
        vertices = new float[numVertices * 6];
        indices = new short[numVertices];
        int vertex = 0;
        int index = 0;
        int point = 0;
        System.out.println(numVertices);
        for (Polygon p : polygonHashMap.values()) {
            for (int i = 0; i < p.points.size() - 2; i++) {
                Vector3 v1 = p.points.get(0);
                Vector3 v2 = p.points.get(i + 1);
                Vector3 v3 = p.points.get(i + 2);

                Vector3 not = getNormal(v1, v2, v3);
                //not.set(v1).sub(center).nor();
                vertices[vertex++] = v1.x;
                vertices[vertex++] = v1.y;
                vertices[vertex++] = v1.z;
                vertices[vertex++] = not.x;
                vertices[vertex++] = not.y;
                vertices[vertex++] = not.z;
                //not.set(v2).sub(center).nor();
                vertices[vertex++] = v2.x;
                vertices[vertex++] = v2.y;
                vertices[vertex++] = v2.z;
                vertices[vertex++] = not.x;
                vertices[vertex++] = not.y;
                vertices[vertex++] = not.z;
                //not.set(v3).sub(center).nor();
                vertices[vertex++] = v3.x;
                vertices[vertex++] = v3.y;
                vertices[vertex++] = v3.z;
                vertices[vertex++] = not.x;
                vertices[vertex++] = not.y;
                vertices[vertex++] = not.z;

                point += 3;
                indices[index++] = (short) (point - 3);
                indices[index++] = (short) (point - 2);
                indices[index++] = (short) (point - 1);
            }
        }

        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        renderable.meshPart.mesh = mesh;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = numVertices;
    }

}
