package com.mygdx.projects.ocTree;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class TestCube extends GameObject {

    public TestCube() {
        this(null);
    }

    public TestCube(Model model) {
        super(model);

        ModelBuilder modelBuilder = new ModelBuilder();
        float rndSize = (float) Math.random() * 3f + 1f;
        model = modelBuilder.createBox(rndSize, rndSize, rndSize, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position | Usage.Normal);
        setMainModel(model);

        setRandomPosition().setRandomVelocity();

    }

    public static TestCube build() {
        TestCube tmp = new TestCube(null);
        return tmp;
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
    }

}
