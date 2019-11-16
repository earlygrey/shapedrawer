package space.earlygrey.shapedrawer;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;

/**
 * <p>Uses a Batch to draw lines, outlined shapes and paths. Meant to be an analogue of {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer}
 * but uses a Batch instead of an {@link com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer}, so that it can be used
 * in between {@link Batch#begin()} and {@link Batch#end()}.</p>
 * <p>Line mitering can be performed when drawing Polygons and Paths, see {@link JoinType} for options.</p>
 * <p>Also includes an option to snap lines to the centre of pixels, see {@link #line(float, float, float, float, float, boolean)}
 * for more information.</p>
 * <p>Uses the projection matrix of the supplied Batch so there is no need to set one as with {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer}.</p>
 * <p>Note that the way filled shapes are drawn depends on whether you provide a batch implementing {@link com.badlogic.gdx.graphics.g2d.PolygonBatch}
 * (eg a {@link com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch}) or not (eg a {@link com.badlogic.gdx.graphics.g2d.SpriteBatch}).
 * Filled polygon drawing is more efficient with a PolygonBatch, and if possible this is recommended.</p>
 *
 * @author earlygrey
 */

public class ShapeDrawer extends AbstractShapeDrawer {

    //================================================================================
    // CONSTRUCTOR
    //================================================================================

    public ShapeDrawer(Batch batch, TextureRegion region) {
        super(batch, region);

    }


    //================================================================================
    // DRAWING METHODS
    //================================================================================

    //=======================================
    //                LINES
    //=======================================

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code lineWidth} set to
     * the current default and {@code snap} set to true.</p>
     * @param s the starting point of the line
     * @param e the end point of the line
     */
    public void line(Vector2 s, Vector2 e) {
        line(s.x, s.y, e.x, e.y, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code snap} set to true.</p>
     * @param s the starting point of the line
     * @param e the end point of the line
     * @param lineWidth the width of the line in world units
     */
    public void line(Vector2 s, Vector2 e, float lineWidth) {
        line(s.x, s.y, e.x, e.y, lineWidth);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code snap} set to true.</p>
     * @param s the starting point of the line
     * @param e the end point of the line
     * @param color temporarily changes the ShapeDrawer's colour
     */
    public void line(Vector2 s, Vector2 e, Color color) {
        float c = setColor(color);
        line(s.x, s.y, e.x, e.y, defaultLineWidth);
        setColor(c);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code snap} set to true.</p>
     * @param s the starting point of the line
     * @param e the end point of the line
     * @param color temporarily changes the ShapeDrawer's colour
     * @param lineWidth the width of the line in world units
     */
    public void line(Vector2 s, Vector2 e, Color color, float lineWidth) {
        float c = setColor(color);
        line(s.x, s.y, e.x, e.y, color, lineWidth);
        setColor(c);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code lineWidth} set to
     * the current default and {@code snap} set to true.</p>
     * @param x1 the x-component of the first point
     * @param y1 the y-component of the first point
     * @param x2 the x-component of the second point
     * @param y2 the y-component of the second point
     */
    public void line(float x1, float y1, float x2, float y2) {
        line(x1, y1, x2, y2, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code lineWidth} set to
     * the current default and {@code snap} set to true.</p>
     * @param x1 the x-component of the first point
     * @param y1 the y-component of the first point
     * @param x2 the x-component of the second point
     * @param y2 the y-component of the second point
     * @param color temporarily changes the ShapeDrawer's colour
     */
    public void line(float x1, float y1, float x2, float y2, Color color) {
        line(x1, y1, x2, y2, color, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code snap} set to true.</p>
     * @param x1 the x-component of the first point
     * @param y1 the y-component of the first point
     * @param x2 the x-component of the second point
     * @param y2 the y-component of the second point
     * @param color temporarily changes the ShapeDrawer's colour
     * @param lineWidth the width of the line in world units
     */
    public void line(float x1, float y1, float x2, float y2, Color color, float lineWidth) {
        float c = setColor(color);
        line(x1, y1, x2, y2, lineWidth);
        setColor(c);
    }

    /**
     * <p>Calls {@link #line(float, float, float, float, float, boolean)}()} with {@code snap} set to true.</p>
     * @param x1 the x-component of the first point
     * @param y1 the y-component of the first point
     * @param x2 the x-component of the second point
     * @param y2 the y-component of the second point
     * @param lineWidth the width of the line in world units
     */
    public void line(float x1, float y1, float x2, float y2, float lineWidth) {
        line(x1, y1, x2, y2, lineWidth, defaultSnap);
    }

    /**
     *
     * <p>Draws a line between (x1, y1) and (x2, y2) with width {@code lineWidth}. The edges of the line are centred at
     * (x1, y1) and (x2, y2).</p>
     * <p>If {@code snap} is true, the start and end
     * points will be snapped to the centre of their respective pixels, and then offset very slightly so that the line
     * is guaranteed to contain the centre of the pixel. This is important when pixel perfect precision
     * is necessary, such as when drawing to a low resolution frame buffer.</p>
     * <p>This is the most performant method for drawing a line.</p>
     *
     * @param x1 the x-component of the first point
     * @param y1 the y-component of the first point
     * @param x2 the x-component of the second point
     * @param y2 the y-component of the second point
     * @param lineWidth the width of the line in world units
     * @param snap whether to snap the start and end coordinates to the centre of the pixel
     */
    public void line(float x1, float y1, float x2, float y2, float lineWidth, boolean snap) {
        line(x1, y1, x2, y2, lineWidth, snap, batchManager.floatBits, batchManager.floatBits);
    }

    public void line(Vector2 s, Vector2 e, Color color1, Color color2) {
        line(s.x, s.y, e.x, e.y, color1, color2);
    }

    public void line(float x1, float y1, float x2, float y2, Color color1, Color color2) {
        line(x1, y1, x2, y2, getDefaultLineWidth(), isDefaultSnap(), color1, color2);
    }

    public void line(float x1, float y1, float x2, float y2, float lineWidth, Color color1, Color color2) {
        line(x1, y1, x2, y2, lineWidth, isDefaultSnap(), color1, color2);
    }

    public void line(float x1, float y1, float x2, float y2, float lineWidth, boolean snap, Color color1, Color color2) {
        line(x1, y1, x2, y2, lineWidth, snap, color1.toFloatBits(), color2.toFloatBits());
    }

    public void line(float x1, float y1, float x2, float y2, float lineWidth, boolean snap, float color1, float color2) {
        lineDrawer.line(x1, y1, x2, y2, lineWidth, snap, color1, color2);
    }

    //=======================================
    //                PATHS
    //=======================================

    /**
     * <p>Calls {@link #path(Array, float)} with {@code lineWidth} set to the current default.</p>
     * @param path an ordered Array of Vector2s representing path points
     */
    public void path(Array<Vector2> path) {
        path(path, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #path(Array, float, JoinType, boolean)} with open set to true and {@code lineWidth}
     * set to the current default.</p>
     * @param path an ordered Array of Vector2s representing path points
     * @param joinType the type of join, see {@link JoinType}
     */
    public void path(Array<Vector2> path, JoinType joinType) {
        path(path, defaultLineWidth, joinType, false);
    }

    /**
     * <p>Calls {@link #path(Array, float, boolean)} with open set to true.</p>
     * @param path an ordered Array of Vector2s representing path points
     * @param lineWidth the width of each line in world units
     */
    public void path(Array<Vector2> path, float lineWidth) {
        path(path, lineWidth, false);
    }

    /**
     *
     * @param path an ordered Array of Vector2s representing path points
     * @param joinType the type of join, see {@link JoinType}
     * @param open if false then the first and last points are connected
     *
     */
    public void path(Array<Vector2> path,JoinType joinType, boolean open) {
        path(path, defaultLineWidth, joinType, open);
    }

    /**
     * <p>Calls {@link #path(Array, float, JoinType, boolean)} with {@code joinType} set to {@link JoinType#SMOOTH}
     *  (also see {@link #isJoinNecessary(float)}).</p>
     * @param path an ordered Array of Vector2s representing path points
     * @param lineWidth the width of each line in world units
     * @param open if false then the first and last points are connected
     */
    public void path(Array<Vector2> path, float lineWidth, boolean open) {
        path(path, lineWidth, isJoinNecessary(lineWidth)?JoinType.SMOOTH:JoinType.NONE, open);
    }

    /**
     * <p>Draws a path by drawing a line between each point and the next.</p>
     * <p>The points at which two lines connect can be mitered to give a smooth join, see {@link JoinType} for the types of mitre.
     * Note that this may cause strange looking joins when the angle between connected lines approaches &pi;, as the miter
     * can get arbitratily long. For thin lines where the mitre cannot be seen, you can set {@code joinType} to {@link JoinType#NONE}.</p>
     * <p>Only a subset of the path containing unique consecutive points (up to some small error) will be considerered.
     * For example, the paths [(0,0), (1.0001,1), (1,1), (2,2)] and [(0,0), (1,1), (2,2)] will be drawn identically. </p>
     * <p>If {@code path} is empty nothing will be drawn, if it contains two points {@link #line(float, float, float, float, float, boolean)}
     * will be used.</p>
     * @param path an {@code Array<Vector2>} containing the ordered points in the path
     * @param lineWidth the width of each line in world units
     * @param joinType see {@link JoinType} the type of join, see method description
     * @param open if false then the first and last points are connected
     */
    public void path(Array<Vector2> path, float lineWidth, JoinType joinType, boolean open) {
        pathDrawer.path(path, lineWidth, joinType, open);
    }

    /**
     * <p>Draws a path by drawing a line between each point and the next. See {@link #path(Array, float, JoinType, boolean)} for details.</p>
     * @param path an {@link FloatArray} containing the ordered points in the path
     * @param lineWidth the width of each line in world units
     * @param joinType see {@link JoinType} the type of join, see method description
     * @param open if false then the first and last points are connected
     */
    public void path(FloatArray path, float lineWidth, JoinType joinType, boolean open) {
        pathDrawer.path(path, lineWidth, joinType, open);
    }


    /**
     * <p>Draws a path by drawing a line between each point and the next. See {@link #path(Array, float, JoinType, boolean)} for details.</p>
     * @param path an {@code float[]} containing the ordered points in the path
     * @param lineWidth the width of each line in world units
     * @param joinType see {@link JoinType} the type of join, see method description
     * @param open if false then the first and last points are connected
     */
    public void path(float[] path, float lineWidth, JoinType joinType, boolean open) {
        pathDrawer.path(path, lineWidth, joinType, open);
    }

    //=======================================
    //          CIRCLES AND ELLIPSES
    //=======================================

    //====================
    //      OUTLINED
    //====================

    /**
     * <p>Calls {@link #circle(float, float, float, float)} with default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius
     */
    public void circle(float centreX, float centreY, float radius) {
        circle(centreX, centreY, radius, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #circle(float, float, float, float, JoinType)} with default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius
     * @param joinType the type of join, see {@link JoinType}
     */
    public void circle(float centreX, float centreY, float radius, JoinType joinType) {
        circle(centreX, centreY, radius, defaultLineWidth, joinType);
    }

    /**
     * <p>Calls {@link #circle(float, float, float, JoinType)} with joinType set to {@link JoinType#SMOOTH}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius
     * @param lineWidth the width of the line in world units
     */
    public void circle(float centreX, float centreY, float radius, float lineWidth) {
        circle(centreX, centreY, radius, lineWidth, isJoinNecessary(lineWidth)?JoinType.SMOOTH:JoinType.NONE);
    }

    /**
     * <p>Calls {@link #ellipse(float, float, float, float, float, float, JoinType)} with rotation set to 0
     * and radiusX and radiusY set to {@code radius}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius
     * @param lineWidth the width of the line in world units
     * @param joinType the type of join, see {@link JoinType}
     */
    public void circle(float centreX, float centreY, float radius, float lineWidth, JoinType joinType) {
        ellipse(centreX, centreY, radius, radius, 0, lineWidth, joinType);
    }

    /**
     * <p>Calls {@link #ellipse(float, float, float, float, float, float)} with rotation set to 0 and default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radiusX the radius along the x-axis
     * @param radiusY the radius along the y-axis
     */
    public void ellipse(float centreX, float centreY, float radiusX, float radiusY) {
        ellipse(centreX, centreY, radiusX, radiusY, 0, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #ellipse(float, float, float, float, float, float)} with default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radiusX the radius along the x-axis
     * @param radiusY the radius along the y-axis
     * @param rotation the anticlockwise rotation in radians
     */
    public void ellipse(float centreX, float centreY, float radiusX, float radiusY, float rotation) {
        ellipse(centreX, centreY, radiusX, radiusY, rotation, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #ellipse(float, float, float, float, float, float, JoinType)} with joinType set to {@link JoinType#SMOOTH}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radiusX the radius along the x-axis
     * @param radiusY the radius along the y-axis
     * @param rotation the anticlockwise rotation in radians
     * @param lineWidth the width of the line in world units
     */
    public void ellipse(float centreX, float centreY, float radiusX, float radiusY, float rotation, float lineWidth) {
        ellipse(centreX, centreY, radiusX, radiusY, rotation, lineWidth, isJoinNecessary(lineWidth)?JoinType.SMOOTH:JoinType.NONE);
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
     * @param lineWidth the width of the line in world units
     * @param joinType the type of join, see {@link JoinType}
     */
    public void ellipse(float centreX, float centreY, float radiusX, float radiusY, float rotation, float lineWidth, JoinType joinType) {
        polygon(centreX, centreY, estimateSidesRequired(radiusX, radiusY), radiusX, radiusY, rotation, lineWidth, joinType);
    }

    //====================
    //   OUTLINED (ARCS)
    //====================


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
    //          PARTIAL ELLIPSES
    //=======================================

    //====================
    //   OUTLINED (ARCS)
    //====================

    /**
     * <p>Calls {@link #arc(float, float, float, float, float, float)} with default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle in radians at which the arc starts
     * @param radians the angle in radians subtended by the arc
     */
    public void arc(float centreX, float centreY, float radius, float startAngle, float radians) {
        arc(centreX, centreY, radius, startAngle, radians, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #arc(float, float, float, float, float, float, boolean)} with useJoin set to true.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle in radians at which the arc starts
     * @param radians the angle in radians subtended by the arc
     * @param lineWidth the width of the line
     */
    public void arc(float centreX, float centreY, float radius, float startAngle, float radians, float lineWidth) {
        arc(centreX, centreY, radius, startAngle, radians, lineWidth, true);
    }

    /**
     * <p>Calls {@link #arc(float, float, float, float, float, float, boolean, int)} with the number of sides estimated by {@link #estimateSidesRequired(float, float)}.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle in radians at which the arc starts
     * @param radians the angle in radians subtended by the arc
     * @param lineWidth the width of the line
     * @param useJoin whether to use a join type, either {@link JoinType#POINTY} or none. See {@link #isJoinNecessary(float)}
     */
    public void arc(float centreX, float centreY, float radius, float startAngle, float radians, float lineWidth, boolean useJoin) {
        arc(centreX, centreY, radius, startAngle, radians, lineWidth, useJoin, estimateSidesRequired(radius, radius));
    }

    /**
     * <p>Draws an arc from {@code startAngle} anti-clockwise that subtends the specified angle.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param radius the radius of the circle that this arc is a part of
     * @param startAngle the angle in radians at which the arc starts
     * @param radians the angle in radians subtended by the arc
     * @param lineWidth the width of the line
     * @param useJoin whether to use a join type, either {@link JoinType#POINTY} or none. See {@link #isJoinNecessary(float)}
     * @param sides the number of straight line segments to draw the arc with
     */
    public void arc(float centreX, float centreY, float radius, float startAngle, float radians, float lineWidth, boolean useJoin, int sides) {
        JoinType joinType = (useJoin && isJoinNecessary(lineWidth))?JoinType.POINTY:JoinType.NONE;
        polygonDrawer.polygon(centreX, centreY, sides, radius, radius, 0, lineWidth, joinType, startAngle, radians);
    }

    //====================
    // FILLED (PIE SLICES)
    //====================

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

    //====================
    //     OUTLINED
    //====================

    /**
     * <p>Calls {@link #polygon(float, float, int, float, float, float, float)} with scaleX and scaleY set to
     * {@code scale}, rotation set to 0, and with the current default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param scale the scale
     */
    public void polygon(float centreX, float centreY, int sides, float scale) {
        polygon(centreX, centreY, sides, scale, scale, 0, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #polygon(float, float, int, float, float, float, float)} with scaleX and scaleY set to
     * {@code scale} and with the current default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param radius the radius
     * @param rotation the anticlockwise rotation in radians
     */
    public void polygon(float centreX, float centreY, int sides, float radius, float rotation) {
        polygon(centreX, centreY, sides, radius, radius, rotation, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #polygon(float, float, int, float, float, float, float, JoinType)}
     * with the current default line width and
     * with joinType set to {@link JoinType#POINTY} (also see {@link #isJoinNecessary(float)}).</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param rotation the rotation in radians after scaling
     * @param scaleX the scale along the x-axis
     * @param scaleY the scale along the y-axis
     */
    public void polygon(float centreX, float centreY, int sides, float scaleX, float scaleY, float rotation) {
        polygon(centreX, centreY, sides, scaleX, scaleY, rotation, defaultLineWidth, isJoinNecessary(defaultLineWidth)?JoinType.POINTY:JoinType.NONE);
    }

    /**
     * <p>Calls {@link #polygon(float, float, int, float, float, float, float, JoinType)}
     * with joinType set to {@link JoinType#POINTY} (also see {@link #isJoinNecessary(float)}).</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param lineWidth the width of the line in world units
     * @param rotation the rotation in radians after scaling
     * @param scaleX the scale along the x-axis
     * @param scaleY the scale along the y-axis
     */
    public void polygon(float centreX, float centreY, int sides, float scaleX, float scaleY, float rotation, float lineWidth) {
        polygon(centreX, centreY, sides, scaleX, scaleY, rotation, lineWidth, isJoinNecessary(lineWidth)?JoinType.POINTY:JoinType.NONE);
    }

    /**
     * <p>Calls {@link #polygon(float, float, int, float, float, float, float, JoinType)} with the current default line width.</p>
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param rotation the rotation in radians after scaling
     * @param scaleX the scale along the x-axis
     * @param scaleY the scale along the y-axis
     * @param joinType the type of join, see {@link JoinType}
     */
    public void polygon(float centreX, float centreY, int sides, float scaleX, float scaleY, float rotation, JoinType joinType) {
        polygon(centreX, centreY, sides, scaleX, scaleY, rotation, defaultLineWidth, joinType);
    }

    /**
     * <p>Draws the regular polygon specified by drawing lines between the vertices.</p>
     *
     * @param centreX the x-coordinate of the centre point
     * @param centreY the y-coordinate of the centre point
     * @param sides the number of sides
     * @param scaleX the scale along the x-axis
     * @param scaleY the scale along the y-axis
     * @param rotation the rotation in radians after scaling
     * @param lineWidth the width of the line in world units
     * @param joinType the type of join, see {@link JoinType}
     */
    public void polygon(float centreX, float centreY, int sides, float scaleX, float scaleY, float rotation, float lineWidth, JoinType joinType) {
        polygonDrawer.polygon(centreX, centreY, sides, scaleX, scaleY, rotation, lineWidth, joinType, 0, ShapeUtils.PI2);
    }

    //====================
    //     FILLED
    //====================

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

    //====================
    //     OUTLINED
    //====================


    /**
     * <p>Calls {@link #polygon(Polygon, float, JoinType)} with default line width and join type set to {@link JoinType#POINTY}.</p>
     * @param polygon the polygon
     */
    public void polygon(Polygon polygon) {
        polygon(polygon, defaultLineWidth, isJoinNecessary(defaultLineWidth)?JoinType.POINTY:JoinType.NONE);
    }

    /**
     * <p>Calls {@link #polygon(Polygon, float, JoinType)} with default line width and join type set to {@link JoinType#POINTY}.</p>
     * @param vertices consecutive ordered pairs of the x-y coordinates of the vertices of the polygon
     */
    public void polygon(float[] vertices) {
        polygon(vertices, defaultLineWidth, isJoinNecessary(defaultLineWidth)?JoinType.POINTY:JoinType.NONE);
    }

    /**
     * <p>Calls {@link #polygon(Polygon, float, JoinType)} with join type set to {@link JoinType#POINTY}.</p>
     * @param polygon the polygon
     * @param lineWidth the line width
     */
    public void polygon(Polygon polygon, float lineWidth) {
        polygon(polygon, lineWidth, isJoinNecessary(defaultLineWidth)?JoinType.POINTY:JoinType.NONE);
    }

    /**
     * <p>Calls {@link #polygon(Polygon, float, JoinType)} with default line width.</p>
     * @param polygon the polygon
     * @param joinType the type of join, see {@link JoinType}
     */
    public void polygon(Polygon polygon, JoinType joinType) {
        polygon(polygon, defaultLineWidth, joinType);
    }

    /**
     * <p>Draws the boundary of the polygon with the given line width and join type.</p>
     * @param polygon the polygon
     * @param lineWidth the line width
     * @param joinType the type of join, see {@link JoinType}
     */
    public void polygon(Polygon polygon, float lineWidth, JoinType joinType) {
        polygon(polygon.getTransformedVertices(), lineWidth, joinType);
    }

    /**
     * <p>Draws the boundary of the polygon with the given line width and join type.</p>
     * @param vertices consecutive ordered pairs of the x-y coordinates of the vertices of the polygon
     * @param lineWidth the line width
     * @param joinType the type of join, see {@link JoinType}
     */
    public void polygon(float[] vertices, float lineWidth, JoinType joinType) {
        pathDrawer.path(vertices, 0, vertices.length, lineWidth, joinType, false);
    }


    //====================
    //     FILLED
    //====================


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

    public void filledTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
        filledPolygonDrawer.triangle(x1, y1, x2, y2, x3, y3);
    }


    //=======================================
    //              RECTANGLES
    //=======================================

    //====================
    //     OUTLINED
    //====================

    /**
     * <p>Calls {@link #rectangle(Rectangle)} with the current default line width.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param rect a {@link Rectangle} object
     */
    public void rectangle(Rectangle rect) {
        rectangle(rect, defaultLineWidth);
    }
    /**
     * <p>Calls {@link #rectangle(Rectangle, Color, float)} with the current default line width.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param rect a {@link Rectangle} object
     * @param color temporarily changes the ShapeDrawer's colour
     */
    public void rectangle(Rectangle rect, Color color) {
        rectangle(rect, color, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, float)}.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param rect a {@link Rectangle} object
     * @param lineWidth the width of the line in world units
     */
    public void rectangle(Rectangle rect, float lineWidth) {
        rectangle(rect.x, rect.y, rect.width, rect.height, lineWidth);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, Color, float)}.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param rect a {@link Rectangle} object
     * @param color temporarily changes the ShapeDrawer's colour
     * @param lineWidth the width of the line in world units
     */
    public void rectangle(Rectangle rect, Color color, float lineWidth) {
        rectangle(rect.x, rect.y, rect.width, rect.height, color, lineWidth);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, float)} with the current default line width.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     */
    public void rectangle(float x, float y, float width, float height) {
        rectangle(x, y, width, height, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, Color, float)} with the current default line width.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color temporarily changes the ShapeDrawer's colour
     */
    public void rectangle(float x, float y, float width, float height, Color color) {
        rectangle(x, y, width, height, color, defaultLineWidth);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, float, JoinType)} with joinType set to {@link JoinType#POINTY}.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param lineWidth the width of the line in world units
     */
    public void rectangle(float x, float y, float width, float height, float lineWidth) {
        rectangle(x, y, width, height, lineWidth, JoinType.POINTY);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, float)}.
     * See {@link #rectangle(float, float, float, float, float, JoinType)} for more information.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color temporarily changes the ShapeDrawer's colour
     * @param lineWidth the width of the line in world units
     */
    public void rectangle(float x, float y, float width, float height, Color color, float lineWidth) {
        float oldColor = setColor(color);
        rectangle(x, y, width, height, lineWidth);
        setColor(oldColor);
    }

    /**
     * <p>Calls {@link #rectangle(float, float, float, float, float, float, JoinType)} with rotation set to 0.</p>
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param lineWidth the width of the line in world units
     * @param joinType see {@link JoinType}
     */
    public void rectangle(float x, float y, float width, float height, float lineWidth, JoinType joinType) {
        rectangle(x, y, width, height, lineWidth, 0, joinType);
    }

    /**
     * Draws a rectangle. See {@link JoinType} for join types.
     * @param x the x-coordinate of the bottom left corner of the rectangle
     * @param y the y-coordinate of the bottom left corner of the rectangle
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param lineWidth the width of the line in world units
     * @param rotation the anticlockwise rotation in radians
     * @param joinType see {@link JoinType}
     */
    public void rectangle(float x, float y, float width, float height, float lineWidth, float rotation, JoinType joinType) {
        if (joinType==JoinType.POINTY && rotation==0) {
            float halfWidth = 0.5f*lineWidth;
            float X = x+width, Y = y+height;
            lineDrawer.line(x+halfWidth, y, X-halfWidth, y, lineWidth, false);//bottom
            lineDrawer.line(x+halfWidth, Y, X-halfWidth, Y, lineWidth, false);//top
            lineDrawer.line(x, y-halfWidth, x, Y+halfWidth, lineWidth, false);//left
            lineDrawer.line(X, y-halfWidth, X, Y+halfWidth, lineWidth, false);//right
        } else {
            polygon(x + 0.5f*width, y + 0.5f*height, 4, lineWidth, rotation + ShapeUtils.PI_4, width, height, joinType);
        }
    }



    //====================
    //     FILLED
    //====================


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
