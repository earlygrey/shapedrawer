# Shape Drawer

[![](https://jitpack.io/v/earlygrey/shapedrawer.svg)](https://jitpack.io/#space.earlygrey/shapedrawer)

---

A library for [libGDX](https://github.com/libgdx/libgdx), an open-source game development application framework written in java.

Draws simple shapes like libGDX's [ShapeRenderer](https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html) does, but uses a Batch to perform the drawing. This means it can be used in between `Batch#begin()` and `Batch#end()` without needing to flush the Batch.

Comes with overloaded methods to draw lines, paths, ellipses, regular polygons and rectangles.

Just needs to be provided with a Batch and a TextureRegion. However, note that if you want to draw filled shapes, it is more efficient to use a batch that implements [PolygonBatch](https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/PolygonBatch.html) (eg a [PolygonSpriteBatch](https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/PolygonSpriteBatch.html)) instead of a Batch that does not (eg a [SpriteBatch](https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/g2d/SpriteBatch.html)).

![Gif didn't load - see wiki for images!](https://raw.githubusercontent.com/wiki/earlygrey/shapedrawer/images/readme_demo.gif)

---

## Including in Project

To use this in your gradle project, add the version number and jitpack repository information to your root build.gradle file:
 
```groovy
allprojects {
    ext {
    	...
        shapedrawerVersion = '2.5.0'
    }
    repositories {
	...
	maven { url 'https://jitpack.io' }
    }
}
```
And  in your core project add the dependency:
```groovy
dependencies {
    implementation "space.earlygrey:shapedrawer:$shapedrawerVersion"
}
```

For HTML5/GWT support, add the dependency to the html project:

```groovy
project(":html") {
    apply plugin: "gwt"
    apply plugin: "war"


    dependencies {
        ...
        implementation "space.earlygrey:shapedrawer:$shapedrawerVersion:sources"
    }
}
```

And add the following line to the GdxDefinition.gwt.xml file in the HTML project:
```xml
<inherits name="space.earlygrey.shapedrawer"/>
```

See the [jitpack website](https://jitpack.io/#space.earlygrey/shapedrawer) for more info.

Alternatively, if you're using [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff) to create your project you can find shape drawer under the "third-party" tab.


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

---

Check the [wiki](https://github.com/earlygrey/shapedrawer/wiki) for more info, including:
* [Using Shape Drawer](https://github.com/earlygrey/shapedrawer/wiki/Using-Shape-Drawer)
* [Shapes](https://github.com/earlygrey/shapedrawer/wiki/Shapes)
* [Join Types](https://github.com/earlygrey/shapedrawer/wiki/Join-Types)
* [Pixel Snapping](https://github.com/earlygrey/shapedrawer/wiki/Pixel-Snapping)


---

Test application uses the [Commodore 64 UI Skin](https://ray3k.wordpress.com/artwork/commodore-64-ui-skin-for-libgdx/) created by Raymond "Raeleus" Buckley under the [CC BY license](https://creativecommons.org/licenses/by/4.0/). [Check out the others!](https://ray3k.wordpress.com/artwork/)
