package com.mygdx.projects.ocTree;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class GameMath {


    public static float degreesToRadians(float deg) {
        return (float) (deg * ((2d * MathUtils.PI) / 360.0d));
    }

    public static float radiansToDegrees(float rad) {
        return (float) (rad * (360.0d / (2d * MathUtils.PI)));
    }

    public static float getAngleInRadians(Vector2 point) {
        return MathUtils.atan2(point.x, point.y) + degreesToRadians(180.0f);
    }

    public static float getAngleInDegrees(Vector2 point) {
        return radiansToDegrees(getAngleInRadians(point));
    }

    public static float getRandomAngleDeg() {
        return MathUtils.random(0.0f, 360.0f);
    }

    public static float getRandomAngleRad() {
        return MathUtils.random(0.0f, 2f * MathUtils.PI);
    }

    public static float getRnd() {
        return MathUtils.random(0, 10000) * 0.0001f;
    }

    public static float getRnd(float from, float to) {
        return (getRnd() * (to - from) + from);
    }

    /**
     * Angle towards something from somwhere? Usual case scenario in HUD environment.
     *
     * @param from where to
     * @param to   where
     * @return angle that can be used to set something travel towards something :D
     */
    public static float getAngleVecToVecDeg(Vector2 from, Vector2 to) {
        return (new Vector2(to).sub(from).angle());
    }

    // Fixed axis in the game so -90degree in place. why?
    public static float getAngleVecToVecDegAxisFixed(Vector2 from, Vector2 to) {
        return getAngleFromVecDegAxisFixed(new Vector2(to.x - from.x, to.y - from.y));
    }

    // Fixed axis in the game so -90degree in place. why?
    public static float getAngleFromVecDegAxisFixed(Vector2 deltaVec) {
        return getLimitedAngleDeg(deltaVec.angle() - 90f);
    }

    public static float getDistanceBetween(Vector2 pos1, Vector2 pos2) {
        //return ((new Vector2(pos1)).sub(pos2)).len(); // len2 would be optimized but not accurate enough.
        // sorry I'm afraid this len acts weirdly. Yes for somereason.
        return getDistance(pos1.x - pos2.x, pos1.y - pos2.y);
    }

    public static Vector2 getVecFromAngleDeg(float angle) {
        return new Vector2(1, 0).rotate(angle); // Why y why not x?
    }

    public static float getLimitedAngleDeg(float angle) {
        if (angle > (360f)) {
            angle %= (360f);
        }
        if (angle < (0.0f)) {
            angle = (-angle) % (360f);
            angle = (360f) - angle;
        }
        return angle;
    }

    public static float getDistance(float px, float py) {
        return ((float) Math.sqrt((px * px + py * py)));
    }

    public static float getDistance(Vector2 vec_) {
        return ((float) Math.sqrt((vec_.x * vec_.x + vec_.y * vec_.y)));
    }

    // http://www.xarg.org/2010/06/is-an-angle-between-two-other-angles/
    public static boolean isAngleInsideAngle(float n, float a, float b) {
        n = (360 + (n % 360)) % 360;
        a = (3600000 + a) % 360;
        b = (3600000 + b) % 360;
        if (a < b)
            return a <= n && n <= b;
        return a <= n || n <= b;
    }

    public static Vector3 getNormal(Vector3 A, Vector3 B, Vector3 C) {
        Vector3 AtoB = new Vector3(B).sub(A);
        Vector3 AtoC = new Vector3(C).sub(A);
        return AtoB.crs(AtoC).nor(); // NOTICE: Sets AtoB to be cross product.
    }

    // Probably from somewhere ie. stackoverflow. I didn't get the libgdx intersector working and didn't have the time to make my own.. with this one but it actually should work as well.
    public static Vector2 lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, boolean countEndingPoints) {
        double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if (denom == 0.0) { // Lines are parallel.
            return null;
        }
        double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
        double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;
        if (countEndingPoints) {
            if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
                // Get the intersection point.
                return new Vector2((int) (x1 + ua * (x2 - x1)), (int) (y1 + ua * (y2 - y1)));
            }
        } else {
            if (ua > 0.0f && ua < 1.0f && ub > 0.0f && ub < 1.0f) { // Modified.. took <= <- equal signs away.
                // Get the intersection point.
                return new Vector2((int) (x1 + ua * (x2 - x1)), (int) (y1 + ua * (y2 - y1)));
            }
        }
        return null;
    }

    public static Vector2 lineIntersect(Vector2 A, Vector2 B, Vector2 A2, Vector2 B2) {
        return lineIntersect(A.x, A.y, B.x, B.y, A2.x, A2.y, B2.x, B2.y, true);
    }

    public static Vector2 lineIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return lineIntersect(x1, y1, x2, y2, x3, y3, x4, y4, true);
    }

    public static Vector2 lineIntersectWithoutEndingPoints(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        return lineIntersect(x1, y1, x2, y2, x3, y3, x4, y4, false);
    }

    public static Vector2 lineIntersectWithoutEndingPoints(Vector2 A, Vector2 B, Vector2 A2, Vector2 B2) {
        return lineIntersectWithoutEndingPoints(A.x, A.y, B.x, B.y, A2.x, A2.y, B2.x, B2.y);
    }

    public static float getDeltaAngleToAngleDeg(float toAngle, float fromAngle) {
        return shortestDirectionToAngleDeg(toAngle, fromAngle) * distanceBetweenAnglesDeg(toAngle, fromAngle);
    }

    // From stackoverflow bcos mine wasn't working anymore
    public static int shortestDirectionToAngleDeg(float toAngle, float fromAngle) {
        int result = 0;
        if (fromAngle < toAngle) {
            if (Math.abs(fromAngle - toAngle) < 180)
                result += 1;
            else result -= 1;
        } else {
            if (Math.abs(fromAngle - toAngle) < 180)
                result -= 1;
            else result += 1;
        }
        return result;
    }

    public static float distanceBetweenAnglesDeg(float toAngle, float fromAngle) {
        float d = Math.abs(toAngle - fromAngle) % 360f;
        return d > 180 ? 360 - d : d;
    }

    public float getDistanceToPoint(Vector2 p1, Vector2 p2) {
        return getDistance(p2.x - p1.x, p2.y - p1.y);
    }


}
