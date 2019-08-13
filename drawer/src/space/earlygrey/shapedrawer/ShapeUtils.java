package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.MathUtils;

public class ShapeUtils {

    public static final float EPSILON = 0.001f;
    public static final float PI = MathUtils.PI;
    public static final float PI2 = (float) (2.*Math.PI);
    public static final float PI_2 = (float) (Math.PI / 2.);
    public static final float PI_4 = (float) (Math.PI / 4.);
    public static final float SQRT2 = (float) Math.sqrt(2.);
    public static final float SQRT3 = (float) Math.sqrt(3.);

    public static float snap(float a, float pixelSize, float halfPixelSize) {
        return  (Math.round(a / pixelSize) * pixelSize) + halfPixelSize;
    }

    public static boolean epsilonEquals(float a, float b) {
        return Math.abs(a-b) < EPSILON;
    }

    public static float normaliseAngleToPositive(float angle) {
        angle =  angle % PI2;
        angle = (angle + PI2) % PI2;
        return angle;
    }

    public static float floor(float x, float interval) {
        return (float) (Math.floor(x / interval) * interval);
    }

    public static float ceil(float x, float interval) {
        return (float) (Math.ceil(x / interval) * interval);
    }

}
