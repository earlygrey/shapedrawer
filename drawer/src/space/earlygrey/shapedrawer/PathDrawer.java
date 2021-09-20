package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntMap;

/**
 * <p>Contains functions for calculating the vertex data for lines in a path with various joins.</p>
 *
 * @author earlygrey
 */

class PathDrawer extends DrawerTemplate<BatchManager> {

    FloatArray path = new FloatArray();
    FloatArray tempPath = new FloatArray();

    static final Vector2 D0 = new Vector2(), E0 = new Vector2();

    PathDrawer(BatchManager batchManager, AbstractShapeDrawer drawer) {
        super(batchManager, drawer);
    }

    <T extends Vector2> void path(Array<T> userPath, float lineWidth, JoinType joinType) {
        path(userPath, lineWidth, lineWidth, joinType, true);
    }

    <T extends Vector2> void path(Array<T> userPath, float startWidth, float endWidth, JoinType joinType, boolean open) {
        for (int i = 0; i < userPath.size; i++) {
            Vector2 v = userPath.get(i);
            tempPath.add(v.x, v.y);
        }

        path(tempPath.items, 0, tempPath.size, startWidth, endWidth, joinType, open);
        tempPath.clear();
    }

    void path (FloatArray userPath, float lineWidth, JoinType joinType, boolean open) {
        path (userPath.items, 0, userPath.size, lineWidth, lineWidth, joinType, open);
    }

    void path (float[] userPath, float lineWidth, JoinType joinType) {
        path (userPath, 0, userPath.length, lineWidth, joinType);
    }

    void path (float[] userPath, float lineWidth, JoinType joinType, boolean open) {
        path (userPath, 0, userPath.length, lineWidth, lineWidth, joinType, open);
    }

    void path (float[] userPath, int start, int end, float lineWidth, JoinType joinType) {
        path(userPath, start, end, lineWidth, lineWidth, joinType, true);
    }

    void path (float[] userPath, int start, int end, float startWidth, float endWidth, JoinType joinType, boolean open) {
        if (userPath.length < 4) return;

        //construct new path consisting of unique consecutive points
        path.add(userPath[start]);
        path.add(userPath[start+1]);
        for(int i = start+2; i < end; i+=2) {
            if (!ShapeUtils.epsilonEquals(userPath[i-2], userPath[i]) || !ShapeUtils.epsilonEquals(userPath[i-1], userPath[i+1])) {
                path.add(userPath[i], userPath[i+1]);
            }
        }
        if (path.size < 4) {
            path.clear();
            return;
        }

        boolean wasCaching = batchManager.startCaching();
        
        if (path.size == 4) {
            float c = batchManager.floatBits;

            batchManager.ensureSpaceForQuad();

//            A.set(path.items[0], path.items[1]);
            B.set(path.items[0], path.items[1]);
            C.set(path.items[2], path.items[3]);
            vert3(E);
            vert4(D);

            Joiner.prepareFlatEndpoint(C, B, D, E, startWidth * 0.5f);
            vert1(E);
            vert2(D);

            float x3 = x3();
            float y3 = y3();
            float x4 = x4();
            float y4 = y4();

            color(c,c,c,c);
            batchManager.pushQuad();

            batchManager.ensureSpaceForQuad();
            vert1(x4, y4);
            vert2(x3, y3);

            Joiner.prepareFlatEndpoint(B, C, D, E, endWidth * 0.5f);
            vert3(E);
            vert4(D);
            color(c,c,c,c);
            batchManager.pushQuad();

            path.clear();
            return;
        }

        if (joinType==JoinType.NONE) {
            drawPathNoJoin(path.items, path.size, startWidth, endWidth, open);
        } else {
            drawPathWithJoin(path.items, path.size, startWidth, endWidth, open, joinType==JoinType.POINTY);
        }
        if (!wasCaching) batchManager.endCaching();
        path.clear();
    }

    void drawPathNoJoin(float[] path, int size, float startWidth, float endWidth, boolean open) {
        int n = open?size-2:size;

        // distance
        IntMap<Float> pointDistance = new IntMap<Float>();
        float distance = 0f;
        float previousX = path[0];
        float previousY = path[1];
        for (int i = 0; i < n; i+= 2) {
            distance += (float) (Math.pow(path[i] - previousX, 2) + Math.pow(path[i+1] - previousY, 2));
            pointDistance.put(i, distance);

            previousX = path[i];
            previousY = path[i+1];
        }

        for (int i = 0; i < n; i+=2) {
            float interpolatedLineWidth = (endWidth - startWidth) * (pointDistance.get(i) / distance) + startWidth;

            drawer.lineDrawer.line(path[i], path[i+1], path[(i+2)%size], path[(i+3)%size], interpolatedLineWidth, false);
        }
    }


    void drawPathWithJoin(float[] path, int size, float startWidth, float endWidth, boolean open, boolean pointyJoin) {
        // distance
        IntMap<Float> pointDistance = new IntMap<Float>();
        float distance = 0f;
        float previousX = path[0];
        float previousY = path[1];
        pointDistance.put(0, distance);
        for (int i = 2; i < size; i+= 2) {
            distance += (float) (Math.pow(path[i] - previousX, 2) + Math.pow(path[i+1] - previousY, 2));
            pointDistance.put(i, distance);

            previousX = path[i];
            previousY = path[i+1];
        }

        float halfStartWidth = startWidth * 0.5f;
        float halfEndWidth = endWidth * 0.5f;
        float c = batchManager.floatBits;

        batchManager.ensureSpaceForQuad();

        for (int i = 2; i < size-2; i+=2) {
            float interpolatedLineWidth = (endWidth - startWidth) * (pointDistance.get(i) / distance) + startWidth;
            float halfInterpolatedLineWidth = interpolatedLineWidth * 0.5f;

            A.set(path[i-2], path[i-1]);
            B.set(path[i], path[i+1]);
            C.set(path[i+2], path[i+3]);

            if (pointyJoin) {
                Joiner.preparePointyJoin(A, B, C, D, E, halfInterpolatedLineWidth);
            } else {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfInterpolatedLineWidth, false);
            }
            vert3(D);
            vert4(E);

            if (i==2) {
                if (open) {
                    Joiner.prepareFlatEndpoint(path[2], path[3], path[0], path[1], D, E, halfStartWidth);
                    vert1(E);
                    vert2(D);
                } else {
                    vec1.set(path[size-2], path[size-1]);
                    if (pointyJoin) {
                        Joiner.preparePointyJoin(vec1, A, B, D0, E0, halfStartWidth);
                    } else {
                        Joiner.prepareSmoothJoin(vec1, A, B, D0, E0, halfStartWidth, true);
                    }
                    vert1(E0);
                    vert2(D0);
                }
            }

            float x3, y3, x4, y4;
            if (pointyJoin) {
                x3 = x3();
                y3 = y3();
                x4 = x4();
                y4 = y4();
            } else {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfInterpolatedLineWidth, true);
                x3 = D.x;
                y3 = D.y;
                x4 = E.x;
                y4 = E.y;
            }
            color(c,c,c,c);
            batchManager.pushQuad();
            if (!pointyJoin) drawSmoothJoinFill(A, B, C, D, E, halfInterpolatedLineWidth);
            batchManager.ensureSpaceForQuad();
            vert1(x4, y4);
            vert2(x3, y3);
        }

        if (open) {
            //draw last link on path
            Joiner.prepareFlatEndpoint(B, C, D, E, halfEndWidth);
            vert3(E);
            vert4(D);
            color(c,c,c,c);
            batchManager.pushQuad();
        } else {
            if (pointyJoin) {
                //draw last link on path
                A.set(path[0], path[1]);
                Joiner.preparePointyJoin(B, C, A, D, E, halfEndWidth);
                vert3(D);
                vert4(E);
                color(c,c,c,c);
                batchManager.pushQuad();

                //draw connection back to first vertex
                batchManager.ensureSpaceForQuad();
                vert1(D);
                vert2(E);
                vert3(E0);
                vert4(D0);
                color(c,c,c,c);
                batchManager.pushQuad();
            } else {
                //draw last link on path
                A.set(B);
                B.set(C);
                C.set(path[0], path[1]);
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfEndWidth, false);
                vert3(D);
                vert4(E);
                color(c,c,c,c);
                batchManager.pushQuad();
                drawSmoothJoinFill(A, B, C, D, E, halfEndWidth);

                //draw connection back to first vertex
                batchManager.ensureSpaceForQuad();
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfEndWidth, true);
                vert3(E);
                vert4(D);
                A.set(path[2], path[3]);
                Joiner.prepareSmoothJoin(B, C, A, D, E, halfStartWidth, false);
                vert1(D);
                vert2(E);
                color(c,c,c,c);
                batchManager.pushQuad();
                drawSmoothJoinFill(B, C, A, D, E, halfStartWidth);
            }
        }
    }

}
