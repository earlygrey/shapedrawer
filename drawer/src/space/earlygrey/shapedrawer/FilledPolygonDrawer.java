package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ShortArray;

class FilledPolygonDrawer extends DrawerTemplate<PolygonShapeDrawer> {

    static final EarClippingTriangulator triangulator = new EarClippingTriangulator();

    FilledPolygonDrawer(PolygonShapeDrawer drawer) {
        super(drawer);
    }

    void polygon(float centreX, float centreY, int sides, float radiusX, float radiusY, float rotation, float startAngle, float radians) {
        if (radians==0) return;
        radians = Math.min(radians, ShapeUtils.PI2);

        boolean wasCaching = drawer.startCaching();

        float angleInterval = MathUtils.PI2 / sides;
        float endAngle = startAngle + radians;

        float cos = (float) Math.cos(angleInterval), sin = (float) Math.sin(angleInterval);
        float cosRot = (float) Math.cos(rotation), sinRot = (float) Math.sin(rotation);

        int start = (int) Math.ceil(sides * (startAngle / ShapeUtils.PI2));
        int end = (int) Math.floor(sides * (endAngle / ShapeUtils.PI2)) + 1;

        if (ShapeUtils.epsilonEquals(start * angleInterval, startAngle)) start++;

        int n = end-start;
        drawer.ensureSpace(n + 2);
        int vertexOffset = drawer.getVerticesArrayIndex();

        //centre point - triangle index 0
        x1(centreX);
        y1(centreY);
        drawer.pushVertex();

        //first perimeter vertex (at start angle) - triangle index 1
        A.set(1, 0).rotateRad(startAngle).scl(radiusX, radiusY);
        x1(A.x*cosRot-A.y*sinRot  + centreX);
        y1(A.x*sinRot+A.y*cosRot + centreY);
        drawer.pushVertex();
        drawer.pushTriangleIndices((short) vertexOffset, (short) (vertexOffset+1), (short) (vertexOffset+2));

        //evenly spaced perimeter vertices
        dir.set(1, 0).rotateRad(Math.min(start * angleInterval, endAngle));
        A.set(dir).scl(radiusX, radiusY);
        for (int i = 0; i < n-1; i++) {
            x1(A.x*cosRot-A.y*sinRot  + centreX);
            y1(A.x*sinRot+A.y*cosRot + centreY);
            drawer.pushVertex();
            dir.set(dir.x * cos - dir.y * sin, dir.x * sin + dir.y * cos);
            A.set(dir).scl(radiusX, radiusY);

            drawer.pushTriangleIndices((short) vertexOffset, (short) (vertexOffset+i+2), (short) (vertexOffset+i+3));
        }

        //last perimeter vertex (at end angle) - triangle index n+1 (already included in loop)
        A.set(1, 0).rotateRad(endAngle).scl(radiusX, radiusY);
        x1(A.x*cosRot-A.y*sinRot  + centreX);
        y1(A.x*sinRot+A.y*cosRot + centreY);
        drawer.pushVertex();

        if (!wasCaching) drawer.endCaching();
    }

    void polygon(float[] vertices) {
        ShortArray triangles = triangulator.computeTriangles(vertices);
        polygon(vertices, triangles);
    }

    void polygon(float[] vertices, ShortArray triangles) {
        polygon(vertices, triangles.items, triangles.size);
    }

    void polygon(float[] vertices, short[] triangles) {
        polygon(vertices, triangles, triangles.length);
    }

    void polygon(float[] vertices, short[] triangles, int trianglesCount) {
        int n = vertices.length / 2;
        /*if (n * ShapeDrawer.VERTEX_SIZE < ShapeDrawer.VERTEX_CACHE_SIZE) {
            throw new IllegalStateException("Cannot draw a polygon with more than " + (ShapeDrawer.VERTEX_CACHE_SIZE*ShapeDrawer.VERTEX_SIZE) + " vertices.");
        }*/
        drawer.ensureSpace(n);
        drawer.pushVertexData(vertices, triangles, trianglesCount);
        drawer.pushToBatch();
    }

    void rectangle(float x, float y, float width, float height, float rotation) {
        drawer.ensureSpaceForQuad();
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
