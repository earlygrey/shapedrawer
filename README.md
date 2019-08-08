# Shape Drawer

Draws simple shapes like libgdx's ShapeRenderer does, but uses a Batch to perform the drawing.

Comes with overloaded methods to draw lines, paths, ellipses, regular polygons and rectangles.
Since it uses a Batch to perform the drawing, it can be used in between `Batch#begin()` and `Batch#end()` without
needing to flush the Batch.

Just needs to be provided with a Batch (SpriteBatch or PolygonSpriteBatch will work) and a TextureRegion.



Test uses the [Commodore 64 UI Skin](https://ray3k.wordpress.com/artwork/commodore-64-ui-skin-for-libgdx/) created by Raymond "Raeleus" Buckley under the [CC BY license:](https://creativecommons.org/licenses/by/4.0/)
