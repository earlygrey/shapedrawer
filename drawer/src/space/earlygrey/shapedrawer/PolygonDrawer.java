package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

class PolygonDrawer extends DrawerTemplate {

    static final Vector2 centre = new Vector2();

    PolygonDrawer(ShapeDrawer drawer) {
        super(drawer);
    }

    void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float lineWidth, JoinType joinType, float startAngle, float radians) {
        float angleInterval = MathUtils.PI2 / sides;
        float halfLineWidth = 0.5f*lineWidth;
        radians = ShapeUtils.normaliseAngleToPositive(radians);
        if (radians==0) radians = ShapeUtils.PI2;

        centre.set(centreX, centreY);

        switch (joinType) {
            case NONE:
                drawPolygonNoJoin(centre, sides, lineWidth, rotation, angleInterval, radiusX, radiusY, startAngle, radians);
                break;
            case SMOOTH:
                drawPolygonSmoothJoin(centre, sides, halfLineWidth, rotation, angleInterval, radiusX, radiusY, startAngle, radians);
                break;
            case POINTY: default:
                drawPolygonPointyJoin(centre, sides, halfLineWidth, rotation, angleInterval, radiusX, radiusY, startAngle, radians);
                break;
        }
    }

    void drawPolygonNoJoin(Vector2 centre, int sides, float lineWidth, float rotation, float angleInterval, float radiusX, float radiusY, float startAngle, float radians) {
        int n = sides;
        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);
        float angle = 0;
        dir.set(1, 0).rotateRad(startAngle);
        B.set(radiusX, 0).rotateRad(startAngle);
        boolean exit = false;
        for (int i = 0; i < n; i++) {
            A.set(B);
            angle += angleInterval;
            if (angle >= radians) {
                dir.set(1, 0).rotateRad(startAngle + radians);
                exit = true;
            } else {
                dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
            }
            B.set(dir).scl(radiusX, radiusY);
            float x1 = A.x*cosRot-A.y*sinRot  + centre.x, y1 = A.x*sinRot+A.y*cosRot + centre.y;
            float x2 = B.x*cosRot-B.y*sinRot  + centre.x, y2 = B.x*sinRot+B.y*cosRot + centre.y;
            drawer.line(x1, y1, x2, y2, lineWidth);
            if (exit) return;
        }
    }
    void drawPolygonPointyJoin(Vector2 centre, int sides, float halfLineWidth, float rotation, float angleInterval, float radiusX, float radiusY, float startAngle, float radians) {
        int n = sides;
        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);
        dir.set(1, 0).rotateRad(startAngle-angleInterval);
        C.set(1, 0).rotateRad(startAngle-angleInterval).scl(radiusX, radiusY);

        float angle = 0;
        for (int i = -2; i < n; i++) {
            A.set(B);
            B.set(C);
            dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
            C.set(dir).scl(radiusX, radiusY);
            if (i>=0) {
                vert1(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);
                vert2(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);
                Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
                vert3(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);
                vert4(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);
                drawVerts(); //draws current AB
            } else if (i==-1) {
                Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
            }

            if (angle >= radians) {
                C.set(1, 0).rotateRad(startAngle + radians).scl(radiusX, radiusY);
                Joiner.prepareFlatEndpoint(B, C, D, E, halfLineWidth);
                vert2(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);
                vert1(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);
                drawVerts();
                return;
            }
            angle += angleInterval;
        }
    }
    void drawPolygonSmoothJoin(Vector2 centre, int sides, float halfLineWidth, float rotation, float angleInterval, float radiusX, float radiusY, float startAngle, float radians) {
        int n = sides;
        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);
        dir.set(1, 0).rotateRad(startAngle);
        C.set(radiusX, 0).rotateRad(startAngle);
        for (int i = -2; i < n; i++) {
            if (i>=0) {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
                vert1(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);
                vert2(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);
            }
            A.set(B);
            B.set(C);
            dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
            C.set(dir).scl(radiusX, radiusY);
            if (i>=0) {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
                vert3(D.x*cosRot-D.y*sinRot  + centre.x, D.x*sinRot+D.y*cosRot + centre.y);
                vert4(E.x*cosRot-E.y*sinRot  + centre.x, E.x*sinRot+E.y*cosRot + centre.y);
                drawVerts();
                drawSmoothJoinFill(A, B, C, D, E, centre, cosRot, sinRot, halfLineWidth);
            }
        }
    }
}
