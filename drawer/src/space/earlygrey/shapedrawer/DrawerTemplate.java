package space.earlygrey.shapedrawer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

abstract class DrawerTemplate {

    static final Vector2 A = new Vector2(), B = new Vector2(), C = new Vector2(), D = new Vector2(), E = new Vector2(), dir = new Vector2();
    static final Vector2 vec1 = new Vector2(), vec2 = new Vector2(), vec3 = new Vector2();

    final ShapeDrawer drawer;

    DrawerTemplate(ShapeDrawer drawer) {
        this.drawer = drawer;
    }

    void drawVerts() {
        drawer.drawVerts();
    }

    void drawSmoothJoinFill(Vector2 A, Vector2 B, Vector2 C, Vector2 D, Vector2 E, float halfLineWidth) {
        boolean bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, false);
        vert1(bendsLeft?E:D);
        vert2(bendsLeft?D:E);
        bendsLeft = Joiner.prepareSmoothJoin(A, B, C, D, E, halfLineWidth, true);
        vert3(bendsLeft?E:D);
        vert4(x3(), y3());
        drawVerts();
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
        vert4(x, y);
        drawVerts();
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
    void draw1234() {
        float s = 3;
        drawer.getBatch().setColor(Color.GREEN);
        drawer.getBatch().draw(drawer.getRegion(), x1()-s, y1()-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.ORANGE);
        drawer.getBatch().draw(drawer.getRegion(), x2()-s, y2()-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.BLUE);
        drawer.getBatch().draw(drawer.getRegion(), x3()-s, y3()-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.PURPLE);
        drawer.getBatch().draw(drawer.getRegion(), x4()-s, y4()-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.WHITE);
    }
    void drawABC() {
        float s = 4;
        drawer.getBatch().setColor(Color.GREEN);
        drawer.getBatch().draw(drawer.getRegion(), A.x-s, A.y-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.ORANGE);
        drawer.getBatch().draw(drawer.getRegion(), B.x-s, B.y-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.BLUE);
        drawer.getBatch().draw(drawer.getRegion(), C.x-s, C.y-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.WHITE);
    }
    void drawDE(boolean scheme1) {
        float s = 2;
        drawer.getBatch().setColor(scheme1?Color.YELLOW:Color.CHARTREUSE);
        drawer.getBatch().draw(drawer.getRegion(), D.x-s, D.y-s, 2*s, 2*s);
        drawer.getBatch().setColor(scheme1?Color.PINK:Color.TAN);
        drawer.getBatch().draw(drawer.getRegion(), E.x-s, E.y-s, 2*s, 2*s);
        drawer.getBatch().setColor(Color.WHITE);
    }
    void drawDE() {
        drawDE(true);
    }

}
