package com.mygdx.projects.utils.b2dutils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.projects.Resource;

public class B2DBodyBuilder {

    public static final B2DBodyBuilder instance = new B2DBodyBuilder();

    private static final String MY_TAG = B2DBodyBuilder.class.getSimpleName();
    private Body body = null;

    private B2DBodyBuilder() {
    }

    public B2DBodyBuilder bodyDef(World world, BodyDefParam param) {
        body = world.createBody(param.mBodyDef);
        return this;
    }

    public B2DBodyBuilder addFixture(FixtureDefParam param) {
        if (this.body == null) {
            Resource.LOG.error(MY_TAG, "BodyDef isn't created yet", new NullPointerException("body is null"));
        }
        param.mFixtureDef.shape = param.mShape;
        body.createFixture(param.mFixtureDef);
        param.dispose();
        return this;
    }

    public Body build() {
        if (this.body == null) {
            Resource.LOG.error(MY_TAG, "BodyDef isn't created yet", new NullPointerException("body is null"));
        }
        Resource.LOG.info(MY_TAG, "create new " + body.getType());
        Body result = this.body;
        this.body = null;
        return result;
    }

    public static class BodyDefParam {
        final BodyDef mBodyDef;

        public BodyDefParam() {
            mBodyDef = new BodyDef();
        }

        public BodyDefParam staticBody() {
            mBodyDef.type = BodyDef.BodyType.StaticBody;
            return this;
        }

        public BodyDefParam dynamicBody() {
            mBodyDef.type = BodyDef.BodyType.DynamicBody;
            return this;
        }

        public BodyDefParam kinematicBody() {
            mBodyDef.type = BodyDef.BodyType.KinematicBody;
            return this;
        }


        public BodyDefParam setPos(float x, float y) {
            mBodyDef.position.set(x, y);
            return this;
        }

        public BodyDefParam setX(float x) {
            mBodyDef.position.x = x;
            return this;
        }

        public BodyDefParam setY(float y) {
            mBodyDef.position.y = y;
            return this;
        }

        public BodyDefParam bullet(boolean bullet) {
            mBodyDef.bullet = bullet;
            return this;
        }

        public BodyDefParam fixedRotation(boolean fixedRotation) {
            mBodyDef.fixedRotation = fixedRotation;
            return this;
        }

    }

    public static class FixtureDefParam {
        final FixtureDef mFixtureDef;
        final Vector2 tmp = new Vector2();
        Shape mShape = null;

        public FixtureDefParam() {
            mFixtureDef = new FixtureDef();
        }

        public FixtureDefParam circleShape(float radius, float centerX, float centerY) {
            if (mShape != null) mShape.dispose();
            CircleShape shape = new CircleShape();
            shape.setRadius(radius);
            shape.setPosition(tmp.set(centerX, centerY));
            mShape = shape;
            mFixtureDef.shape = mShape;
            return this;
        }

        public FixtureDefParam boxShape(float width, float height, float centerX, float centerY) {
            if (mShape != null) mShape.dispose();
            width /= 2f;
            height /= 2f;
            PolygonShape box = new PolygonShape();
            box.setAsBox(width, height, tmp.set(centerX, centerY), 0);
            mShape = box;
            mFixtureDef.shape = mShape;
            return this;
        }

        public FixtureDefParam polygonShape(float[] vertices) {
            if (mShape != null) mShape.dispose();
            PolygonShape poly = new PolygonShape();
            poly.set(vertices);
            mShape = poly;
            mFixtureDef.shape = mShape;
            return this;
        }

        public FixtureDefParam sensor(boolean sensor) {
            mFixtureDef.isSensor = true;
            return this;
        }

        public FixtureDefParam restitution(float restitution) {
            mFixtureDef.restitution = restitution;
            return this;
        }

        public FixtureDefParam density(float density) {
            mFixtureDef.density = density;
            return this;
        }

        public FixtureDefParam filter(Filter filter) {
            mFixtureDef.filter.categoryBits = filter.categoryBits;
            mFixtureDef.filter.maskBits = filter.maskBits;
            mFixtureDef.filter.groupIndex = filter.groupIndex;
            return this;
        }

        public FixtureDefParam friction(float friction) {
            mFixtureDef.friction = friction;
            return this;
        }

        void dispose() {
            mShape.dispose();
            mShape = null;
        }

    }

}