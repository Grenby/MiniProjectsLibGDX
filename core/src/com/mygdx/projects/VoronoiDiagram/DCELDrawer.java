package com.mygdx.projects.VoronoiDiagram;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DCELDrawer {


    public static void render(ShapeRenderer renderer, DCEL dcel, Color color) {
        renderer.setColor(color);

        renderer.begin(ShapeRenderer.ShapeType.Line);

        renderer.end();
    }


}
