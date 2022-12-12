package space.earlygrey.shapedrawer.shapes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.Line;

public class BasicLine extends BasicShape<BasicLine> implements Line {

    private Vector2 from, to;
    private float width;
    private float startColor, endColor;
    boolean snap;

    protected BasicLine(ShapeDrawer drawer) {
        super(drawer);
        from = new Vector2();
        to = new Vector2();
    }

    @Override
    void reset(boolean filled) {
        super.reset(filled);
        from.setZero();
        to.setZero();
        startColor = drawer.getPackedColor();
        endColor = drawer.getPackedColor();
        snap = drawer.isDefaultSnap();
        width = drawer.getDefaultLineWidth();
    }

    public BasicLine from(float x, float y) {
        from.set(x, y);
        return this;
    }

    public BasicLine to(float x, float y) {
        to.set(x, y);
        return this;
    }

    public BasicLine from(Vector2 from) {
        this.from.set(from);
        return this;
    }

    public BasicLine to(Vector2 to) {
        this.to.set(to);
        return this;
    }

    @Override
    public BasicLine joinType(JoinType joinType) {
        this.joinType = joinType;
        return this;
    }

    @Override
    public BasicLine lineWidth(float width) {
        setLineWidth(width);
        return this;
    }


    public BasicLine width(float width) {
        this.width = width;
        return this;
    }

    public BasicLine color(Color color) {
        startColor(color);
        endColor(color);
        return this;
    }

    public BasicLine startColor(Color startColor) {
        this.startColor = startColor.toFloatBits();
        return this;
    }

    public BasicLine endColor(Color endColor) {
        this.endColor = endColor.toFloatBits();
        return this;
    }

    public BasicLine snap(boolean snap) {
        this.snap = snap;
        return this;
    }

    @Override
    public void draw() {
        drawer.line(from.x, from.y, to.x, to.y, width, snap, startColor, endColor);
    }


}
