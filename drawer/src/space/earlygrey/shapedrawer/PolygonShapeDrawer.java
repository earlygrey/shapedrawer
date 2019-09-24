package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ShortArray;

import java.util.Arrays;

/**
 * <p>Uses a PolygonBatch to draw lines, shapes (filled or outlined) and paths. Meant to be an analogue of {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer}
 * but uses a Batch instead of an {@link com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer}, so that it can be used
 * in between {@link PolygonBatch#begin()} and {@link PolygonBatch#end()}.</p>
 * <p>Line mitering can be performed when drawing Polygons and Paths, see {@link JoinType} for options.</p>
 * <p>Also includes an option to snap lines to the centre of pixels, see {@link #line(float, float, float, float, float, boolean)}
 * for more information.</p>
 * <p>Uses the projection matrix of the supplied Batch so there is no need to set one as with {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer}.</p>
 * <p>The difference between this and {@link space.earlygrey.shapedrawer.ShapeDrawer} is that this can draw filled
 * shapes and requires a {@link PolygonBatch}, a {@link space.earlygrey.shapedrawer.ShapeDrawer} cannot draw
 * filled shapes but just requires a {@link com.badlogic.gdx.graphics.g2d.Batch}.</p>
 *
 * @author earlygrey
 */

public class PolygonShapeDrawer extends ShapeDrawer {

    //================================================================================
    // MEMBERS
    //================================================================================

    protected final FilledPolygonDrawer filledPolygonDrawer;

    short[] triangles;
    int triangleOffset = 0;
    final int TRIANGLE_PUSH_SIZE = 3 * VERTEX_SIZE;

    //================================================================================
    // CONSTRUCTOR
    //================================================================================

    public PolygonShapeDrawer(PolygonBatch batch, TextureRegion region) {
        super(batch, region);
        // every 4 vertices pushed needs at most 6 triangle indices (6/4 = 1.5)
        int trianglesLength = (int) Math.ceil((verts.length / VERTEX_SIZE) * 1.5);
        triangles = new short[trianglesLength];
        filledPolygonDrawer = new FilledPolygonDrawer(this);
    }


    //================================================================================
    // CONSTRUCTOR
    //================================================================================

    /*
        Note that the constructor ensures that the Batch is a PolygonBatch.
     */
    @Override
    public PolygonBatch getBatch() {
        return (PolygonBatch) super.getBatch();
    }

    @Override
    void pushQuad() {
        triangles[triangleOffset++] = (short) (vertexCount + 0);
        triangles[triangleOffset++] = (short) (vertexCount + 1);
        triangles[triangleOffset++] = (short) (vertexCount + 2);
        triangles[triangleOffset++] = (short) (vertexCount + 0);
        triangles[triangleOffset++] = (short) (vertexCount + 2);
        triangles[triangleOffset++] = (short) (vertexCount + 3);
        super.pushQuad();
    }


    @Override
    void pushTriangle() {
        triangles[triangleOffset++] = (short) (vertexCount + 0);
        triangles[triangleOffset++] = (short) (vertexCount + 1);
        triangles[triangleOffset++] = (short) (vertexCount + 2);
        int i = getArrayOffset();
       /* verts[i + SpriteBatch.U1] = r.getU();
        verts[i + SpriteBatch.V1] = r.getV();
        verts[i + SpriteBatch.U2] = r.getU2();
        verts[i + SpriteBatch.V2] = r.getV();
        verts[i + SpriteBatch.U3] = r.getU2();
        verts[i + SpriteBatch.V3] = r.getV2();*/
        verts[i + SpriteBatch.C1] = floatBits;
        verts[i + SpriteBatch.C2] = floatBits;
        verts[i + SpriteBatch.C3] = floatBits;
        vertexCount += TRIANGLE_PUSH_SIZE;
    }

    protected void pushVertexData(float[] vertices, short[] triangles, int trianglesCount)  {

        /*float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE;
        float minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;

        for (int j = 0; j < vertices.length; j+=2) {
            float x = vertices[j], y = vertices[j+1];
            minX = x<minX?x:minX;
            maxX = x>maxX?x:maxX;
            minY = y<minY?y:minY;
            maxY = y>maxY?y:maxY;
        }

        float w = maxX-minX, h = maxY-minY;*/

        int i = getArrayOffset();

        for (int j = 0; j < vertices.length; j+=2) {
            float x = vertices[j], y = vertices[j+1];
            verts[i + SpriteBatch.X1] = x;
            verts[i + SpriteBatch.Y1] = y;
            verts[i + SpriteBatch.C1] = floatBits;
            /*verts[i + SpriteBatch.U1] = r.getU();
            verts[i + SpriteBatch.V1] = r.getV();*/

            i += VERTEX_SIZE;
        }

        for (int j = 0, n = trianglesCount; j < n; j++) {
            this.triangles[triangleOffset++] = (short) (vertexCount + triangles[j]);
        }

        vertexCount += vertices.length / 2;
    }

    @Override
    void ensureSpaceForTriangle() {
        ensureSpace(3);
    }

    /**
     * <p>Calls {@link PolygonBatch#draw(com.badlogic.gdx.graphics.Texture, float[], int, int, short[], int, int)}
     * using the currently cached vertex and triangle information.</p>
     */
    @Override
    void pushToBatch() {
        if (vertexCount == 0) return;
        getBatch().draw(r.getTexture(), verts, 0, getArrayOffset(), triangles, 0, triangleOffset);
        vertexCount = 0;
        triangleOffset = 0;
    }


    //================================================================================
    // DRAWING METHODS
    //================================================================================

    //=======================================
    //          CIRCLES AND ELLIPSES
    //=======================================

    /**
     * <p>Draws a filled circle by calling {@link #filledEllipse(float, float, float, float, float)} with rotation set to 0
     * and radiusX and radiusY set to {@code radius}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius
     */
    public void filledCircle(float centreX, float centreY, float radius) {
        filledEllipse(centreX, centreY, radius, radius, 0);
    }

    /**
     * <p>Draws a filled ellipse by calling {@link #filledEllipse(float, float, float, float, float)} with rotation set to 0.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radiusX the radius along the x-axis
     * @param radiusY the radius along the y-axis
     */
    public void filledEllipse(float centreX, float centreY, float radiusX, float radiusY) {
        filledEllipse(centreX, centreY, radiusX, radiusY, 0);
    }

    /**
     * <p>Draws an ellipse as a stretched regular polygon, estimating the number of sides required
     * (see {@link #estimateSidesRequired(float, float)}) to appear smooth enough based on the
     * pixel size that has been set. Calls {@link #polygon(float, float, int, float, float, float, JoinType)}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radiusX the radius along the x-axis
     * @param radiusY the radius along the y-axis
     * @param rotation the anticlockwise rotation in radians
     */
    public void filledEllipse(float centreX, float centreY, float radiusX, float radiusY, float rotation) {
        filledPolygonDrawer.polygon(centreX, centreY, estimateSidesRequired(radiusX, radiusY), radiusX, radiusY, rotation, 0, ShapeUtils.PI2);
    }

    //=======================================
    //          SECTORS (PIE SLICES)
    //=======================================

    /**
     * <p>Draws a sector (pie slice) by calling {@link #sector(float, float, float, float, float, int)} with the number of sides estimated by {@link #estimateSidesRequired(float, float)}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle in radians at which the arc starts
     * @param radians the angle in radians subtended by the arc
     */
    public void sector(float centreX, float centreY, float radius, float startAngle, float radians) {
        sector(centreX, centreY, radius, startAngle, radians, estimateSidesRequired(radius, radius));
    }

    /**
     * <p>Draws a sector (pie slice) by from {@code startAngle} anti-clockwise that subtends the specified angle.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle in radians at which the arc starts
     * @param radians the angle in radians subtended by the arc
     * @param sides the number of straight line segments to draw the arc with
     */
    public void sector(float centreX, float centreY, float radius, float startAngle, float radians, int sides) {
        filledPolygonDrawer.polygon(centreX, centreY, sides, radius, radius, 0, startAngle, radians);
    }

    //=======================================
    //           REGULAR POLYGONS
    //=======================================


    /**
     * <p>Calls {@link #filledPolygon(float, float, int, float, float)} with scaleX and scaleY set to
     * {@code scale} and the rotation set to 0.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param scale the scale
     */
    public void filledPolygon(float centreX, float centreY, int sides, float scale) {
        filledPolygon(centreX, centreY, sides, scale, scale, 0);
    }

    /**
     * <p>Calls {@link #filledPolygon(float, float, int, float, float, float)} with scaleX and scaleY set to
     * {@code scale}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param radius the radius
     * @param rotation the anticlockwise rotation in radians
     */
    public void filledPolygon(float centreX, float centreY, int sides, float radius, float rotation) {
        filledPolygon(centreX, centreY, sides, radius, radius, rotation);
    }

    /**
     * <p>Draws a filled regular polygon.</p>
     *
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param scaleX the scale along the x-axis
     * @param scaleY the scale along the y-axis
     * @param rotation the rotation in radians after scaling
     */
    public void filledPolygon(float centreX, float centreY, int sides, float scaleX, float scaleY, float rotation) {
        filledPolygonDrawer.polygon(centreX, centreY, sides, scaleX, scaleY, rotation, 0, ShapeUtils.PI2);
    }



    //=======================================
    //           ARBITRARY POLYGONS
    //=======================================

    /**
     * <p>Draws a filled polygon.</p>
     * <p>Note: this triangulates the polygon every time it is called - it is greatly recommended to cache
     * the triangles and use {@link #filledPolygon(Polygon, short[])} or {@link #filledPolygon(Polygon, ShortArray)} instead.
     * You can use something like {@link com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])} to calculate the triangles.</p>
     * @param polygon the polygon to draw
     */
    public void filledPolygon(Polygon polygon) {
        filledPolygon(polygon.getTransformedVertices());
    }

    /**
     * <p>Draws a filled polygon using the specified vertices.</p>
     * <p>Note: this triangulates the polygon every time it is called - it is greatly recommended to cache
     * the triangles and use {@link #filledPolygon(float[], short[])} or {@link #filledPolygon(float[], ShortArray)} instead.
     * You can use something like {@link com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])} to calculate the triangles.</p>
     * @param vertices consecutive ordered pairs of the x-y coordinates of the vertices of the polygon
     */
    public void filledPolygon(float[] vertices) {
        filledPolygonDrawer.polygon(vertices);
    }

    /**
     * <p>Draws a filled polygon using the specified vertices.</p>
     * @param polygon the polygon to draw
     * @param triangles ordered triples of the indices of the float[] defining the polygon vertices corresponding to triangles.
     *                  You can use something like {@link com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])} to
     *                  calculate them.
     */
    public void filledPolygon(Polygon polygon, short[] triangles) {
        filledPolygon(polygon.getTransformedVertices(), triangles);
    }

    /**
     * <p>Draws a filled polygon using the specified vertices.</p>
     * @param vertices consecutive ordered pairs of the x-y coordinates of the vertices of the polygon
     * @param triangles ordered triples of the indices of the float[] defining the polygon vertices corresponding to triangles.
     *                  You can use something like {@link com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])} to
     *                  calculate them.
     */
    public void filledPolygon(float[] vertices, short[] triangles) {
        filledPolygonDrawer.polygon(vertices, triangles);
    }

    /**
     * <p>Draws a filled polygon using the specified vertices.</p>
     * @param polygon the polygon to draw
     * @param triangles ordered triples of the indices of the float[] defining the polygon vertices corresponding to triangles.
     *                  You can use something like {@link com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])} to
     *                  calculate them.
     */
    public void filledPolygon(Polygon polygon, ShortArray triangles) {
        filledPolygon(polygon.getTransformedVertices(), triangles);
    }

    /**
     * <p>Draws a filled polygon using the specified vertices.</p>
     * @param vertices consecutive ordered pairs of the x-y coordinates of the vertices of the polygon
     * @param triangles ordered triples of the indices of the float[] defining the polygon vertices corresponding to triangles.
     *                  You can use something like {@link com.badlogic.gdx.math.EarClippingTriangulator#computeTriangles(float[])} to
     *                  calculate them.
     */
    public void filledPolygon(float[] vertices, ShortArray triangles) {
        filledPolygonDrawer.polygon(vertices, triangles);
    }


    //=======================================
    //              RECTANGLES
    //=======================================

    /**
     * <p>Calls {@link #filledRectangle(float, float, float, float)}.</p>
     * @param rect a {@link Rectangle} object
     */
    public void filledRectangle(Rectangle rect) {
        rectangle(rect.x, rect.y, rect.width, rect.height);
    }

    /**
     * <p>Calls {@link #filledRectangle(float, float, float, float, Color)}.
     * See {@link #filledRectangle(float, float, float, float, Color)} for more information.</p>
     * @param rect a {@link Rectangle} object
     * @param color temporarily changes the ShapeDrawer's colour
     */
    public void filledRectangle(Rectangle rect, Color color) {
        filledRectangle(rect.x, rect.y, rect.width, rect.height, color);
    }


    /**
     * <p>Calls {@link #filledRectangle(float, float, float, float, float)} with rotation set to 0.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void filledRectangle(float x, float y, float width, float height) {
        filledRectangle(x, y, width, height, 0);
    }

    /**
     * <p>Sets this drawer's colour, calls {@link #filledRectangle(float, float, float, float)}, then resets the colour.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color temporarily changes the ShapeDrawer's colour
     */
    public void filledRectangle(float x, float y, float width, float height, Color color) {
        float oldColor = setColor(color);
        filledRectangle(x, y, width, height);
        setColor(oldColor);
    }

    /**
     * Draws a filled rectangle.
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param rotation the anticlockwise rotation in radians
     */
    public void filledRectangle(float x, float y, float width, float height, float rotation) {
        filledPolygonDrawer.rectangle(x, y, width, height, rotation);
    }
}
