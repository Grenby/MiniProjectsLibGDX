package com.mygdx.projects.VoronoiDiagram;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Shell {

    Array<Vector2> array = new Array<>();
    Array<Vector2> shell = new Array<>(10);

    Vector2 tmp1 = new Vector2();
    Vector2 tmp2 = new Vector2();

    public Array<Vector2> getShell(Array<Vector2> points) {
        return getShell(points, shell, false);
    }

    private float crs(Vector2 v1, Vector2 v2, Vector2 v3) {
        return tmp1.set(v2).sub(v1).crs(tmp2.set(v3).sub(v1));
    }

    public Array<Vector2> getShell(Array<Vector2> points, Array<Vector2> shell, boolean sorted) {
        shell.clear();

        if (points.size > 1) {
            int n = points.size;
            if (!sorted) {
                array.clear();
                array.addAll(points);
                array.sort((o1, o2) -> Float.compare(o1.x, o2.x));
            } else {
                array = points;
            }
            // Build lower hull
            for (int i = 0; i < n; ++i) {
                while (shell.size >= 2 && crs(shell.get(shell.size - 2), shell.get(shell.size - 1), array.get(i)) <= 0)
                    shell.size--;
                shell.add(array.get(i));
            }

            // Build upper hull
            for (int i = n - 2, t = shell.size + 1; i >= 0; i--) {
                while (shell.size >= t && crs(shell.get(shell.size - 2), shell.get(shell.size - 1), array.get(i)) <= 0)
                    shell.size--;
                shell.add(array.get(i));
            }

        } else {
            shell.addAll(points);
        }
        return shell;
    }


}
