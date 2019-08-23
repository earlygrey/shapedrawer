package space.earlygrey.shapedrawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

/**
 * <p>Contains the mechanics for using the Batch and settings such as line width and pixel size.</p>
 *
 * @author earlygrey
 */

public abstract class AbstractShapeDrawer {

    //================================================================================
    // MEMBERS
    //================================================================================

    protected final Batch batch;
    protected TextureRegion r;
    protected float floatBits;
    protected final float[] verts = new float[4000];
    protected int vertexCount;

    protected float pixelSize = 1, halfPixelSize = 0.5f * pixelSize;
    protected float offset = ShapeUtils.EPSILON * pixelSize;
    protected float defaultLineWidth = pixelSize;
    protected boolean defaultSnap = false;
    protected boolean cacheDraws = false;

    protected static final Matrix4 mat4 = new Matrix4();

    protected static final int VERTEX_SIZE = 5;
    protected static final int VERTICES_PER_PUSH = 4;
    protected static final int PUSH_SIZE = VERTICES_PER_PUSH * VERTEX_SIZE;



    //================================================================================
    // CONSTRUCTOR
    //================================================================================

    /**
     * <p>Creates a new ShapeDrawer with the given batch and region.</p>
     * @param batch the batch used for drawing. Cannot be changed.
     * @param region the texture region used for drawing. Can be changed later.
     */
    protected AbstractShapeDrawer(Batch batch, TextureRegion region) {
        this.batch = batch;
        setTextureRegion(region);
        setColor(Color.WHITE);
    }


    //================================================================================
    // UPDATE METHODS
    //================================================================================

    /**
     * <p>Call this when the batch projection or transformation matrices are changed.
     * Calls {@link #update(boolean)} with {@code updatePixelSize} set to true.</p>
     * <p>NOTE: if you are not using an orthographic projection, you should call
     * {@link #update(boolean)} with {@code updatePixelSize} set to false.</p>
     *
     */
    public void update() {
        update(true);
    }

    /**
     * <p>Call this when the batch projection or transformation matrices are changed.
     * Currently just calculates and updates the pixel size (see {@link #updatePixelSize()},
     * and only if {@code updatePixelSize} is set to true.</p>
     * @param updatePixelSize whether to call {@link #updatePixelSize()}
     */
    public void update(boolean updatePixelSize) {
        if (updatePixelSize) updatePixelSize();
    }

    /**
     * <p>This uses the current projection and transformation matrices of the Batch to calculate the
     * size of a screen pixel along the x-axis in world units, and calls {@link #setPixelSize(float)} with
     * that value. You should use this if you have changed the batch projection or transformation matrices,
     * or more generally can just call {@link #update()}.</p>
     * <p>NOTE: this only works when the projection is orthographic!</p>
     * @return the previous screen pixel size in world units
     */
    public float updatePixelSize() {
        Matrix4 trans = getBatch().getTransformMatrix(), proj = getBatch().getProjectionMatrix();
        mat4.set(proj).mul(trans);
        float scaleX = mat4.getScaleX();
        float worldWidth = 2f / scaleX;
        float newPixelSize = worldWidth / Gdx.graphics.getWidth();
        return setPixelSize(newPixelSize);
    }

    //================================================================================
    // HELPERS
    //================================================================================

    /**
     * <p>Makes a guess as to whether joins will be discernible on the screen on based on the thickness of the line.
     * This affects the default behaviour when a {@link JoinType} is unspecified.</p>
     * <p>You can override this if you want to change this behaviour.</p>
     * @param lineWidth
     * @return whether drawing joins will likely be discernible
     */
    protected boolean isJoinNecessary(float lineWidth) {
        return lineWidth > 3 * pixelSize;
    }

    protected int estimateSidesRequired(float radiusX, float radiusY) {
        float circumference = (float) (ShapeUtils.PI2 * Math.sqrt((radiusX*radiusX + radiusY*radiusY)/2f));
        int sides = (int) (circumference / (16 * pixelSize));
        float a = Math.min(radiusX, radiusY), b = Math.max(radiusX, radiusY);
        float eccentricity = (float) Math.sqrt(1-((a*a) / (b*b)));
        sides += (sides * eccentricity) / 16;
        return Math.max(sides, 20);
    }

    //================================================================================
    // GETTERS AND SETTERS
    //================================================================================

    /**
     * <p>This is used internally to make estimates about how things will appear on screen. It affects
     * line endpoint snapping (see {@link ShapeDrawer#line(float, float, float, float, float, boolean)}) and
     * estimating the number of sides required to draw an ellipse
     * (see {@link ShapeDrawer#ellipse(float, float, float, float, float, float)}).</p>
     * @param pixelSize the size of a screen pixel in world units
     * @return the previous screen pixel size in world units
     */
    public float setPixelSize(float pixelSize) {
        float oldPixelSize = this.pixelSize;
        this.pixelSize = pixelSize;
        halfPixelSize = 0.5f * pixelSize;
        offset = ShapeUtils.EPSILON * pixelSize;
        return oldPixelSize;
    }

    /**
     *
     * @return the current setting for the pixel size in world units
     */
    public float getPixelSize() {
        return pixelSize;
    }

    /**
     *
     * @return the batch this ShapeDrawer was initialised with
     */
    public Batch getBatch() {
        return batch;
    }

    /**
     *
     * @return the current TextureRegion used for drawing
     */
    public TextureRegion getRegion() {
        return r;
    }

    /**
     *
     * @return the default line width used if one is not specified in the method signature
     */
    public float getDefaultLineWidth() {
        return defaultLineWidth;
    }

    /**
     * <p>Sets the default line width used if one is not specified in the method signature.</p>
     * @param defaultLineWidth the line width to be used as a default
     * @return the previous default line width
     */
    public float setDefaultLineWidth(float defaultLineWidth) {
        float oldWidth = this.defaultLineWidth;
        this.defaultLineWidth = defaultLineWidth;
        return oldWidth;
    }

    /**
     *
     * @return whether lines are snapped to the centre of pixels if it is not specified in the method signature
     */
    public boolean isDefaultSnap() {
        return defaultSnap;
    }

    /**
     * <p>Sets whether line endpoints are snapped to the centre of pixels if it is not specified in the method signature.</p>
     * @param defaultSnap whether to snap
     * @return the previous default snap setting
     */
    public boolean setDefaultSnap(boolean defaultSnap) {
        boolean oldSnap = this.defaultSnap;
        this.defaultSnap = defaultSnap;
        return oldSnap;
    }

    /**
     * <p>Sets the TextureRegion used to draw.</p>
     * @param region the region to use
     * @return the previous texture region
     */
    public TextureRegion setTextureRegion(TextureRegion region) {
        TextureRegion oldRegion = this.r;
        this.r = region;
        return oldRegion;
    }

    /**
     * <p>Sets the colour of the ShapeDrawer. This works like {@link Batch#setColor(Color)} though drawing is not affected by
     * the colour of the Batch. Internally only the packed float value is stored, see {@link Color#toFloatBits()}.</p>
     * @param color the colour to use
     * @return the previous packed float value of the ShapeDrawer's colour
     */
    public float setColor(Color color) {
        return setColor(color.toFloatBits());
    }

    /**
     * Sets the colour of the ShapeDrawer. This works just like {@link Batch#setColor(Color)} though drawing is not affected by
     * the colour of the Batch.
     * @param floatBits the packed float value of the colour, see {@link Color#toFloatBits()}.
     * @return the previous packed float value of the ShapeDrawer's colour
     */
    public float setColor(float floatBits) {
        float oldColor = getPackedColor();
        this.floatBits = floatBits;
        return oldColor;
    }

    /**
     *
     * @return the packed colour of this ShapeDrawer
     */
    public float getPackedColor() {
        return floatBits;
    }


    //================================================================================
    // DRAWING METHODS
    //================================================================================

    public boolean isCachingDraws() {
        return cacheDraws;
    }

    public void startCaching() {
        this.cacheDraws = true;
    }

    public void endCaching() {
        this.cacheDraws = false;
        if (vertexCount>0) drawVerts();
    }

    protected void pushVerts() {
        verts[getArrayOffset() + SpriteBatch.U1] = r.getU();
        verts[getArrayOffset() + SpriteBatch.V1] = r.getV();
        verts[getArrayOffset() + SpriteBatch.U2] = r.getU2();
        verts[getArrayOffset() + SpriteBatch.V2] = r.getV();
        verts[getArrayOffset() + SpriteBatch.U3] = r.getU2();
        verts[getArrayOffset() + SpriteBatch.V3] = r.getV2();
        verts[getArrayOffset() + SpriteBatch.U4] = r.getU();
        verts[getArrayOffset() + SpriteBatch.V4] = r.getV2();
        verts[getArrayOffset() + SpriteBatch.C1] = floatBits;
        verts[getArrayOffset() + SpriteBatch.C2] = floatBits;
        verts[getArrayOffset() + SpriteBatch.C3] = floatBits;
        verts[getArrayOffset() + SpriteBatch.C4] = floatBits;
        vertexCount += VERTICES_PER_PUSH;
        if (!isCachingDraws() || verts.length - PUSH_SIZE < PUSH_SIZE * vertexCount) {
            drawVerts();
        }
    }

    protected void drawVerts() {
        if (vertexCount == 0) return;
        batch.draw(r.getTexture(), verts, 0, getArrayOffset());
        //System.out.println(vertexCount);
        vertexCount = 0;
    }

    protected void x1(float x1){verts[getArrayOffset() + SpriteBatch.X1] = x1;}
    protected void y1(float y1){verts[getArrayOffset() + SpriteBatch.Y1] = y1;}
    protected void x2(float x2){verts[getArrayOffset() + SpriteBatch.X2] = x2;}
    protected void y2(float y2){verts[getArrayOffset() + SpriteBatch.Y2] = y2;}
    protected void x3(float x3){verts[getArrayOffset() + SpriteBatch.X3] = x3;}
    protected void y3(float y3){verts[getArrayOffset() + SpriteBatch.Y3] = y3;}
    protected void x4(float x4){verts[getArrayOffset() + SpriteBatch.X4] = x4;}
    protected void y4(float y4){verts[getArrayOffset() + SpriteBatch.Y4] = y4;}
    protected float x1() {return verts[getArrayOffset() + SpriteBatch.X1];}
    protected float y1() {return verts[getArrayOffset() + SpriteBatch.Y1];}
    protected float x2() {return verts[getArrayOffset() + SpriteBatch.X2];}
    protected float y2() {return verts[getArrayOffset() + SpriteBatch.Y2];}
    protected float x3() {return verts[getArrayOffset() + SpriteBatch.X3];}
    protected float y3() {return verts[getArrayOffset() + SpriteBatch.Y3];}
    protected float x4() {return verts[getArrayOffset() + SpriteBatch.X4];}
    protected float y4() {return verts[getArrayOffset() + SpriteBatch.Y4];}

    protected int getArrayOffset() {
        return VERTEX_SIZE * vertexCount;
    }

}
