package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;

import java.util.Arrays;

class PathDrawer extends DrawerTemplate {

    //Array<Vector2> path = new Array<Vector2>();
    FloatArray path = new FloatArray();
    FloatArray tempPath = new FloatArray();


    PathDrawer(ShapeDrawer drawer) {
        super(drawer);
    }

    <T extends Vector2> void path(Array<T> userPath, float lineWidth, JoinType joinType) {
        for (int i = 0; i < userPath.size; i++) {
            Vector2 v = userPath.get(i);
            tempPath.add(v.x, v.y);
        }
        path(tempPath.items, 0, tempPath.size, lineWidth, joinType);
        tempPath.clear();
    }

    void path (float[] userPath, int start, int end, float lineWidth, JoinType joinType) {

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
                drawPathNoJoin(path.items, path.size, lineWidth);
                break;
            case SMOOTH:
                drawPathSmoothJoin(path.items, path.size, lineWidth);
                break;
            case POINTY:
                drawPathPointyJoin(path.items, path.size, lineWidth);
                break;
        }
        path.clear();
    }

    void drawPathNoJoin(float[] path, int size, float lineWidth) {
        for (int i = 0; i < size-2; i+=2) {
            drawer.line(path[i], path[i+1], path[i+2], path[i+3], lineWidth);
        }
    }

    void drawPathPointyJoin(float[] path, int size, float lineWidth) {
        float halfLineWidth =  0.5f*lineWidth;

        for (int i = 2; i < size-2; i+=2) {

            A.set(path[i-2], path[i-1]);
            B.set(path[i], path[i+1]);
            C.set(path[i+2], path[i+3]);

            Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
            vert3(D);
            vert4(E);
            if (i==2) {
                preparePathEndpoint(path[2], path[3], path[0], path[1], D, E, halfLineWidth);
                vert1(E);
                vert2(D);
            }
            drawVerts();
            vert1(x4(), y4());
            vert2(x3(), y3());
        }
        preparePathEndpoint(B, C, D, E, halfLineWidth);
        vert1(D);
        vert2(E);
        drawVerts();
    }

    void drawPathSmoothJoin(float[] path, int size, float lineWidth) {
        float halfLineWidth =  0.5f*lineWidth;

        A.set(path[0], path[1]);
        B.set(path[2], path[3]);
        C.set(path[4], path[5]);

        for (int i = 4; i < size; i+=2) {
            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
            vert3(E);
            vert4(D);

            if (i==4) {
                preparePathEndpoint(B, A, D, E, halfLineWidth);
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
        preparePathEndpoint(B, C, D, E, halfLineWidth);
        vert3(D);
        vert4(E);
        drawVerts();
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
