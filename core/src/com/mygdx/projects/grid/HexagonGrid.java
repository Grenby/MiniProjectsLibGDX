package com.mygdx.projects.grid;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;

public class HexagonGrid {

    protected final Vector2 gX = new Vector2();
    protected final Vector2 gY = new Vector2();

    protected final Matrix3 toCartesian = new Matrix3();
    protected final Matrix3 toHexagon = new Matrix3();

    protected final Vector2 tmp1 = new Vector2();
    protected final Vector2 tmp2 = new Vector2();
    protected final float[] oX = new float[7];
    protected final float[] oY = new float[7];


    public HexagonGrid() {
    }

    public HexagonGrid(Vector2 axis1, Vector2 axis2) {
        set(axis1, axis2);
    }

    /**
     * x,y - cartesian coordinate of point
     *
     * @param out - GridPoint for to record the result
     */
    public GridPoint2 getHexagon(float x, float y, GridPoint2 out) {
        tmp1.set(x, y).mul(toHexagon);
        int xx = (int) (tmp1.x + 0.5f * Math.signum(tmp1.x));
        int yy = (int) (tmp1.y + 0.5f * Math.signum(tmp1.y));
        return out.set(xx, yy);
    }

    public GridPoint2 getHexagon(Vector2 v, GridPoint2 out) {
        tmp1.set(v).mul(toHexagon);
        int xx = (int) (tmp1.x + 0.5f * Math.signum(tmp1.x));
        int yy = (int) (tmp1.y + 0.5f * Math.signum(tmp1.y));
        return out.set(xx, yy);
    }

//    /**
//     * transform old coordinate(cartesian) to new coordinate(hexagon coordinate system)
//     * @param out - vector with old coordinate, in this vector will be set result
//     * @return out
//     */
//    public Vector2 inNewCoordinate(Vector2 out){
//        return out.mul(toHexagon);
//    }
//
//    /**
//     * translate from hexagon coordinate system to cartesian sys
//     * @param out vector with hexagon coords
//     * @return out
//     */
//    public Vector2 inOldCoordinate(Vector2 out){
//        return out.mul(toCartesian);
//    }

    /**
     * @param point - grid point of hexagon
     * @param out   - to set in this vector coords of center
     * @return out
     */
    public Vector2 getCenter(GridPoint2 point, Vector2 out) {
        return out.set(gX).scl(point.x).mulAdd(gY, point.y);
    }

    public Vector2 getCenter(int x, int y, Vector2 out) {
        return out.set(gX).scl(x).mulAdd(gY, y);
    }

    public Vector2 getX() {
        return gX;
    }

    public Vector2 getY() {
        return gY;
    }

    public Matrix3 getToCartesian() {
        return toCartesian;
    }

    public Matrix3 getToHexagon() {
        return toHexagon;
    }

    /**
     * @return array of X coords of a hexagon centered at (0,0), oX[0] = oX[7]
     */
    public float[] getOX() {
        return oX;
    }

    /**
     * @return array of Y coords of a hexagon centered at (0,0). oY[0] = oY[7]
     */
    public float[] getOY() {
        return oY;
    }

    protected void setArray() {
        tmp1.set(gX).add(gY).scl(1f / 3f);
        tmp2.set(getY());
        tmp2.set(gX).scl(0.5f).sub(tmp1).mulAdd(gX, 0.5f);
        oX[0] = tmp2.x;
        oX[1] = tmp1.x;
        oX[2] = -tmp1.x;
        oX[3] = -tmp2.x;
        oX[4] = -tmp1.x;
        oX[5] = tmp1.x;
        oX[6] = tmp2.x;

        oY[0] = tmp2.y;
        oY[1] = tmp1.y;
        oY[2] = tmp1.y;
        oY[3] = -tmp2.y;
        oY[4] = -tmp1.y;
        oY[5] = -tmp1.y;
        oY[6] = tmp2.y;
    }

    /**
     * suppose, that (0,0) - in cartesian coords is center of first hexagon
     *
     * @param axis1 - coords of center hexagon,that bordered with first hexagon
     * @param axis2 - coords of other hexagon
     */
    public void set(Vector2 axis1, Vector2 axis2) {
        if (axis1.isCollinear(axis2))
            return;
        gX.set(axis1);
        gY.set(axis2);
        float[] val = toCartesian.val;
        val[Matrix3.M00] = gX.x;
        val[Matrix3.M01] = gY.x;
        val[Matrix3.M02] = 0;
        val[Matrix3.M10] = gX.y;
        val[Matrix3.M11] = gY.y;
        val[Matrix3.M12] = 0;
        val[Matrix3.M20] = 0;
        val[Matrix3.M21] = 0;
        val[Matrix3.M22] = 1;
        toHexagon.set(toCartesian).inv();
        setArray();
    }

//    public void set(Vector2 axis1, Vector2 axis2,Vector2 translate){
//        if (axis1.isCollinear(axis2))
//            return;
//        gX.set(axis1);
//        gY.set(axis2);
//        float [] val =toCartesian.val;
//        val[Matrix3.M00] = gX.x;
//        val[Matrix3.M01] = gY.x;
//        val[Matrix3.M02] = translate.x;
//        val[Matrix3.M10] = gX.y;
//        val[Matrix3.M11] = gY.y;
//        val[Matrix3.M12] = translate.y;
//        val[Matrix3.M20] = 0;
//        val[Matrix3.M21] = 0;
//        val[Matrix3.M22] = 1;
//        toHexagon.set(toCartesian).inv();
//        setArray();
//    }

    public static GridPoint3 toCubeCoords(GridPoint2 point, GridPoint3 out) {
        out.x = point.x;
        out.y = point.y;
        out.z = -out.x - out.y;
        return out;
    }

    public static GridPoint2 toSquareCoords(GridPoint3 point, GridPoint2 out) {
        out.set(point.x, point.y);
        return out;
    }

}
