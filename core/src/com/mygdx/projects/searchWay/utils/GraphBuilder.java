package com.mygdx.projects.searchWay.utils;

import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.projects.utils.garphs.IndexedGraphImpl;

public class GraphBuilder {

    public static IndexedGraphImpl<Vector2> buildGrid(int w, int h) {
        return buildGrid(w, h, (x, y, out) -> out.set(x, y));
    }

    public static IndexedGraphImpl<Vector2> buildGrid(int w, int h, CoordinateMapper mapper) {
        IndexedGraphImpl<Vector2> indexedGraph = new IndexedGraphImpl<>();
        Vector2[][] points = new Vector2[w][h];
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                points[i][j] = new Vector2();
                mapper.map(i, j, points[i][j]);
            }
        }

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Vector2 from = points[i][j];
                if (i != 0) {
                    indexedGraph.addConnection(new DefaultConnection<>(from, points[i - 1][j]));
                }
                if (j != 0) {
                    indexedGraph.addConnection(new DefaultConnection<>(from, points[i][j - 1]));
                }
                if (i != w - 1) {
                    indexedGraph.addConnection(new DefaultConnection<>(from, points[i + 1][j]));
                }
                if (j != h - 1) {
                    indexedGraph.addConnection(new DefaultConnection<>(from, points[i][j + 1]));
                }

            }
        }

        return indexedGraph;
    }

    public static IndexedGraphImpl<Vector2> buildRandomGraph(float x0, float y0, float x1, float y1, int num) {
        IndexedGraphImpl<Vector2> indexedGraph = new IndexedGraphImpl<>();
        Array<Vector2> points = new Array<>();
        for (int i = 0; i < num; i++) {
            float x = MathUtils.random() * (x1 - x0) + x0;
            float y = MathUtils.random() * (y1 - y0) + y0;
            points.add(new Vector2(x, y));
        }

        for (int i = 0; i < num; i++) {
            Vector2 vector = points.get(i);

            float d1 = Float.MAX_VALUE;
            float d2 = Float.MAX_VALUE;
            Vector2 n1 = points.get(i);
            Vector2 n2 = points.get(i);

            for (int j = 0; j < num; j++) {
                if (i == j) {
                    continue;
                }
                Vector2 to = points.get(j);
                float d = to.dst2(vector);
                if (d < d1) {
                    d2 = d1;
                    n2 = n1;
                    d1 = d;
                    n1 = to;
                } else if (d < d2) {
                    d2 = d;
                    n2 = to;
                }
            }
            indexedGraph.addConnection(new DefaultConnection<>(vector, n1));
            indexedGraph.addConnection(new DefaultConnection<>(n1, vector));
            indexedGraph.addConnection(new DefaultConnection<>(vector, n2));
            indexedGraph.addConnection(new DefaultConnection<>(n2, vector));
        }

        return indexedGraph;
    }


    public interface CoordinateMapper {

        void map(int x, int y, Vector2 out);

    }

}
