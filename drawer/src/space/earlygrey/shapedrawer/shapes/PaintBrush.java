package space.earlygrey.shapedrawer.shapes;

import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.shapes.Shape.Sector;

public class PaintBrush extends Utensil {

   public PaintBrush(ShapeDrawer drawer) {
      super(drawer);
   }

   @Override
   boolean filled() {
      return true;
   }

   public Sector sector() {
      ARC.reset(filled());
      return ARC;
   }


}
