package space.earlygrey.shapedrawer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
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

    private final Batch batch;
    TextureRegion r;
    float floatBits;
    final int VERTEX_CACHE_SIZE = 2000;
    final float[] verts = new float[VERTEX_CACHE_SIZE];
    int vertexCount;

    float pixelSize = 1, halfPixelSize = 0.5f * pixelSize;
    float offset = ShapeUtils.EPSILON * pixelSize;
    float defaultLineWidth = pixelSize;
    boolean defaultSnap = false;
    boolean cacheDraws = false;

    protected static final Matrix4 mat4 = new Matrix4();

    // These are named just for clarity
    final int VERTEX_SIZE = 5, QUAD_PUSH_SIZE = 4 * VERTEX_SIZE;


    //================================================================================
    // CONSTRUCTOR
    //================================================================================

    /**
     * <p>Creates a new ShapeDrawer with the given batch and region.</p>
     * @param batch the batch used for drawing. Cannot be changed.
     * @param region the texture region used for drawing. Can be changed later.
     */

    AbstractShapeDrawer(Batch batch, TextureRegion region) {
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
     * @param lineWidth the width of the line in world units
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
        for (int i = 0; i < verts.length; i+=VERTEX_SIZE) {
            verts[i + SpriteBatch.U1] = r.getU() + 0.5f*r.getRegionWidth();
            verts[i + SpriteBatch.V1] = r.getV() + 0.5f*r.getRegionHeight();
        }
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
     * <p>Sets the colour of the ShapeDrawer. This works just like {@link Batch#setColor(Color)} though drawing is not affected by
     * the colour of the Batch.</p>
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

    /**
     *
     * @return whether drawing is currently being cached
     */
    boolean isCachingDraws() {
        return cacheDraws;
    }

    /**
     * <p>Begin caching draw calls by storing vertex information in a float[] until it all gets set to the
     * Batch with one call to {@link Batch#draw(Texture, float[], int, int)}.</p>
     * @return whether drawing was being cached before this method was called
     */
    boolean startCaching() {
        boolean wasCaching = isCachingDraws();
        this.cacheDraws = true;
        return wasCaching;
    }

    /**
     * <p>Stops caching and calls {@link Batch#draw(Texture, float[], int, int)} if anything is cached.</p>
     */
    void endCaching() {
        this.cacheDraws = false;
        if (vertexCount>0) pushToBatch();
    }


    //================================================================================
    // DRAWING METHODS
    //================================================================================

    /**
     * <p>Adds the colour and texture coordinates of four vertices to the cache and progresses the index. If drawing is
     * not currently being cached, immediately calls {@link #pushToBatch()}.</p>
     * @return whether the vertex data was pushed to the Batch
     */
    void pushQuad() {
        int i = getArrayOffset();
        /*verts[i + SpriteBatch.U1] = r.getU();
        verts[i + SpriteBatch.V1] = r.getV();
        verts[i + SpriteBatch.U2] = r.getU2();
        verts[i + SpriteBatch.V2] = r.getV();
        verts[i + SpriteBatch.U3] = r.getU2();
        verts[i + SpriteBatch.V3] = r.getV2();
        verts[i + SpriteBatch.U4] = r.getU();
        verts[i + SpriteBatch.V4] = r.getV2();*/
        verts[i + SpriteBatch.C1] = floatBits;
        verts[i + SpriteBatch.C2] = floatBits;
        verts[i + SpriteBatch.C3] = floatBits;
        verts[i + SpriteBatch.C4] = floatBits;
        vertexCount += 4;
    }

    /**
     <p>Adds the colour and texture coordinates of three vertices to the cache and progresses the index. If drawing is
     * not currently being cached, immediately calls {@link #pushToBatch()}.</p>
     * @return whether the vertex data was pushed to the Batch
     */
    void pushTriangle() {
        x4(x3());
        y4(y3());
        pushQuad();
    }

    void ensureSpaceForTriangle() {
        ensureSpace(4);
    }
    void ensureSpaceForQuad() {
        ensureSpace(4);
    }
    void ensureSpace(int vertices) {
        if (verticesRemaining() < vertices) pushToBatch();
    }

    int verticesRemaining() {
        return (verts.length - QUAD_PUSH_SIZE * vertexCount) / VERTEX_SIZE;
    }

    /**
     * <p>Calls {@link Batch#draw(Texture, float[], int, int)} using the currently cached vertex information.</p>
     */
    void pushToBatch() {
        if (vertexCount == 0) return;
        batch.draw(r.getTexture(), verts, 0, getArrayOffset());
        vertexCount = 0;
    }

    int getArrayOffset() {
        return VERTEX_SIZE * vertexCount;
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

}
