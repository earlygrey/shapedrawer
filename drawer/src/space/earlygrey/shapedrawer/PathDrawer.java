package space.earlygrey.shapedrawer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

class PathDrawer extends DrawerTemplate {

    Array<Vector2> path = new Array<Vector2>();

    PathDrawer(ShapeDrawer drawer) {
        super(drawer);
    }

    <T extends Vector2> void path(Array<T> userPath, float lineWidth, JoinType joinType) {

        if (userPath.size < 2) return;

        //construct new path consisting of unique consecutive points
        int n = userPath.size;
        path.add(userPath.get(0));
        for(int i = 1; i < n; i++) {
            if(!userPath.get(i-1).epsilonEquals(userPath.get(i))) {
                path.add(userPath.get(i));
            }
        }
        if (path.size < 2) {
            path.clear();
            return;
        }
        if (path.size == 2) {
            drawer.line(path.get(0), path.get(1), lineWidth);
            path.clear();
            return;
        }

        switch(joinType) {
            case NONE:
                drawPathNoJoin(path, lineWidth);
                break;
            case SMOOTH:
                drawPathSmoothJoin(path, lineWidth);
                break;
            case POINTY:
                drawPathPointyJoin(path, lineWidth);
                break;
        }
        path.clear();
    }

    <T extends Vector2> void drawPathNoJoin(Array<T> path, float lineWidth) {
        int n = path.size;
        for (int i = 0; i < n-1; i++) {
            drawer.line(path.get(i), path.get(i+1), lineWidth);
        }
    }

    <T extends Vector2> void drawPathPointyJoin(Array<T> path, float lineWidth) {
        float halfLineWidth =  0.5f*lineWidth;

        int n = path.size;

        for (int i = 1; i < n-1; i++) {

            A.set(path.get(i-1));
            B.set(path.get(i));
            C.set(path.get(i+1));

            Joiner.preparePointyJoin(A, B, C, D, E, halfLineWidth);
            vert3(D);
            vert4(E);
            if (i==1) {
                preparePathEndpoint(path.get(1), path.get(0), D, E, halfLineWidth);
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

    <T extends Vector2> void drawPathSmoothJoin(Array<T> path, float lineWidth) {
        float halfLineWidth =  0.5f*lineWidth;

        A.set(path.get(0));
        B.set(path.get(1));
        C.set(path.get(2));

        int n = path.size;
        for (int i = 2; i < n; i++) {
            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
            vert3(E);
            vert4(D);

            if (i==2) {
                preparePathEndpoint(B, A, D, E, halfLineWidth);
                vert1(D);
                vert2(E);
            }

            drawVerts();
            drawSmoothJoinFill(A, B, C, D, E, halfLineWidth);

            Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
            vert1(D);
            vert2(E);

            if (i<n-1) {
                A.set(path.get(i-1));
                B.set(path.get(i));
                C.set(path.get(i+1));
            }
        }
        preparePathEndpoint(B, C, D, E, halfLineWidth);
        vert3(D);
        vert4(E);
        drawVerts();
    }

    void preparePathEndpoint(Vector2 pathPoint, Vector2 endPoint, Vector2 D, Vector2 E, float halfLineWidth) {
        vec1.set(endPoint).sub(pathPoint).setLength(halfLineWidth);
        D.set(vec1.y, -vec1.x).add(endPoint);
        E.set(-vec1.y, vec1.x).add(endPoint);
    }

}
