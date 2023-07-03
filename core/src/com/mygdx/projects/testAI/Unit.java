package com.mygdx.projects.testAI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class Unit {

    public Vector2 dirVelocity = new Vector2();
    public Vector2 pos = new Vector2(0, 0);
    float r = 0.5f;
    float velocity = 5f;

    public Unit(float x, float y) {
        pos.set(x, y);
    }


    public void update(float delta) {
        Holder.field.getGrad(pos, dirVelocity);
        dirVelocity.scl(-1);
        pos.mulAdd(dirVelocity, velocity * delta);
    }

    public void render(ShapeDrawer shapeDrawer) {
        shapeDrawer.setColor(Color.RED);
        shapeDrawer.filledCircle(pos.x, pos.y, r);
        shapeDrawer.setColor(Color.BLACK);
        shapeDrawer.line(pos.x, pos.y, pos.x + dirVelocity.x * 2 * r, pos.y + dirVelocity.y * 2 * r, 0.1f);
    }


}
