package com.mygdx.projects.testAI;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import space.earlygrey.shapedrawer.ShapeDrawer;

public class MyWorld {

    Vector2 pos = new Vector2();
    Vector2 dim = new Vector2(100, 100);

    World world = new World(new Vector2(0, 0), false);
    Box2DDebugRenderer renderer = new Box2DDebugRenderer();

    Array<Unit> units = new Array<>();

    public MyWorld() {
        units.add(new Unit(10, 10));

    }

    void correct(Unit unit) {
        if (unit.pos.x - unit.r < pos.x)
            unit.pos.x = unit.r;
        if (unit.pos.y - unit.r < pos.y)
            unit.pos.y = unit.r;
        if (unit.pos.x + unit.r > pos.x + dim.x)
            unit.pos.x = pos.x + dim.x - unit.r;
        if (unit.pos.y + unit.r > pos.y + dim.y)
            unit.pos.y = pos.y + dim.y - unit.r;
    }

    public void update(float delta) {
        for (Unit u : units) {
            u.update(delta);
            correct(u);
        }
    }

    public void render(ShapeDrawer drawer) {

        renderer.render(world, drawer.getBatch().getProjectionMatrix());
        drawer.getBatch().begin();
        for (Unit u : units) {
            u.render(drawer);
        }
        drawer.getBatch().end();
    }

    public Unit addUnit(Vector2 pos) {
        Unit unit = new Unit(pos.x, pos.y);
        units.add(unit);
        return unit;
    }

}
