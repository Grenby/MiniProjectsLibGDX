package com.mygdx.projects.ocTree;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Disposable;

// Solid path changed to subj

public class GameObject implements Disposable {

    private static int lastGeneretedId = 0;
    public int debugPaint = 0;
    public Matrix4 transform = new Matrix4();
    public float DEFAULT_MAX_SPEED = 5.5f;
    public float maxSpeed = DEFAULT_MAX_SPEED;
    public Vector3 size = new Vector3(10, 10, 10);
    BoundingBox boundingBox = new BoundingBox();
    BoundingBox translatedBoundingBox = new BoundingBox();
    ModelInstance model;
    Vector3 velocity = new Vector3();
    float lifeTime = 0.0f;
    float density = 1f;
    boolean vanquish = false; // Key to destroy this element.
    boolean alive = false;

    float radius = -1f;

    String typeId = "";
    Integer id = -1;

    public GameObject(Model model) {
        setMainModel(model);
        if (id == -1)
            generateNewId();
        reset();
        loadDefaults();
        addToGameEnvironment();
    }

    public GameObject() {
        this(null);
    }

    public void addToGameEnvironment() {
        MyGdxGame.addObj(this);
    }

    public void init() {
        // TODO: Here some setting to reset velocity etc.. ?
    }

    // The deepest cloning.. Maybe this could return GO later?
    public void cloneTo(GameObject gObj) {
        gObj.size = new Vector3(this.size);
        gObj.id = this.id;
    }

    public void spawn() {
        setReferences();
    }

    public void setReferences() {

    }

    public void reset() {
        init();
        alive = true;
    }

    public String getTypeId() {
        return getClass().getSimpleName();
        //return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    // Notice that Id must be compared with care.
    public Boolean hasSameId(GameObject gObj) {
        return getId().equals(gObj.getId());
    }

    public Integer getId() {
        return new Integer(this.id);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void removeMe() {
        this.vanquish = true;
    }

    public void revertRemoval() {
        vanquish = false;
    }

    public boolean isSetRemoveMe() {
        return vanquish;
    }

    public void setRandomRotation() {
        transform.setToTranslation(getPosition());
        transform.rotate(new Vector3(MathUtils.random(), MathUtils.random(), MathUtils.random()), MathUtils.random() * 360f);
    }

    public void setRandomVelocity() {
        float range = 5f;
        setVelocity(MathUtils.random(-range, range), MathUtils.random(-range, range), MathUtils.random(-range, range));
    }

    public GameObject setRandomPosition() {
        return setPosition(MathUtils.random(-MyGdxGame.MAP_SIZE / 2f, MyGdxGame.MAP_SIZE / 2f),
                MathUtils.random(-MyGdxGame.MAP_SIZE / 2f, MyGdxGame.MAP_SIZE / 2f),
                MathUtils.random(-MyGdxGame.MAP_SIZE / 2f, MyGdxGame.MAP_SIZE / 2f));
    }

    public void generateNewId() {
        setId(lastGeneretedId + 1);
        lastGeneretedId++; // YAGH DISCUSTING. what about some function? well this will do now :)
    }

    public ModelInstance getMainModelInstance() {
        return model;
    }

    public void setMainModel(Model model) {
        if (model != null) {
            this.model = new ModelInstance(model);
            calculateSize();
        } else
            this.model = null; // Model was nullified.
    }


    public void loadDefaults() {
        setReferences();
    }

    public void updateBoundingBoxPosition() {
        translatedBoundingBox = new BoundingBox();
        translatedBoundingBox.set(new Vector3(boundingBox.min).add(getPosition()), new Vector3(boundingBox.max).add(getPosition()));
    }

    // Calculate size of the objects from bounding box.
    public void calculateSize() {
        try {
            model.calculateBoundingBox(boundingBox);
            updateBoundingBoxPosition();
            setSize(boundingBox.getWidth(), boundingBox.getHeight(), boundingBox.getDepth());
        } catch (Exception e) {
            // Probably model shrunk completely ie exploded so impossible to calculate bounding box :)
            setSize(0, 0, 0);
        }
    }

    public float getMass() {
        return getAvgSize() * getDensity();
    }

    public boolean isMoving() {
        return !nearZeroSpeed();
    }

    public boolean nearZeroSpeed() {
        return getSpeed() < 0.001f;
    }

    public Vector3 getSize() {
        return new Vector3(size);
    }

    public void setSize(Vector3 sizeV) {
        size = new Vector3(sizeV);
    }

    public Vector3 getHalfSize() {
        return new Vector3(size.x / 2, size.y / 2, size.z / 2);
    }

    public float getRadius() {
        if (radius == -1)
            return getAvgSize() / 2f;
        else
            return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getAvgSize() {
        return (size.x + size.y) / 2.0f;
    }

    public float getHitDistance() {
        return getAvgSize() / 2.0f; // Avarage Radius
    }

    public void setSize(float w, float h, float d) {
        size = new Vector3(w, h, d);
    }

    public float getSizeX() {
        return size.x;
    }

    public float getSizeY() {
        return size.y;
    }

    public float getSizeZ() {
        return size.z;
    }

    public float getX() {
        return getPosition().x;
    }

    public float getY() {
        return getPosition().y;
    }

    public float getZ() {
        return getPosition().z;
    }

    public Vector3 getForwardVec() {
        Vector3 baseDirection = new Vector3(0, 1, 0).nor(); // Axis for rotation
        Quaternion rot = new Quaternion();
        Vector3 direction = new Vector3();
        direction.set(baseDirection);
        transform.getRotation(rot);
        direction.mul(rot).nor();
        return direction;
    }

    public Vector3 getPosition() {
        Vector3 playerPos = new Vector3();
        transform.getTranslation(playerPos);
        return playerPos;
    }

    public GameObject setPosition(Vector3 pos) {
        Quaternion rot = new Quaternion();
        transform.getRotation(rot);
        transform.setToTranslation(pos);
        transform.rotate(rot);
        return this;
    }

    public GameObject setPosition(float x, float y, float z) {
        return setPosition(new Vector3(x, y, z));
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public GameObject setVelocity(Vector3 vel) {
        velocity = new Vector3(vel);
        return this;
    }

    public void addForce(Vector3 f) {
        velocity.add(f);
    }

    public GameObject setVelocity(float x, float y, float z) {
        return setVelocity(new Vector3(x, y, z));
    }

    public float getAngularVelocity() {
        return 0f;
        // not done
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getSpeed() {
        return getVelocity().len();
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public float getDensity() {
        return this.density;
    }

    public void setDensity(float density) {
        this.density = density;
    }

    public float getLifeTimeSec() {
        return lifeTime;
    }

    public float getLifeTimeMs() {
        return lifeTime * 1000f;
    }

    /* Returns bounding box. No translation. Just dimensions. Not rotated or anything. It has been made private to avoid accidental using this in place where translatedBoundingBox should be used. */
    private BoundingBox getBoundingBox() {
        return new BoundingBox(boundingBox);
    }

    public BoundingBox getTranslatedBoundingBox() {
        return new BoundingBox(translatedBoundingBox);
    }

    public void transformModels() {
        if (this.model != null)
            GameUtils.cloneTransformationTo(transform, model.transform);
        calculateSize(); // Updating bounding box every frame for now..
    }

    public void refresh() {

        // Take care of moving object's modelinstances according the main transformation.
        transformModels();

        final float delta = Math.min(1f / 30f, Gdx.graphics.getDeltaTime());
        lifeTime += delta;

        // Move object simply according velocity..
        Vector3 currentPosition = new Vector3(getPosition());
        setPosition(currentPosition.add(new Vector3(getVelocity()).scl(delta)));

        manageMapBoarder();

    }

    void manageMapBoarder() {
        Vector3 pos = getPosition();
        if (pos.x < -MyGdxGame.MAP_SIZE / 2f)
            pos.x = MyGdxGame.MAP_SIZE / 2f + (pos.x + MyGdxGame.MAP_SIZE / 2f);
        if (pos.x > MyGdxGame.MAP_SIZE / 2f)
            pos.x = (pos.x - MyGdxGame.MAP_SIZE / 2f);
        if (pos.y < -MyGdxGame.MAP_SIZE / 2f)
            pos.y = MyGdxGame.MAP_SIZE / 2f + (pos.y + MyGdxGame.MAP_SIZE / 2f);
        if (pos.y > MyGdxGame.MAP_SIZE / 2f)
            pos.y = (pos.y - MyGdxGame.MAP_SIZE / 2f);
        if (pos.z < -MyGdxGame.MAP_SIZE / 2f)
            pos.z = MyGdxGame.MAP_SIZE / 2f + (pos.z + MyGdxGame.MAP_SIZE / 2f);
        if (pos.z > MyGdxGame.MAP_SIZE / 2f)
            pos.z = (pos.z - MyGdxGame.MAP_SIZE / 2f);
        setPosition(pos);
    }

    public void setColor(Color color) {
        if (model != null)
            getMainModelInstance().materials.get(0).set(ColorAttribute.createDiffuse(color));
    }

    public void draw3D() {
        if (this.model != null)
            MyGdxGame.modelBatch.render(this.model, MyGdxGame.environment);
    }

    @Override
    public void dispose() {
        // ..
    }

}
