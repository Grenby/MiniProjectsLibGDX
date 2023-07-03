package com.mygdx.projects.ocTree;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

public class OcTree {

    private final int MAX_OBJECTS = 10;
    private final int MAX_LEVELS = 5;

    private final int level;
    private final Array<GameObject> objects;
    private final BoundingBox bounds;
    private final OcTree[] nodes;

    public OcTree(BoundingBox pBounds) {
        this(0, pBounds);
    }

    public OcTree(int pLevel, BoundingBox pBounds) {
        level = pLevel;
        objects = new Array<GameObject>();
        bounds = pBounds;
        nodes = new OcTree[8];
    }

    public BoundingBox getBounds() {
        return bounds;
    }

    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public Vector3 getCenter() {
        Vector3 out = new Vector3();
        bounds.getCenter(out);
        return out;
    }

    private void split() {
        float subWidth = bounds.getWidth() / 2f;
        float subHeight = bounds.getHeight() / 2f;
        float subDepth = bounds.getDepth() / 2f;
        Vector3 subBoxSize = new Vector3(subWidth, subHeight, subDepth);
        Vector3 center = getCenter();

        nodes[0] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(1, 1, 1).scl(subBoxSize)), new Vector3(center)));
        nodes[1] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(-1, 1, 1).scl(subBoxSize)), new Vector3(center)));
        nodes[2] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(-1, -1, 1).scl(subBoxSize)), new Vector3(center)));
        nodes[3] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(1, -1, 1).scl(subBoxSize)), new Vector3(center)));
        nodes[4] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(1, 1, -1).scl(subBoxSize)), new Vector3(center)));
        nodes[5] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(-1, 1, -1).scl(subBoxSize)), new Vector3(center)));
        nodes[6] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(-1, -1, -1).scl(subBoxSize)), new Vector3(center)));
        nodes[7] = new OcTree(level + 1, new BoundingBox(new Vector3(center).add(new Vector3(1, -1, -1).scl(subBoxSize)), new Vector3(center)));

    }

    private int getIndex(GameObject gObj, OPERATOR operator) {
        return getIndex(gObj.getTranslatedBoundingBox(), operator);
    }

    private int getIndex(BoundingBox box, OPERATOR operator) {
        for (int i = 0; i < nodes.length; i++) {
            if (operator == OPERATOR.CONTAINS) {
                if (nodes[i].getBounds().contains(box))
                    return i;
            } else if (operator == OPERATOR.INTERSECTS) {
                // Note: This function only returns 1, not all the intersecting child nodes.
                if (nodes[i].getBounds().intersects(box))
                    return i;
            }
        }
        return -1;
    }

    public void insert(GameObject gObj) {

        if (nodes[0] != null) {
            int index = getIndex(gObj, OPERATOR.CONTAINS);
            if (index != -1) {
                nodes[index].insert(gObj);
                return;
            }
        }

        objects.add(gObj);

        if (objects.size > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
                // Not the quickest way to do this but just to make sure it works now.
                Array<GameObject> removeList = new Array<GameObject>();
                for (GameObject obj : objects) {
                    int index = getIndex(obj, OPERATOR.CONTAINS);
                    if (index != -1) {
                        nodes[index].insert(obj);
                        removeList.add(obj);
                    }
                }
                for (GameObject rmvObj : removeList) {
                    objects.removeValue(rmvObj, true);
                }
            }
        }

    }

    public Array<GameObject> retrieve(BoundingBox box) {
        Array<GameObject> retrievedObjs = new Array<GameObject>();
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].getBounds().intersects(box))
                    retrievedObjs.addAll(nodes[i].retrieve(box));
            }
        }
        retrievedObjs.addAll(objects);
        return retrievedObjs;
    }

    public Array<GameObject> retrieveRay(Ray ray) {
        Array<GameObject> retrievedObjs = new Array<GameObject>();
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++) {
                //Intersector.intersectRayBounds <- this would five also intersection point.
                if (Intersector.intersectRayBoundsFast(ray, nodes[i].getBounds()))
                    retrievedObjs.addAll(nodes[i].retrieveRay(ray));
            }
        }
        retrievedObjs.addAll(objects);
        return retrievedObjs;
    }

    enum OPERATOR {
        CONTAINS,
        INTERSECTS
    }


}
