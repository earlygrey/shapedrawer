package space.earlygrey.shapedrawer.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import space.earlygrey.shapedrawer.GraphDrawer;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.ShapeUtils;
import space.earlygrey.shapedrawer.test.TestUtils.RollingAverage;

public class ShapeDrawerTest extends ApplicationAdapter {

	/*
	I'll tidy this up when it's all done...
	 */

	ShapeDrawer drawer;
	GraphDrawer graphDrawer;
	Batch batch;
	Texture texture;
	Stage stage;
	Skin skin;

	float[] vertices =  new float[16];
	ShortArray triangles = new ShortArray();
	static final EarClippingTriangulator triangulator = new EarClippingTriangulator();
	Array<Vector2> path = new Array<Vector2>();

	ShapeRenderer sr;
	BitmapFont debugFont;

	ShapeMode shapeMode = ShapeMode.LINE;
	JoinType joinType = JoinType.POINTY;

	int sides = 8;
	boolean touched = false, closedPath = false;
	boolean filled = false;

	Table preview, srPreview;

	enum ShapeMode {
		LINE, PATH, POLYGON, ELLIPSE, ARC, RECTANGLE, GRAPH;
	}

	static float runTime;
	Vector2 v = new Vector2(), anchor = new Vector2(), v2 = new Vector2();

	RollingAverage average = new RollingAverage(10);

    boolean usePoly = true;

	@Override
	public void create () {

		if (usePoly) {
			batch = new PolygonSpriteBatch(32767);
		} else {
			batch = new SpriteBatch(8191);
		}

		stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), batch);

		//create single white pixel
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		texture = new Texture(pixmap);
		pixmap.dispose();
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);


		skin = new Skin(Gdx.files.internal("uiskin.json"));
		final Color lightBlue = skin.getColor("light_blue");

		drawer = new ShapeDrawer(batch, region);
		Color drawColor = new Color(1,1,1,0.6f);
		drawer.setColor(drawColor);
		sr = new ShapeRenderer();
		sr.setColor(drawColor);
		
		graphDrawer = new GraphDrawer(drawer);

		debugFont = new BitmapFont();

		final Label tipLabel = new Label("", skin);
		final Label drawerMethodLabel = new Label("ShapeDrawer#line()", skin), srMethodLabel = new Label("ShapeRenderer#rectLine()", skin);
		final Label instructionLabel = new Label("drag to move endpoint", skin);
		instructionLabel.setAlignment(Align.center);
		final TextButton clearPathButton = new TextButton("Clear", skin);
		final CheckBox dragPathCheckbox = new CheckBox("drag path", skin);
		final CheckBox closedPathCheckbox = new CheckBox("closed", skin);
        final CheckBox filledCheckbox = new CheckBox("filled", skin);

		Table root = new Table();
		root.setFillParent(true);

		final Table shapeSelectorTable = new Table(), joinSelectorTable = new Table();
		final Table widthSelectorTable = new Table(), sidesSelectorTable = new Table();

		final SelectBox<JoinType> selectBoxJoin = new SelectBox<JoinType>(skin);
		selectBoxJoin.setItems(JoinType.values());
		selectBoxJoin.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				joinType = selectBoxJoin.getSelected();
			}
		});

		final SelectBox<ShapeMode> selectBoxShape = new SelectBox<ShapeMode>(skin);
		selectBoxShape.setItems(ShapeMode.values());
		selectBoxShape.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				shapeMode = selectBoxShape.getSelected();
				switch(shapeMode) {
					case LINE:
						selectBoxJoin.setSelected(JoinType.NONE);
						joinSelectorTable.setVisible(false);
						sidesSelectorTable.setVisible(false);
						clearPathButton.setVisible(false);
						closedPathCheckbox.setVisible(false);
                        filledCheckbox.setVisible(false);
						instructionLabel.setText("drag to move endpoint");
						dragPathCheckbox.setVisible(false);
						drawerMethodLabel.setText("ShapeDrawer#line()");
						srMethodLabel.setText("ShapeRenderer#rectLine()");
						break;
					case PATH:
						selectBoxJoin.setSelected(JoinType.SMOOTH);
						joinSelectorTable.setVisible(true);
						sidesSelectorTable.setVisible(false);
						clearPathButton.setVisible(true);
						closedPathCheckbox.setVisible(true);
                        filledCheckbox.setVisible(false);
						instructionLabel.setText("click to set a waypoint");
						dragPathCheckbox.setVisible(true);
						drawerMethodLabel.setText("ShapeDrawer#path()");
						srMethodLabel.setText("ShapeRenderer#rectLine()");
						break;
					case POLYGON:
						refreshPolygonVertices();
						selectBoxJoin.setSelected(JoinType.POINTY);
						joinSelectorTable.setVisible(true);
						sidesSelectorTable.setVisible(true);
						clearPathButton.setVisible(false);
						closedPathCheckbox.setVisible(false);
                        filledCheckbox.setVisible(true);
						instructionLabel.setText("drag to adjust\nsize and rotation");
						dragPathCheckbox.setVisible(false);
						drawerMethodLabel.setText("ShapeDrawer#polygon()");
						srMethodLabel.setText("ShapeRenderer#polygon()");
						break;
					case ELLIPSE:
						selectBoxJoin.setSelected(JoinType.SMOOTH);
						joinSelectorTable.setVisible(false);
						sidesSelectorTable.setVisible(false);
						clearPathButton.setVisible(false);
						closedPathCheckbox.setVisible(false);
                        filledCheckbox.setVisible(true);
						instructionLabel.setText("drag to adjust\nsize and rotation");
						dragPathCheckbox.setVisible(false);
						drawerMethodLabel.setText("ShapeDrawer#ellipse()");
						srMethodLabel.setText("ShapeRenderer#ellipse()");
						break;
					case ARC:
						joinSelectorTable.setVisible(false);
						sidesSelectorTable.setVisible(false);
						clearPathButton.setVisible(false);
						closedPathCheckbox.setVisible(false);
                        filledCheckbox.setVisible(true);
						instructionLabel.setText("drag to adjust arc");
						dragPathCheckbox.setVisible(false);
						drawerMethodLabel.setText("ShapeDrawer#arc()");
						srMethodLabel.setText("ShapeRenderer#arc()");
						break;
					case RECTANGLE:
						selectBoxJoin.setSelected(JoinType.POINTY);
						joinSelectorTable.setVisible(false);
						sidesSelectorTable.setVisible(false);
						clearPathButton.setVisible(false);
						closedPathCheckbox.setVisible(false);
                        filledCheckbox.setVisible(true);
						instructionLabel.setText("drag to adjust size");
						dragPathCheckbox.setVisible(false);
						drawerMethodLabel.setText("ShapeDrawer#rectangle()");
						srMethodLabel.setText("ShapeRenderer#rect()");
						break;
					case GRAPH:
						selectBoxJoin.setSelected(JoinType.SMOOTH);
						joinSelectorTable.setVisible(true);
						sidesSelectorTable.setVisible(false);
						clearPathButton.setVisible(false);
						closedPathCheckbox.setVisible(false);
						filledCheckbox.setVisible(false);
						instructionLabel.setText("drag to adjust size");
						dragPathCheckbox.setVisible(false);
						drawerMethodLabel.setText("GraphDrawer#draw()");
						srMethodLabel.setText("N/A");
						break;
				}
			}
		});


		final Slider startWidthSlider = new Slider(1f, 100f, 1f, false, skin);
		final Label startWidthLabel = new Label("", skin);
		startWidthSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				drawer.setDefaultLineWidth(startWidthSlider.getValue());
				startWidthLabel.setText(String.valueOf(drawer.getDefaultLineWidth()));
				Gdx.gl.glLineWidth(startWidthSlider.getValue());
			}
		});

		final Slider endWidthSlider = new Slider(1f, 100f, 1f, false, skin);
		final Label endWidthLabel = new Label("", skin);
		endWidthSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				drawer.setEndLineWidth(endWidthSlider.getValue());
				endWidthLabel.setText(String.valueOf(drawer.getEndLineWidth()));
			}
		});

		final Slider sidesSlider = new Slider(3f, 80, 1f, false, skin);
		final Label sidesLabel = new Label("", skin);
		sidesSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				sides = (int) sidesSlider.getValue();
				sidesLabel.setText(String.valueOf(sides));
				refreshPolygonVertices();
			}
		});


		Table previewTable = new Table() {
			@Override
			public void draw(Batch batch, float parentAlpha) {
				this.localToStageCoordinates(v.set(0.5f*getWidth(), 0));
				drawer.line(0, v.y, stage.getWidth(), v.y, lightBlue, 2);
				drawer.line(0, v.y, stage.getWidth(), v.y, lightBlue, 2);
				drawer.line(v.x, v.y, v.x, v.y+30, lightBlue, 2);
				drawer.line(v.x, v.y+getHeight(), v.x, v.y+getHeight()-30, lightBlue, 2);
				super.draw(batch, parentAlpha);
			}
		};
		previewTable.add(preview = new Table()).grow().top();
		previewTable.add(srPreview = new Table()).grow().top();
		previewTable.row();
		preview.add(instructionLabel).top();
		preview.row();
		preview.add(tipLabel).expand().padBottom(4).top();
		preview.row();
		preview.add(drawerMethodLabel).padBottom(4);

		srPreview.add(srMethodLabel).expandY().padBottom(4).bottom();



		Table controlsTable = new Table();

		shapeSelectorTable.add(new Label("Shape Type", skin)).padBottom(4);
		shapeSelectorTable.row();
		shapeSelectorTable.add(selectBoxShape);

		joinSelectorTable.add(new Label("Join Type", skin)).padBottom(4);
		joinSelectorTable.row();
		joinSelectorTable.add(selectBoxJoin);

		Table startWidthLabelTable = new Table();
		Table endWidthLabelTable = new Table();
		startWidthLabelTable.add(new Label("Start Width: ", skin)).padBottom(4);
		startWidthLabelTable.add(startWidthLabel);
		endWidthLabelTable.add(new Label("End Width: ", skin)).padBottom(4);
		endWidthLabelTable.add(endWidthLabel);
		widthSelectorTable.add(startWidthLabelTable).width(300).padRight(2);
		widthSelectorTable.add(endWidthLabelTable).width(300).padLeft(2);
		widthSelectorTable.row();
		widthSelectorTable.add(startWidthSlider).width(300).padRight(2);
		widthSelectorTable.add(endWidthSlider).width(300).padLeft(2);

		Table sidesLabelTable = new Table();
		sidesLabelTable.add(new Label("Sides: ", skin)).padBottom(4);
		sidesLabelTable.add(sidesLabel);
		sidesSelectorTable.add(sidesLabelTable);
		sidesSelectorTable.row();
		sidesSelectorTable.add(sidesSlider).width(400);


		controlsTable.add(clearPathButton).padTop(10).space(30);
		controlsTable.add(closedPathCheckbox).padTop(10).space(30);
        controlsTable.add(filledCheckbox).padTop(10).space(30);
		controlsTable.add(shapeSelectorTable).padTop(10).space(30);
		controlsTable.add(joinSelectorTable).padTop(10).space(30);
		controlsTable.add(dragPathCheckbox).padTop(10).space(30);
		controlsTable.row();
		controlsTable.add(widthSelectorTable).padTop(10).colspan(6);
		controlsTable.row();
		controlsTable.add(sidesSelectorTable).padTop(10).colspan(6);


		root.add(previewTable).pad(2).grow().top().padBottom(10);
		root.row();
		root.add(controlsTable).padBottom(20);
		stage.addActor(root);

		controlsTable.setTouchable(Touchable.enabled);
		controlsTable.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				event.stop();
				return true;
			}
		});

		preview.setTouchable(Touchable.enabled);
		preview.addListener(new InputListener() {
			float lastPathAdditionRuntime;
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if (preview.hit(x, y, false)==null) return false;
				touched = true;
				if (shapeMode == ShapeMode.PATH) {
					path.add(setToMouse(new Vector2()));
					if (path.size>100) path.removeIndex(0);
				}
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				touched = false;
			}
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if (!touched || preview.hit(x, y, false)==null) return;
				setToMouse(anchor);
				if (shapeMode==ShapeMode.POLYGON) {
					refreshPolygonVertices();
				}
				if (shapeMode == ShapeMode.PATH && dragPathCheckbox.isChecked()) {
					if (runTime-lastPathAdditionRuntime > 0.05f) {
						path.add(setToMouse(new Vector2()));
						if (path.size>100) path.removeIndex(0);
						lastPathAdditionRuntime = runTime;
					}
				}

			}
		});
		clearPathButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				path.clear();
			}
		});
		closedPathCheckbox.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				closedPath = closedPathCheckbox.isChecked();
			}
		});
        filledCheckbox.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                filled = !filled;
            }
        });

		Gdx.input.setInputProcessor(stage);

		selectBoxShape.setSelected(ShapeMode.LINE);
		startWidthSlider.setValue(20);
		endWidthSlider.setValue(20);
		sidesSlider.setValue(6);
		selectBoxJoin.setSelected(JoinType.NONE);
		joinSelectorTable.setVisible(false);
		sidesSelectorTable.setVisible(false);
		clearPathButton.setVisible(false);
        filledCheckbox.setVisible(false);
		dragPathCheckbox.setVisible(false);
		closedPathCheckbox.setVisible(false);
		preview.localToStageCoordinates(v.set(preview.getWidth(), preview.getHeight()).scl(0.5f));

		anchor.set(stage.getWidth(), stage.getHeight()).scl(0.5f);
	}

	void refreshPolygonVertices() {
		if (2*sides!=vertices.length) vertices = new float[2*sides];
		srPreview.localToStageCoordinates(v.set(srPreview.getWidth(), srPreview.getHeight()).scl(0.5f));
		int srX = (int) v.x, srY = (int) v.y;
		preview.localToStageCoordinates(v.set(preview.getWidth(), preview.getHeight()).scl(0.5f));
		int X = (int) v.x, Y = (int) v.y;
		float angleInterval = MathUtils.PI2 / sides;
		float xRadius = anchor.dst(X, Y);
		float rotation = v.set(anchor).sub(X, Y).angleRad();
		float sin = (float) Math.sin(rotation), cos = (float) Math.cos(rotation);
		v2.set(1, 0);
		for (int i = 0; i < 2*sides; i+=2) {
			float x = v2.x*xRadius, y = v2.y*200;
			vertices[i] = x*cos-y*sin + srX;
			vertices[i+1] = x*sin+y*cos + srY;
			v2.rotateRad(angleInterval);
		}

		triangles = triangulator.computeTriangles(vertices);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0f, 0f, 1f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		runTime += Gdx.graphics.getDeltaTime();

		drawer.update(); //doesn't really do anything in this test

		srPreview.localToStageCoordinates(v.set(srPreview.getWidth(), srPreview.getHeight()).scl(0.5f));
		int srX = (int) v.x, srY = (int) v.y;
		preview.localToStageCoordinates(v.set(preview.getWidth(), preview.getHeight()).scl(0.5f));
		int x = (int) v.x, y = (int) v.y;
		float rotation = v.set(anchor).sub(x, y).angleRad();
		rotation = ShapeUtils.normaliseAngleToPositive(rotation);
		float scale = anchor.dst(x, y);

		switch(shapeMode) {
			case LINE: default:
				sr.begin(ShapeType.Filled);
				v.set(srX, srY).sub(x, y).add(anchor);
				sr.rectLine(srX, srY, v.x, v.y, drawer.getDefaultLineWidth());
				break;
			case PATH:
				sr.begin(ShapeType.Filled);
				for (int i = 0; i < path.size-1; i++) {
					Vector2 p1 = path.get(i), p2 = path.get(i+1);
					v.set(p1).sub(x, y).add(srX, srY);
					v2.set(p2).sub(x, y).add(srX, srY);
					sr.rectLine(v.x, v.y, v2.x, v2.y, drawer.getDefaultLineWidth());
				}
				if (closedPath && path.size >= 2) {
					v.set(path.get(path.size-1)).sub(x, y).add(srX, srY);
					v2.set(path.first()).sub(x, y).add(srX, srY);
					sr.rectLine(v.x, v.y, v2.x, v2.y, drawer.getDefaultLineWidth());
				}
				break;
			case POLYGON:
				sr.begin(ShapeType.Line);
				sr.polygon(vertices);
				break;
			case ELLIPSE:
				sr.begin(filled?ShapeType.Filled:ShapeType.Line);
				float w = scale, h = 200;
				sr.ellipse(srX-w, srY-h, 2*w, 2*h, rotation*MathUtils.radiansToDegrees);
				break;
			case ARC:
				sr.begin(filled?ShapeType.Filled:ShapeType.Line);
				sr.arc(srX, srY, 200, 0, rotation*MathUtils.radiansToDegrees);
				break;
			case RECTANGLE:
				sr.begin(filled?ShapeType.Filled:ShapeType.Line);
				w = Math.abs(anchor.x - x); h = Math.abs(anchor.y - y);
				sr.rect(srX-w, srY-h, 2*w, 2*h);

				break;
			case GRAPH:
				//no preview for ShapeRenderer because you can only draw graphs with ShapeDrawer
				break;
		}
		sr.end();


		batch.begin();

		long time = TimeUtils.nanoTime();
		switch(shapeMode) {
			case LINE: default:
                drawer.line(x, y,anchor.x, anchor.y);
				break;
			case PATH:
				drawer.path(path, joinType, !closedPath);
				break;
			case POLYGON:
				if (filled) {
					drawer.filledPolygon(x, y, sides, scale, 200, rotation);
					//drawer.filledPolygon(vertices, triangles);
				} else {
					drawer.polygon(x, y, sides, scale, 200, rotation, joinType);
				}
				break;
			case ELLIPSE:
				if (filled) {
					drawer.filledEllipse(x, y, scale, 200, rotation);
				} else {
					drawer.ellipse(x, y, scale, 200, rotation);
				}
				break;
			case ARC:
				if (filled) {
					drawer.sector(x, y, 200, 0, rotation);
				} else {
					drawer.arc(x, y, 200, 0f, rotation);
				}
				break;
			case RECTANGLE:
				int w = 2 * (int) Math.abs(anchor.x - x), h = 2 * (int) Math.abs(anchor.y - y);
				if (filled) {
					drawer.filledRectangle(x-0.5f*w, y-0.5f*h, w, h);
				} else {
					drawer.rectangle(x-0.5f*w, y-0.5f*h, w, h);
				}

				break;
			case GRAPH:
				w = 2 * (int) Math.abs(anchor.x - x);
				h = 2 * (int) Math.abs(anchor.y - y);
				graphDrawer.setJoinType(joinType);
				graphDrawer.draw(Interpolation.elastic, x-0.5f*w, y-0.5f*h, w, h);
				break;
		}

		average.add(TimeUtils.nanosToMillis(TimeUtils.timeSinceNanos(time)));

		batch.end();

		int calls = 0;
		if (batch instanceof PolygonSpriteBatch) {
			calls = ((PolygonSpriteBatch)batch).renderCalls;
		} else if (batch instanceof SpriteBatch) {
			calls = ((SpriteBatch)batch).renderCalls;
		}

		stage.act();
		stage.draw();

		batch.begin();

		debugFont.draw(batch, Gdx.graphics.getFramesPerSecond()+"fps", stage.getWidth()-100, 20);
		debugFont.draw(batch, "draw: "+average+"ms", stage.getWidth()-100, 40);
		debugFont.draw(batch, "calls: "+calls, stage.getWidth()-100, 60);

		batch.end();


	}
	

	Vector2 setToMouse(Vector2 vec) {
		return vec.set(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.getViewport().update(width, height);
	}


	@Override
	public void dispose () {
		batch.dispose();
		texture.dispose();
		stage.dispose();
		skin.dispose();
		sr.dispose();
		debugFont.dispose();
	}


}
