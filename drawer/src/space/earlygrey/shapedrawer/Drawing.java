package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;

/**
 * Holds a cache of shapes drawn with a {@link ShapeDrawer}, which can be drawn at any time without having to perform the shape calculations again.
 * @author earlygrey
 */

public class Drawing {

    BatchManager batchManager;
    Array<float[]> vertexBatches;

    Drawing(BatchManager batchManager) {
        this.batchManager = batchManager;
        vertexBatches = new Array<float[]>();
    }

    /**
     * Draw the cached shapes to the screen. Must be used in between {@link Batch#begin()} and {@link Batch#end()}.
     */
    public void draw() {
        if (batchManager.r == null) throw new IllegalStateException("The texture region is null. Please set a texture region first (e.g. in the constructor or by calling setTextureRegion(TextureRegion region))");        
        for (int i = 0; i < vertexBatches.size; i++) {
            float[] vertices = vertexBatches.get(i);
            getBatchManager().getBatch().draw(batchManager.r.getTexture(), vertices, 0, vertices.length);
        }
    }

    void pushVertices() {
        float[] vertices = new float[getBatchManager().getVerticesArrayIndex()];
        System.arraycopy(getBatchManager().verts, 0, vertices, 0, vertices.length);
        vertexBatches.add(vertices);
    }

    void finalise() {
        vertexBatches.setSize(vertexBatches.size);
    }

    BatchManager getBatchManager() {
        return batchManager;
    }

    static class PolygonDrawing extends Drawing {

        Array<short[]> triangleBatches;

        PolygonDrawing(PolygonBatchManager batchManager) {
            super(batchManager);
            triangleBatches = new Array<short[]>();
        }

        @Override
        public void draw() {
            if (batchManager.r == null) throw new IllegalStateException("The texture region is null. Please set a texture region first (e.g. in the constructor or by calling setTextureRegion(TextureRegion region))");
            for (int i = 0; i < vertexBatches.size; i++) {
                float[] vertices = vertexBatches.get(i);
                short[] triangles = triangleBatches.get(i);
                getBatchManager().getBatch().draw(batchManager.r.getTexture(), vertices, 0, vertices.length, triangles, 0, triangles.length);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        PolygonBatchManager getBatchManager() {
            return (PolygonBatchManager) super.getBatchManager();
        }

        @Override
        void pushVertices() {
            super.pushVertices();
            short[] triangles = new short[getBatchManager().getTrianglesArrayOffset()];
            System.arraycopy(getBatchManager().triangles, 0, triangles, 0, triangles.length);
            triangleBatches.add(triangles);
        }

        @Override
        void finalise() {
            super.finalise();
            triangleBatches.setSize(triangleBatches.size);
        }
    }

}
