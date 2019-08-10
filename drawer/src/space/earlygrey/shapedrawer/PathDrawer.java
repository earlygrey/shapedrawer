package space.earlygrey.shapedrawer;

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
        path(tempPath.items, 0, tempPath.size, lineWidth, joinType);
        tempPath.clear();
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
            if (!ShapeUtils.epsilonEquals(userPath[i-2], userPath[i]) && !ShapeUtils.epsilonEquals(userPath[i-1], userPath[i+1])) {
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

        switch(joinType) {
            case NONE:
                drawPathNoJoin(path.items, path.size, lineWidth, open);
                break;
            case SMOOTH:
                drawPathSmoothJoin(path.items, path.size, lineWidth, open);
                break;
            case POINTY:
                drawPathPointyJoin(path.items, path.size, lineWidth, open);
                break;
        }
        path.clear();
    }

    void drawPathNoJoin(float[] path, int size, float lineWidth, boolean open) {
        int n = open?size-2:size;
        for (int i = 0; i < n; i+=2) {
            drawer.line(path[i], path[i+1], path[(i+2)%size], path[(i+3)%size], lineWidth);
        }
    }

    void drawPathPointyJoin(float[] path, int size, float lineWidth, boolean open) {
        float halfLineWidth =  0.5f*lineWidth;

        for (int i = 2; i < size-2; i+=2) {

            A.set(path[i-2], path[i-1]);
            B.set(path[i], path[i+1]);
            C.set(path[i+2], path[i+3]);

            Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
            vert3(D);
            vert4(E);
            if (i==2) {
                if (open) {
                    preparePathEndpoint(path[2], path[3], path[0], path[1], D, E, halfLineWidth);
                    vert1(E);
                    vert2(D);
                } else {
                    vec1.set(path[size-2], path[size-1]);
                    Joiner.preparePointyJoin(vec1, A, B, D0, E0, halfLineWidth);
                    vert1(E0);
                    vert2(D0);
                }
            }
            drawVerts();
            vert1(x4(), y4());
            vert2(x3(), y3());
        }
        if (open) {
            //draw last link on path
            preparePathEndpoint(B, C, D, E, halfLineWidth);
            vert1(D);
            vert2(E);
            drawVerts();
        } else {
            //draw last link on path
            A.set(path[0], path[1]);
            Joiner.preparePointyJoin(B, C, A, D, E, halfLineWidth);
            vert1(E);
            vert2(D);
            drawVerts();
            //draw connection back to first vertex
            vert3(D0);
            vert4(E0);
            drawVerts();
        }
    }

    void drawPathSmoothJoin(float[] path, int size, float lineWidth, boolean open) {
        float halfLineWidth =  0.5f*lineWidth;

        A.set(path[0], path[1]);
        B.set(path[2], path[3]);
        C.set(path[4], path[5]);

        for (int i = 4; i < size; i+=2) {
            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
            vert3(E);
            vert4(D);

            if (i==4) {
                if (open) {
                    preparePathEndpoint(B, A, D, E, halfLineWidth);
                } else {
                    vec1.set(path[size-2], path[size-1]);
                    Joiner.prepareSmoothJoin(vec1, A, B, D0, E0, halfLineWidth, false);
                    Joiner.prepareSmoothJoin(vec1, A, B, D, E, halfLineWidth, true);
                }
                vert1(D);
                vert2(E);
            }

            drawVerts();
            drawSmoothJoinFill(A, B, C, D, E, halfLineWidth);

            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
            vert1(D);
            vert2(E);

            if (i<size-2) {
                A.set(path[i-2], path[i-1]);
                B.set(path[i], path[i+1]);
                C.set(path[i+2], path[i+3]);
            }
        }
        if (open) {
            //draw last link on path
            preparePathEndpoint(B, C, D, E, halfLineWidth);
            vert3(D);
            vert4(E);
            drawVerts();
        } else {
            //draw last link on path
            A.set(B);
            B.set(C);
            C.set(path[0], path[1]);
            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
            vert3(E);
            vert4(D);
            drawVerts();
            drawSmoothJoinFill(A, B, C, D, E, halfLineWidth);
            //draw connection back to first vertex
            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
            vert3(E);
            vert4(D);
            vert1(D0);
            vert2(E0);
            drawVerts();
            A.set(path[2], path[3]);
            drawSmoothJoinFill(B, C, A, D0, E0, halfLineWidth);
        }

    }


    void preparePathEndpoint(float pathPointX, float pathPointY, float endPointX, float endPointY, Vector2 D, Vector2 E, float halfLineWidth) {
        vec1.set(endPointX, endPointY).sub(pathPointX, pathPointY).setLength(halfLineWidth);
        D.set(vec1.y, -vec1.x).add(endPointX, endPointY);
        E.set(-vec1.y, vec1.x).add(endPointX, endPointY);
    }

    void preparePathEndpoint(Vector2 pathPoint, Vector2 endPoint, Vector2 D, Vector2 E, float halfLineWidth) {
        preparePathEndpoint(pathPoint.x, pathPoint.y, endPoint.x, endPoint.y, D, E, halfLineWidth);
    }

}
