package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

class PathDrawer extends DrawerTemplate {

    FloatArray path = new FloatArray();
    FloatArray tempPath = new FloatArray();

    static final Vector2 D0 = new Vector2(), E0 = new Vector2();

    PathDrawer(ShapeDrawer drawer) {
        super(drawer);
    }

    <T extends Vector2> void path(Array<T> userPath, float lineWidth, JoinType joinType) {
        path(userPath, lineWidth, joinType, true);
    }

    <T extends Vector2> void path(Array<T> userPath, float lineWidth, JoinType joinType, boolean open) {
        for (int i = 0; i < userPath.size; i++) {
            Vector2 v = userPath.get(i);
            tempPath.add(v.x, v.y);
        }

        path(tempPath.items, 0, tempPath.size, lineWidth, joinType, open);
        tempPath.clear();
    }

    void path (FloatArray userPath, float lineWidth, JoinType joinType, boolean open) {
        path (userPath.items, 0, userPath.size, lineWidth, joinType, open);
    }

    void path (float[] userPath, float lineWidth, JoinType joinType) {
        path (userPath, 0, userPath.length, lineWidth, joinType);
    }

    void path (float[] userPath, float lineWidth, JoinType joinType, boolean open) {
        path (userPath, 0, userPath.length, lineWidth, joinType, open);
    }

    void path (float[] userPath, int start, int end, float lineWidth, JoinType joinType) {
        path(userPath, start, end, lineWidth, joinType, true);
    }

    void path (float[] userPath, int start, int end, float lineWidth, JoinType joinType, boolean open) {

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
        if (path.size == 4) {
            drawer.line(path.items[0], path.items[1], path.items[2], path.items[3], lineWidth);
            path.clear();
            return;
        }
        drawer.startCaching();
        if (joinType==JoinType.NONE) {
            drawPathNoJoin(path.items, path.size, lineWidth, open);
        } else {
            drawPathWithJoin(path.items, path.size, lineWidth, open,joinType==JoinType.POINTY);
        }
        drawer.endCaching();
        path.clear();
    }

    void drawPathNoJoin(float[] path, int size, float lineWidth, boolean open) {
        int n = open?size-2:size;
        for (int i = 0; i < n; i+=2) {
            drawer.line(path[i], path[i+1], path[(i+2)%size], path[(i+3)%size], lineWidth);
        }
    }


    void drawPathWithJoin(float[] path, int size, float lineWidth, boolean open, boolean pointyJoin) {
        float halfLineWidth =  0.5f*lineWidth;

        for (int i = 2; i < size-2; i+=2) {

            A.set(path[i-2], path[i-1]);
            B.set(path[i], path[i+1]);
            C.set(path[i+2], path[i+3]);

            if (pointyJoin) {
                Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
            } else {
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
            }
            vert3(D);
            vert4(E);

            if (i==2) {
                if (open) {
                    Joiner.prepareFlatEndpoint(path[2], path[3], path[0], path[1], D, E, halfLineWidth);
                    vert1(E);
                    vert2(D);
                } else {
                    vec1.set(path[size-2], path[size-1]);
                    if (pointyJoin) {
                        Joiner.preparePointyJoin(vec1, A, B, D0, E0, halfLineWidth);
                    } else {
                        Joiner.prepareSmoothJoin(vec1, A, B, D0, E0, halfLineWidth, true);
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
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
                x3 = D.x;
                y3 = D.y;
                x4 = E.x;
                y4 = E.y;
            }

            drawer.pushVerts();
            if (!pointyJoin) drawSmoothJoinFill(A, B, C, D, E, halfLineWidth);
            vert1(x4, y4);
            vert2(x3, y3);
        }

        if (open) {
            //draw last link on path
            Joiner.prepareFlatEndpoint(B, C, D, E, halfLineWidth);
            vert3(E);
            vert4(D);
            drawer.pushVerts();
        } else {
            if (pointyJoin) {
                //draw last link on path
                A.set(path[0], path[1]);
                Joiner.preparePointyJoin(B, C, A, D, E, halfLineWidth);
                vert3(D);
                vert4(E);
                drawer.pushVerts();

                //draw connection back to first vertex
                vert1(D);
                vert2(E);
                vert3(E0);
                vert4(D0);
                drawer.pushVerts();
            } else {
                //draw last link on path
                A.set(B);
                B.set(C);
                C.set(path[0], path[1]);
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
                vert3(D);
                vert4(E);
                drawer.pushVerts();
                drawSmoothJoinFill(A, B, C, D, E, halfLineWidth);

                //draw connection back to first vertex
                Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
                vert3(E);
                vert4(D);
                A.set(path[2], path[3]);
                Joiner.prepareSmoothJoin(B, C, A, D, E, halfLineWidth, false);
                vert1(D);
                vert2(E);
                drawer.pushVerts();
                drawSmoothJoinFill(B, C, A, D, E, halfLineWidth);
            }
        }
    }

}
