package com.mygdx.projects.grid;

import com.badlogic.gdx.math.GridPoint2;

public class HexagonChunkGrid {

    private final HexagonGrid hexagonGrid;
    private final HexagonGrid chunkGrid;
    private final int r;

    public HexagonChunkGrid(HexagonGrid hexagonGrid, HexagonGrid chunkGrid, int r) {
        this.hexagonGrid = hexagonGrid;
        this.chunkGrid = chunkGrid;
        this.r = r;
    }

    public GridPoint2 small_to_big(GridPoint2 point, GridPoint2 out) {
        int shift = 3 * r + 2;
        int area = 3 * r * r + 3 * r + 1;
        int xh, yh, zh;
        xh = Math.floorDiv(point.y + shift * point.x, area);
        yh = Math.floorDiv(-point.x - out.y + shift * point.y, area);
        zh = Math.floorDiv(point.x - shift * (point.x + point.y), area);
        return out.set(Math.floorDiv(1 + xh - yh, 3), Math.floorDiv(1 + yh - zh, 3));
    }

    public GridPoint2 chunkCenter(GridPoint2 point, GridPoint2 out) {
        return out;
    }

    public HexagonGrid getHexagonGrid() {
        return hexagonGrid;
    }

    public HexagonGrid getChunkGrid() {
        return chunkGrid;
    }

    public int getR() {
        return r;
    }

}

