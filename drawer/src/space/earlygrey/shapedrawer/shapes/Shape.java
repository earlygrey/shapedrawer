package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;

import space.earlygrey.shapedrawer.JoinType;

public interface Shape<T extends Shape> {

    void draw();

    T color(Color color);

    interface FilledCircle<T extends FilledCircle> extends Shape {

        T centre(float x, float y);

        T centre(Vector2 centre);

        T radius(float radius);

    }

    interface OutlinedCircle extends FilledCircle<OutlinedCircle> {
        OutlinedCircle joinType(JoinType joinType);

        OutlinedCircle lineWidth(float width);

    }

    interface FilledEllipse<T extends FilledEllipse> extends Shape<T> {

        T centre(float x, float y);

        T centre(Vector2 centre);

        T radiusX(float radiusX);

        T radiusY(float radiusY);

        T rotation(float rotation);

        T rotate(float rotation);

    }

    interface OutlinedEllipse extends FilledEllipse<OutlinedEllipse> {

        OutlinedEllipse joinType(JoinType joinType);

        OutlinedEllipse lineWidth(float width);

    }

    interface Line extends Shape<Line> {

        Line from(float x, float y);

        Line to(float x, float y);

        Line from(Vector2 from);

        Line to(Vector2 to);

        Line joinType(JoinType joinType);

        Line lineWidth(float width);

        Line startColor(Color startColor);

        Line endColor(Color endColor);

        Line snap(boolean snap);

    }

    interface FilledTriangle<T extends FilledTriangle> extends Shape {

        T a(float x, float y);

        T b(float x, float y);

        T c(float x, float y);

        T a(Vector2 a);

        T b(Vector2 b);

        T c(Vector2 c);
    }

    interface OutlinedTriangle extends FilledTriangle<OutlinedTriangle> {
        OutlinedTriangle joinType(JoinType joinType);

        OutlinedTriangle lineWidth(float width);

    }

    interface FilledPolygon<T extends FilledPolygon> extends Shape<T> {

        <V extends Vector2> T vertices(Iterable<V> points);

        T vertices(FloatArray points);

        T vertices(float[] points);

        T addVertex(float x, float y);

        T addVertex(Vector2 vertex);

        T offset(float x, float y);

        T scale(float x, float y);

    }

    interface OutlinedPolygon<T extends OutlinedPolygon> extends FilledPolygon<T> {

        T joinType(JoinType joinType);

        T lineWidth(float width);

    }

    interface PolyLine extends OutlinedPolygon<PolyLine> {

    }

    interface FilledRegularPolygon<T extends FilledRegularPolygon> extends Shape<T> {

        T centre(Vector2 centre);

        T radiusX(float radiusX);

        T radiusY(float radiusY);

        T rotation(float rotation);

        T rotate(float rotation);

        T sides(int sides);
    }

    interface OutlinedRegularPolygon extends FilledRegularPolygon<OutlinedRegularPolygon> {

        OutlinedRegularPolygon joinType(JoinType joinType);

        OutlinedRegularPolygon lineWidth(float width);
    }

    interface Sector<T extends Sector> extends Shape<T> {

        T centre(float x, float y);

        T centre(Vector2 centre);

        T radius(float radius);

        T startAngle(float startAngle);

        T radians(float radians);

        T sides(int sides);

    }

    interface Arc extends Sector<Arc> {
        Arc joinType(JoinType joinType);

        Arc lineWidth(float width);
    }

    interface FilledRectangle<T extends FilledRectangle> extends Shape<T> {

        T position(float x, float y);

        T position(Vector2 centre);

        T size(float width, float height);

        T rotation(float rotation);

        T rotate(float rotation);

    }

    interface OutlinedRectangle extends FilledRectangle<OutlinedRectangle> {

        OutlinedRectangle joinType(JoinType joinType);

        OutlinedRectangle lineWidth(float width);
    }


}
