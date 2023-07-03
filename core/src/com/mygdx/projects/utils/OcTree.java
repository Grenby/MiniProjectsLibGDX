package com.mygdx.projects.utils;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FlushablePool;
import com.badlogic.gdx.utils.Pool;

public class OcTree {

    private static final FlushablePool<Leaf[]> freeLeaves = new FlushablePool<Leaf[]>() {
        @Override
        protected Leaf[] newObject() {
            Leaf[] leaves = new Leaf[8];
            for (int i = 0; i < 8; i++)
                leaves[i] = new Leaf();
            return leaves;
        }
    };

    private static class Leaf implements Pool.Poolable {
        static float minSize = 32;
        static int maxObj = 3;
        static final Vector3 tmp1 = new Vector3();
        static final Vector3 tmp2 = new Vector3();
        static final Array<Integer> arr = new Array<>(4);
        final BoundingBox box = new BoundingBox();
        final Array<GameObj> objects = new Array<>(4);
        //Leaf parent;
        Leaf[] leaves;

        //int currentLifespan = 0;

        public BoundingBox getBox() {
            return box;
        }

//        public void setParent(Leaf parent) {
//            this.parent = parent;
//        }

        boolean split() {
            if (leaves == null) {
                if (this.objects.size <= maxObj) {
                    return false;
                } else {
                    tmp1.set(box.max).sub(box.min);
                    if (Math.min(Math.min(tmp1.x, tmp1.y), tmp1.z) < minSize) {
                        return false;
                    }
                }
            } else
                return false;
            float w = box.getWidth() / 2f;
            float h = box.getHeight() / 2f;
            float d = box.getDepth() / 2f;
            box.getCenter(tmp1);
            leaves = freeLeaves.obtain();

            //for (Leaf l: leaves) {
            //     l.setParent(this);
            //}
            leaves[0].box.set(tmp1, tmp2.set(tmp1).add(w, h, d));
            leaves[1].box.set(tmp1, tmp2.set(tmp1).add(w, h, -d));
            leaves[2].box.set(tmp1, tmp2.set(tmp1).add(w, -h, d));
            leaves[3].box.set(tmp1, tmp2.set(tmp1).add(w, -h, -d));
            leaves[4].box.set(tmp1, tmp2.set(tmp1).add(-w, h, d));
            leaves[5].box.set(tmp1, tmp2.set(tmp1).add(-w, h, -d));
            leaves[6].box.set(tmp1, tmp2.set(tmp1).add(-w, -h, d));
            leaves[7].box.set(tmp1, tmp2.set(tmp1).add(-w, -h, -d));
            return true;
        }

        void build() {
            if (!split())
                return;
            for (int i = 0; i < objects.size; i++) {
                GameObj o = objects.get(i);
                BoundingBox box = o.getBoundingBox();
                for (int j = 0; j < 8; j++) {
                    if (leaves[j].box.contains(box)) {
                        leaves[j].inset(o);
                        arr.add(i);
                        objects.removeIndex(i);
                        i--;
                        break;
                    }
                }
            }
        }

        void inset(GameObj obj) {
            objects.add(obj);
            build();
        }

        void insert(Array<GameObj> objs) {
            objects.addAll(objs);
            build();
        }

        void renderAgent(ShapeRenderer renderer) {
            Vector3 min = box.min;
            tmp1.set(box.max).sub(min);
            renderer.box(min.x, min.y, min.z, tmp1.x, tmp1.y, -tmp1.z);
            if (leaves != null) {
                for (int i = 0; i < 8; i++) {
                    leaves[i].renderAgent(renderer);
                }
            }
        }

        public void intersection(BoundingBox box, Array<GameObj> out) {
            if (leaves != null) {
                for (int i = 0; i < 8; i++) {
                    if (leaves[i].box.intersects(box))
                        leaves[i].intersection(box, out);
                }
            }
            for (GameObj o : objects) {
                if (o.getBoundingBox().intersects(box))
                    out.add(o);
            }
        }

        public void intersection(Ray ray, Array<GameObj> out) {
            if (leaves != null) {
                for (int i = 0; i < 8; i++) {
                    if (Intersector.intersectRayBoundsFast(ray, leaves[i].box))
                        leaves[i].intersection(ray, out);
                }
            }
            for (GameObj o : objects) {
                if (Intersector.intersectRayBoundsFast(ray, o.getBoundingBox()))
                    out.add(o);
            }
        }

        public void intersection(Frustum frustum, Array<GameObj> out) {
            if (leaves != null) {
                for (int i = 0; i < 8; i++) {
                    if (frustum.boundsInFrustum(leaves[i].box))
                        leaves[i].intersection(box, out);
                }
            }
            for (GameObj o : objects) {
                if (frustum.boundsInFrustum(o.getBoundingBox()))
                    out.add(o);
            }
        }


        @Override
        public void reset() {

        }
    }

    private final Leaf root;
    private final Array<GameObj> insertObj = new Array<>();
    private final Array<GameObj> intersectObj = new Array<GameObj>();

    float minSize;
    int maxLifespan = 8;
    int maxObjInBox = 8;

    public OcTree() {
        root = new Leaf();
    }

    public OcTree(BoundingBox boundingBox, Array<GameObj> objs) {
        this();
        root.box.set(boundingBox);
        insert(objs);
    }

    public OcTree(Array<GameObj> objs) {
        this();
        setBox(objs);
        insert(objs);
    }

    public OcTree(BoundingBox boundingBox) {
        this();
        root.box.set(boundingBox);
    }

    public void update() {
        if (insertObj.size > 0) {
            root.insert(insertObj);
            insertObj.clear();
        }

    }

    public <T extends GameObj> void insert(T obj) {
        insertObj.add(obj);
    }

    public <T extends GameObj> void insert(T... objs) {
        insertObj.addAll(objs);
    }

    public <T extends GameObj> void insert(Array<T> objs) {
        insertObj.addAll(objs);
    }

    public void render(ShapeRenderer renderer) {
        root.renderAgent(renderer);
    }

    private void setBox(Array<GameObj> array) {
        Vector3 min = new Vector3(array.get(0).getBoundingBox().min);
        Vector3 max = new Vector3(array.get(0).getBoundingBox().max);
        for (GameObj o : array) {
            BoundingBox b = o.getBoundingBox();
            min.set(Math.min(b.min.x, min.x), Math.min(b.min.y, min.y), Math.min(b.min.z, min.z));
            max.set(Math.max(b.max.x, max.x), Math.max(b.max.y, max.y), Math.max(b.max.z, max.z));
        }
    }

    public Array<GameObj> intersection(BoundingBox box) {
        intersectObj.clear();
        root.intersection(box, intersectObj);
        return intersectObj;
    }

    public Array<GameObj> intersection(Ray ray) {
        intersectObj.clear();
        root.intersection(ray, intersectObj);
        return intersectObj;
    }

    public Array<GameObj> intersection(Frustum frustum) {
        intersectObj.clear();
        root.intersection(frustum, intersectObj);
        return intersectObj;
    }


}
