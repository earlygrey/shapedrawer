package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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

    public void ellipseFilled(float centreX, float centreY, float radiusX, float radiusY, float rotation) {
        filledPolygonDrawer.polygon(centreX, centreY, 80, radiusX, radiusY, rotation, 0, ShapeUtils.PI2);
    }
}
