package com.mygdx.projects.ants.states;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.projects.ants.Ant;

import java.util.ArrayList;

public abstract class AntState {

    protected Vector2 tmp = new Vector2();
    protected float w = 0;

    public AntState(float w) {
        this.w = w;
    }

    protected abstract Vector2 calculate(Ant ant, ArrayList<Ant> nears);

    public Vector2 getAcceleration(Ant ant, ArrayList<Ant> nears) {
        return calculate(ant, nears).nor().scl(w);
    }
}
