package com.mygdx.projects.ants;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.projects.utils.b2dutils.B2DSteeringUtils;

public class Ant implements Steerable<Vector2> {

    public final Vector2 position = new Vector2();
    public final Vector2 velocity = new Vector2();
    public final Vector2 acceleration = new Vector2();
    public final Vector2 direction = new Vector2(1, 0);

    public final Vector2 targetPosition = new Vector2();
    public int num = 0;
    float angleVelocity = 0;
    float angleAcceleration = 0;

    float maxLinearSpeed = 50;
    float maxLinearAcceleration = 30;
    float maxAngularSpeed = 10;
    float maxAngularAcceleration = 10;


    public float r = 1;

    @Override
    public Vector2 getLinearVelocity() {
        return velocity;
    }

    @Override
    public float getAngularVelocity() {
        return 0;
    }

    @Override
    public float getBoundingRadius() {
        return r;
    }

    @Override
    public boolean isTagged() {
        return false;
    }

    @Override
    public void setTagged(boolean tagged) {

    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0.001f;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {

    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {

    }

    @Override
    public float getMaxAngularSpeed() {
        return 0;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {

    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {

    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public float getOrientation() {
        return vectorToAngle(direction);
    }

    @Override
    public void setOrientation(float orientation) {
        angleToVector(direction, orientation);
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return B2DSteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return B2DSteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public void resetAcceleration() {
        acceleration.setZero();
        angleVelocity = 0;
    }

    public Ant addAcceleration(Vector2 acceleration) {
        this.acceleration.add(acceleration);
        return this;
    }

    public Ant mulAddAcceleration(Vector2 acceleration, float val) {
        this.acceleration.mulAdd(acceleration, val);
        return this;
    }

    public void update(float delta) {

        acceleration.limit(getMaxLinearAcceleration());
        velocity.limit(getMaxLinearSpeed());

        position.mulAdd(velocity, delta).mulAdd(acceleration, delta * delta / 2);
        velocity.mulAdd(acceleration, delta);

        angleToVector(direction, vectorToAngle(velocity));
    }


}
