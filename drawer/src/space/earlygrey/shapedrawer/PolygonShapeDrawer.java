package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class PolygonShapeDrawer extends ShapeDrawer {

    protected final FilledPolygonDrawer filledPolygonDrawer;

    short[] triangles = new short[3000];
    int triangleOffset = 0;
    final int TRIANGLE_PUSH_SIZE = 3 * VERTEX_SIZE;

    public PolygonShapeDrawer(PolygonBatch batch, TextureRegion region) {
        super(batch, region);
        filledPolygonDrawer = new FilledPolygonDrawer(this);
    }

    @Override
    public PolygonBatch getBatch() {
        return (PolygonBatch) super.getBatch();
    }

    @Override
    protected void pushQuad() {
        int vertexOffset = vertexCount;

        triangles[triangleOffset++] = (short) (vertexOffset + 0);
        triangles[triangleOffset++] = (short) (vertexOffset + 1);
        triangles[triangleOffset++] = (short) (vertexOffset + 2);
        triangles[triangleOffset++] = (short) (vertexOffset + 0);
        triangles[triangleOffset++] = (short) (vertexOffset + 2);
        triangles[triangleOffset++] = (short) (vertexOffset + 3);

        super.pushQuad();
    }

    @Override
    protected void pushTriangle() {
        int vertexOffset = vertexCount;
        triangles[triangleOffset++] = (short) (vertexOffset + 0);
        triangles[triangleOffset++] = (short) (vertexOffset + 1);
        triangles[triangleOffset++] = (short) (vertexOffset + 2);
        int i = getArrayOffset();
        verts[i + SpriteBatch.U1] = r.getU();
        verts[i + SpriteBatch.V1] = r.getV();
        verts[i + SpriteBatch.U2] = r.getU2();
        verts[i + SpriteBatch.V2] = r.getV();
        verts[i + SpriteBatch.U3] = r.getU2();
        verts[i + SpriteBatch.V3] = r.getV2();
        verts[i + SpriteBatch.C1] = floatBits;
        verts[i + SpriteBatch.C2] = floatBits;
        verts[i + SpriteBatch.C3] = floatBits;

        vertexCount += TRIANGLE_PUSH_SIZE;
        if (!isCachingDraws() || isCacheFull()) {
            drawVerts();
        }
    }

    @Override
    protected void drawVerts() {
        if (vertexCount == 0) return;
        getBatch().draw(r.getTexture(), verts, 0, getArrayOffset(), triangles, 0, triangleOffset);
        vertexCount = 0;
        triangleOffset = 0;
    }


    //=======================================
    //          CIRCLES AND ELLIPSES
    //=======================================

    /**
     * <p>Calls {@link #filledEllipse(float, float, float, float, float)} with rotation set to 0
     * and radiusX and radiusY set to {@code radius}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius
     */
    public void filledCircle(float centreX, float centreY, float radius) {
        filledEllipse(centreX, centreY, radius, radius, 0);
    }

    /**
     * <p>Calls {@link #filledEllipse(float, float, float, float, float)} with rotation set to 0 and default line width.</p>
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
     * pixel size set. Calls {@link #polygon(float, float, int, float, float, float, JoinType)}.</p>
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
    //                 ARCS
    //=======================================

    /**
     * <p>Calls {@link #sector(float, float, float, float, float, int)} with the number of sides estimated by {@link #estimateSidesRequired(float, float)}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle at which the arc starts
     * @param radians the angle subtended by the arc
     */
    public void sector(float centreX, float centreY, float radius, float startAngle, float radians) {
        sector(centreX, centreY, radius, startAngle, radians, estimateSidesRequired(radius, radius));
    }

    /**
     * <p>Draws a sector from {@code startAngle} anti-clockwise that subtends the specified angle.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle at which the arc starts
     * @param radians the angle subtended by the arc
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
     * {@code scale}, rotation set to 0, and with the current default line width.</p>
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
     * {@code scale} and with the current default line width.</p>
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
     * <p>Draws the regular polygon speficied by drawing lines between the vertices.</p>
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
     * <p>Calls {@link #filledRectangle(Rectangle, Color)}.
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
