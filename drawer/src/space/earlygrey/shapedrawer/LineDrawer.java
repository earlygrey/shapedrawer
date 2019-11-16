package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;

/**
 * <p>Contains functions for calculating vertex data for drawing individual lines.</p>
 *
 * @author earlygrey
 */

class LineDrawer extends DrawerTemplate<BatchManager> {

    LineDrawer(BatchManager batchManager, AbstractShapeDrawer drawer) {
        super(batchManager, drawer);
    }

    void line(float x1, float y1, float x2, float y2, float lineWidth, boolean snap) {
        pushLine(x1, y1, x2, y2, lineWidth, snap);
        batchManager.pushToBatch();
    }

    void pushLine(float x1, float y1, float x2, float y2, float lineWidth, boolean snap) {
        pushLine(x1, y1, x2, y2, lineWidth, snap, batchManager.floatBits, batchManager.floatBits);
    }

    void pushLine(float x1, float y1, float x2, float y2, float lineWidth, boolean snap, float c1, float c2) {

        batchManager.ensureSpaceForQuad();

        // dif=(xdif,ydif) is the vector going from (x1, y1) to the first vertex going clockwise around the border of the line.
        // l=(lx,ly) is the vector from (x1, y1) to (x2, y2)
        float xdif = 0, ydif = 0, lx = x2 - x1, ly = y2 - y1;

        if (snap) {
            /*
            First "snap" the given coords so they're right in the centre of the pixel,
            then extend the line very slightly in each direction, so it covers the centre of the start and end pixels.
            This is because nearest neighbour just takes the colour from right in the centre.
            */
            float offset = batchManager.offset;
            float pixelSize = batchManager.pixelSize, halfPixelSize = batchManager.halfPixelSize;
            x1 = ShapeUtils.snap(x1, pixelSize, halfPixelSize) - Math.signum(lx) * offset;
            y1 = ShapeUtils.snap(y1, pixelSize, halfPixelSize) - Math.signum(ly) * offset;
            x2 = ShapeUtils.snap(x2, pixelSize, halfPixelSize) + Math.signum(lx) * offset;
            y2 = ShapeUtils.snap(y2, pixelSize, halfPixelSize) + Math.signum(ly) * offset;
        }

        float halfLineWidth = 0.5f * lineWidth;

        if (x1==x2) {
            xdif = halfLineWidth;
        } else if (y1==y2) {
            ydif = halfLineWidth;
        } else {
            xdif = y2 - y1; //coordinates swapped as rotating PI/2 degrees
            ydif = x2 - x1;
            float l2 = xdif*xdif + ydif*ydif; // squared length of the vector l
            float invl = 1f/ (float) Math.sqrt(l2); // inverse length of line
            float s = invl * halfLineWidth; //s is a scalar used to normalise and scale to halfLineWidth
            xdif *= s;
            ydif *= s;
        }

        x1(x1+xdif);
        y1(y1-ydif);
        x2(x1-xdif);
        y2(y1+ydif);
        x3(x2-xdif);
        y3(y2+ydif);
        x4(x2+xdif);
        y4(y2-ydif);

        color1(c1);
        color2(c1);
        color3(c2);
        color4(c2);

        batchManager.pushQuad();
        if (!batchManager.isCachingDraws()) batchManager.pushToBatch();
    }


}
