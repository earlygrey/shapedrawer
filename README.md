# Shape Drawer

[![](https://jitpack.io/v/earlygrey/shapedrawer.svg)](https://jitpack.io/#space.earlygrey/shapedrawer)

---

A library for [libgdx](https://libgdx.badlogicgames.com/), an open-source game development application framework written in java.

Draws simple shapes like libgdx's [ShapeRenderer](https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html) does, but uses a Batch to perform the drawing. This means it can be used in between `Batch#begin()` and `Batch#end()` without needing to flush the Batch.

Comes with overloaded methods to draw lines, paths, ellipses, regular polygons and rectangles.

Just needs to be provided with a Batch (eg a SpriteBatch) or if you additionally want filled shapes a PolygonBatch (eg a PolygonSpriteBatch), as well as a TextureRegion.

---

## Including in Project

To use this in your gradle project, add the following to your root build.gradle file:
 
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
And  in your core project add the dependency:
```
dependencies {
        implementation 'space.earlygrey:shapedrawer:1.3.0'
}
```

For HTML5/GWT support, add the dependency to the html project:

```groovy
project(":html") {
    apply plugin: "gwt"
    apply plugin: "war"


    dependencies {
        ...
        implementation 'space.earlygrey:shapedrawer:1.3.0:sources'
    }
}
```

And add the following line to the GdxDefinition.gwt.xml file in the HTML project:
```xml
<inherits name="space.earlygrey.shapedrawer"/>
```

See the [jitpack website](https://jitpack.io/#earlygrey/shapedrawer/-SNAPSHOT) for more info.


## Usage

To create a ShapeDrawer instance you just need a Batch and a TextureRegion. Typically this is a single white pixel so that you can easily colour it, and is best packed into an atlas with your other textures.

To instantiate a ShapeDrawer, use:

```java
ShapeDrawer drawer = new ShapeDrawer(batch, region);
```

And to use it, simply call its drawing methods in between `Batch#begin()` and `Batch#end()`. Something like this:

```java
batch.begin();
drawer.line(0, 0, 100, 100);
batch.end();
```

That's it!

If you want to draw filled shapes you'll need a PolygonBatch, and then instead of creating a ShapeDrawer, create a PolygonShapeDrawer:

```java
PolygonShapeDrawer drawer = new PolygonShapeDrawer(polygonBatch, region);
```

PolygonShapeDrawer extends ShapeDrawer so you still have access to all the methods provided by the latter.

---

Check the [wiki](https://github.com/earlygrey/shapedrawer/wiki) for more info, including:
* [Using Shape Drawer](https://github.com/earlygrey/shapedrawer/wiki/Using-Shape-Drawer)
* [Shapes](https://github.com/earlygrey/shapedrawer/wiki/Shapes)
* [Join Types](https://github.com/earlygrey/shapedrawer/wiki/Join-Types)
* [Pixel Snapping](https://github.com/earlygrey/shapedrawer/wiki/Pixel-Snapping)


---

Test application uses the [Commodore 64 UI Skin](https://ray3k.wordpress.com/artwork/commodore-64-ui-skin-for-libgdx/) created by Raymond "Raeleus" Buckley under the [CC BY license](https://creativecommons.org/licenses/by/4.0/). [Check out the others!](https://ray3k.wordpress.com/artwork/)
