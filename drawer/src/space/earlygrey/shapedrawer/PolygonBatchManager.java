package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.g2d.PolygonBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PolygonBatchManager extends BatchManager {

    short[] triangles;
    int triangleOffset = 0;
    static final int TRIANGLE_PUSH_SIZE = 3 * VERTEX_SIZE;

    PolygonBatchManager(PolygonBatch batch, TextureRegion region) {
        super(batch, region);
        // every 4 vertices pushed needs at most 6 triangle indices (6/4 = 1.5)
        int trianglesLength = (int) Math.ceil((verts.length / VERTEX_SIZE) * 1.5);
        triangles = new short[trianglesLength];
    }


    //Note that the constructor ensures that the Batch is a PolygonBatch.
    @Override
    @SuppressWarnings("unchecked")
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
        triangles[triangleOffset++] = (short) vertexCount;
        triangles[triangleOffset++] = (short) (vertexCount+1);
        triangles[triangleOffset++] = (short) (vertexCount+2);
        int i = getVerticesArrayIndex();
        verts[i + SpriteBatch.C1] = floatBits;
        verts[i + SpriteBatch.C2] = floatBits;
        verts[i + SpriteBatch.C3] = floatBits;
        vertexCount += 3;
    }

    void pushTriangleIndices(short t1, short t2, short t3) {
        triangles[triangleOffset++] = t1;
        triangles[triangleOffset++] = t2;
        triangles[triangleOffset++] = t3;
    }

    protected void pushVertexData(float[] vertices, short[] triangles, int trianglesCount)  {

        int i = getVerticesArrayIndex();

        for (int j = 0; j < vertices.length; j+=2) {
            float x = vertices[j], y = vertices[j+1];
            verts[i + SpriteBatch.X1] = x;
            verts[i + SpriteBatch.Y1] = y;
            verts[i + SpriteBatch.C1] = floatBits;
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
        getBatch().draw(r.getTexture(), verts, 0, getVerticesArrayIndex(), triangles, 0, triangleOffset);
        vertexCount = 0;
        triangleOffset = 0;
    }


}
