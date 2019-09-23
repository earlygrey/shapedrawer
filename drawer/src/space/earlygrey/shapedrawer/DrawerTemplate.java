package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

/**
 * <p>Template for the classes that take care of drawing individual shape types.
 * Contains utility methods for pushing vertex information to the drawer.</p>
 *
 * @author earlygrey
 */

abstract class DrawerTemplate {

    static final Vector2 A = new Vector2(), B = new Vector2(), C = new Vector2(), D = new Vector2(), E = new Vector2(), dir = new Vector2();
    static final Vector2 vec1 = new Vector2();

    final ShapeDrawer drawer;

    DrawerTemplate(ShapeDrawer drawer) {
        this.drawer = drawer;
    }

    /**
     * <p>Draws a trangle that fills the gap at joints when {@link JoinType#SMOOTH} join type is used.</p>
     * @param A the point before the join
     * @param B the point at the join
     * @param C the point after the join
     * @param D
     * @param E
     */
    void drawSmoothJoinFill(Vector2 A, Vector2 B, Vector2 C, Vector2 D, Vector2 E, float halfLineWidth) {
        boolean bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
        vert1(bendsLeft?E:D);
        vert2(bendsLeft?D:E);
        bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
        vert3(bendsLeft?E:D);
        drawer.pushTriangle();
    }

    void drawSmoothJoinFill(Vector2 A, Vector2 B, Vector2 C, Vector2 D, Vector2 E, Vector2 offset, float cos, float sin, float halfLineWidth) {
        boolean bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
        Vector2 V1 = bendsLeft?E:D, V2 = bendsLeft?D:E;
        vert1(V1.x*cos-V1.y*sin  + offset.x, V1.x*sin+V1.y*cos + offset.y);
        vert2(V2.x*cos-V2.y*sin  + offset.x, V2.x*sin+V2.y*cos + offset.y);
        bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
        Vector2 V3 = bendsLeft?E:D;
        float x = V3.x*cos-V3.y*sin  + offset.x, y = V3.x*sin+V3.y*cos + offset.y;
        vert3(x, y);
        drawer.pushTriangle();
    }


    //VERTEX SETTING UTILITY FUNCTIONS
    void x1(float x1){drawer.x1(x1);}
    void y1(float y1){drawer.y1(y1);}
    void x2(float x2){drawer.x2(x2);}
    void y2(float y2){drawer.y2(y2);}
    void x3(float x3){drawer.x3(x3);}
    void y3(float y3){drawer.y3(y3);}
    void x4(float x4){drawer.x4(x4);}
    void y4(float y4){drawer.y4(y4);}
    void vert1(float x, float y) {x1(x);y1(y);}
    void vert2(float x, float y) {x2(x);y2(y);}
    void vert3(float x, float y) {x3(x);y3(y);}
    void vert4(float x, float y) {x4(x);y4(y);}
    void vert1(Vector2 V) {vert1(V.x, V.y);}
    void vert2(Vector2 V) {vert2(V.x, V.y);}
    void vert3(Vector2 V) {vert3(V.x, V.y);}
    void vert4(Vector2 V) {vert4(V.x, V.y);}
    void vert1(Vector2 V, Vector2 offset) {vert1(V.x+offset.x, V.y+offset.y);}
    void vert2(Vector2 V, Vector2 offset) {vert2(V.x+offset.x, V.y+offset.y);}
    void vert3(Vector2 V, Vector2 offset) {vert3(V.x+offset.x, V.y+offset.y);}
    void vert4(Vector2 V, Vector2 offset) {vert4(V.x+offset.x, V.y+offset.y);}
    float x1() {return drawer.x1();}
    float y1() {return drawer.y1();}
    float x2() {return drawer.x2();}
    float y2() {return drawer.y2();}
    float x3() {return drawer.x3();}
    float y3() {return drawer.y3();}
    float x4() {return drawer.x4();}
    float y4() {return drawer.y4();}



    // DEBUGGING
    void print1234() {
        System.out.println(
                "("+x1()+","+y1()+")  "  +
                "("+x2()+","+y2()+")  " +
                "("+x3()+","+y3()+")  " +
                "("+x4()+","+y4()+")");

    }
    void printABC() {
        System.out.println("A: "+A+", B: "+B+", C: "+C);
    }

    void drawPoint(Vector2 point, Color color) {
        drawPoint(point.x, point.y, color);
    }
    void drawPoint(Vector2 point, Color color, float r) {
        drawPoint(point, color.toFloatBits(), r);
    }
    void drawPoint(Vector2 point, float color, float r) {
        drawPoint(point.x, point.y, color, r);
    }
    void drawPoint(float x, float y, Color color) {
        drawPoint(x, y, color.toFloatBits(), 3);
    }
    void drawPoint(float x, float y, float color, float r) {
        Color c = drawer.getBatch().getColor();
        drawer.getBatch().setPackedColor(color);
        drawer.getBatch().draw(drawer.getRegion(), x-r, y-r, 2*r, 2*r);
        drawer.getBatch().setColor(c);
    }

    void draw1234() {
        drawPoint(x1(), y1(), Color.GREEN);
        drawPoint(x2(), y2(), Color.ORANGE);
        drawPoint(x3(), y3(), Color.YELLOW);
        drawPoint(x4(), y4(), Color.PURPLE);
    }

    void drawABC() {
        drawABC(Vector2.Zero);
    }

    void drawABC(Vector2 offset) {
        drawPoint(A, Color.GREEN);
        drawPoint(B, Color.ORANGE);
        drawPoint(C, Color.YELLOW);

    }
    void drawDE(boolean scheme1) {
        drawPoint(D, scheme1?Color.YELLOW:Color.CHARTREUSE);
        drawPoint(E, scheme1?Color.PINK:Color.TAN);
    }
    void drawDE() {
        drawDE(true);
    }

}
