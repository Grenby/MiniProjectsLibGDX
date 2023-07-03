package com.mygdx.projects.sphere;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class TriangleSphere implements RenderableProvider {

    Mesh mesh;
    Material material;
    Matrix4 tr;

    public TriangleSphere(int numIter) {


        mesh = new Mesh(
                true,
                16,
                16,
                VertexAttribute.Position(),
                VertexAttribute.Normal()
        );


        material = new Material(
                new ColorAttribute(ColorAttribute.Diffuse, 1.0f, 0.5f, 0.31f, 1),
                new ColorAttribute(ColorAttribute.Specular, 0.5f, 0.5f, 0.5f, 1),
                new ColorAttribute(ColorAttribute.Ambient, 1.0f, 0.5f, 0.31f, 1),
                new ColorAttribute(ColorAttribute.Fog, MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), MathUtils.random(0.5f, 1f), 1)
        );

    }

    @Override
    public void getRenderables(Array<Renderable> array, Pool<Renderable> pool) {

        Renderable renderable = pool.obtain();
        renderable.meshPart.mesh = mesh;
        renderable.worldTransform.set(tr);
        renderable.meshPart.offset = 0;
        renderable.meshPart.size = 6;
        renderable.meshPart.primitiveType = GL20.GL_TRIANGLES;
        renderable.material = material;
        array.add(renderable);
    }
}
