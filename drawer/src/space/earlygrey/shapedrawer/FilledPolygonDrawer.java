package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.MathUtils;

public class FilledPolygonDrawer extends DrawerTemplate {

    FilledPolygonDrawer(ShapeDrawer drawer) {
        super(drawer);
    }

    void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float startAngle, float radians) {

        radians = ShapeUtils.normaliseAngleToPositive(radians);
        if (radians==0) {
            radians = ShapeUtils.PI2;
            startAngle = 0;
        }

        boolean wasCaching = drawer.startCaching();

        float angleInterval = MathUtils.PI2 / sides;
        float endAngle = startAngle + radians;

        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);

        int start = (int) Math.ceil(sides * (startAngle / ShapeUtils.PI2));
        int end = (int) Math.floor(sides * (endAngle / ShapeUtils.PI2)) + 1;

        dir.set(1, 0).rotateRad(Math.min(start * angleInterval, endAngle));
        A.set(1, 0).rotateRad(startAngle).scl(radiusX, radiusY);
        B.set(dir).scl(radiusX, radiusY);

        for (int i = start; i <= end; i++) {
            x1(A.x*cosRot-A.y*sinRot  + centreX);
            y1(A.x*sinRot+A.y*cosRot + centreY);
            x2(B.x*cosRot-B.y*sinRot  + centreX);
            y2(B.x*sinRot+B.y*cosRot + centreY);
            x3(centreX);
            y3(centreY);
            drawer.pushTriangle();
            if (i<end-1) {
                A.set(B);
                dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
                B.set(dir).scl(radiusX, radiusY);
            } else if (i==end-1) {
                A.set(B);
                B.set(1, 0).rotateRad(endAngle).scl(radiusX, radiusY);
            }
        }

        if (!wasCaching) drawer.endCaching();
    }


    void rectangle(float x, float y, float width, float height, float rotation) {
        float cos = (float) Math.cos(rotation), sin = (float) Math.sin(rotation);
        float halfWidth = 0.5f*width, halfHeight = 0.5f * height;
        float centreX = x + halfWidth, centreY = y + halfHeight;
        x1(halfWidth*cos - halfHeight*sin + centreX);
        y1(halfWidth*sin + halfHeight*cos + centreY);
        x2(-halfWidth*cos - halfHeight*sin + centreX);
        y2(-halfWidth*sin + halfHeight*cos + centreY);
        x3(-halfWidth*cos - (-halfHeight*sin) + centreX);
        y3(-halfWidth*sin + (-halfHeight*cos) + centreY);
        x4(halfWidth*cos - (-halfHeight*sin) + centreX);
        y4(halfWidth*sin + (-halfHeight*cos) + centreY);
        drawer.pushQuad();
    }


}
