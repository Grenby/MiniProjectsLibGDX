package com.mygdx.projects.life;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Field {

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Runnable[] runs = new Runnable[4];
    static int[] OX = {1, 0, -1, -1, 0, 0, 1, 1};
    static int[] OY = {0, 1, 0, 0, -1, -1, 0, 0};

    public interface UpdateCell {
        int update(int[][] field, int x, int y);
    }

    public static class Default implements UpdateCell {
        @Override
        public int update(int[][] field, int x, int y) {
            int num = 0;
            int w = field.length;
            int h = field[0].length;

            int x0 = x;
            int y0 = y;

            for (int i = 0; i < OX.length; i++) {
                x = x + OX[i];
                y = y + OY[i];

                if (x < 0) x += w;
                if (y < 0) y += h;

                if (x == w) x = 0;
                if (y == h) y = 0;

                if (field[x][y] == 1)
                    num++;
            }
            if (field[x0][y0] == 1 && (num == 2 || num == 3))
                return 1;
            double r = Math.random();
            if (r > 0.5)
                return num == 4 ? 1 : 0;
            else
                return num == 3 ? 1 : 0;

        }
    }

    private final int[][] tmp;

    public int[][] field;
    public UpdateCell updateCell;
    public int width;
    public int height;

    public Field(int w, int h) {
        field = new int[w][h];
        tmp = new int[w][h];
        updateCell = new Default();
        width = w;
        height = h;

        for (int i = 0; i < 4; i++) {
            final int from = w / 4 * i;
            final int to = (i == 3) ? w : w / 4 * (i + 1);
            runs[i] = () -> {
                for (int i1 = from; i1 < to; i1++) {
                    for (int j = 0; j < field.length; j++) {
                        tmp[i1][j] = updateCell.update(field, i1, j);
                    }
                }
            };
        }

    }

    public void set(int x, int y, int value) {
        field[x][y] = value;
    }

    public void add(int x, int y) {
        field[x][y] = 1;
    }

    public void remove(int x, int y) {
        field[x][y] = 0;
    }

    public void update() {


        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                tmp[i][j] = 0;
            }
        }
//        for (Runnable r: runs) {
//            executor.submit(r);
//        }
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field.length; j++) {
                tmp[i][j] = updateCell.update(field, i, j);
            }
        }
        for (int i = 0; i < field.length; i++) {
            System.arraycopy(tmp[i], 0, field[i], 0, field.length);
        }
    }

}
