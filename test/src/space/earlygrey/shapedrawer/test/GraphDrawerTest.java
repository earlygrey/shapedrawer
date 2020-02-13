package space.earlygrey.shapedrawer.test;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import space.earlygrey.shapedrawer.GraphDrawer;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import space.earlygrey.shapedrawer.scene2d.GraphDrawerDrawable;

import java.lang.reflect.Field;

/**
 * A demonstration of the {@link GraphDrawer} and {@link GraphDrawerDrawable} capabilities. Contains a full
 * demonstration of all libGDX interpolations.
 *
 * @author Raymond "Raeleus" Buckley
 */
public class GraphDrawerTest extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;
    private ShapeDrawer shapeDrawer;
    private GraphDrawer graphDrawer;
    private GraphDrawerDrawable graphDrawerDrawable;
    
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 800;
        config.samples = 4;
        new LwjglApplication(new GraphDrawerTest(), config);
    }
    
    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        
        shapeDrawer = new ShapeDrawer(stage.getBatch(), skin.getRegion("white"));
        graphDrawer = new GraphDrawer(shapeDrawer);
        
        graphDrawerDrawable = new GraphDrawerDrawable(graphDrawer);
        graphDrawerDrawable.setColor(skin.getColor("light_blue"));
        graphDrawerDrawable.interpolation = Interpolation.elastic;
        graphDrawerDrawable.setJoinType(JoinType.SMOOTH);
        graphDrawerDrawable.setLineWidth(2);
        graphDrawerDrawable.setSamples(200);
    
        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);
    
        root.pad(20);
        root.defaults().space(12);
        Image image = new Image(graphDrawerDrawable);
        root.add(image).grow();
        
        root.row();
        Table table = new Table();
        root.add(table).growX();
        
        table.defaults().space(8).right();
        Label label = new Label("Interpolation:", skin);
        table.add(label);
    
        final Array<Interpolation> interpolations = getInterpolations();
        final SelectBox<String> interpolationSelectBox = new SelectBox<String>(skin);
        interpolationSelectBox.setItems(getInterpolationNames());
        interpolationSelectBox.setSelectedIndex(interpolations.indexOf(graphDrawerDrawable.getInterpolation(), true));
        table.add(interpolationSelectBox).uniformX().fillX();
        interpolationSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                graphDrawerDrawable.setInterpolation(interpolations.get(interpolationSelectBox.getSelectedIndex()));
            }
        });
        
        table.row();
        label = new Label("Joint Type:", skin);
        table.add(label);
    
        final SelectBox<JoinType> joinTypeSelectBox = new SelectBox<JoinType>(skin);
        joinTypeSelectBox.setItems(JoinType.values());
        joinTypeSelectBox.setSelected(graphDrawerDrawable.joinType);
        table.add(joinTypeSelectBox).uniformX().fillX();
        joinTypeSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                graphDrawerDrawable.setJoinType(joinTypeSelectBox.getSelected());
            }
        });
        
        root.row();
        table = new Table();
        root.add(table);
    
        table.defaults().space(8).right();
        label = new Label("Line Width:", skin);
        table.add(label);
        
        final Slider lineWidthSlider = new Slider(0, 10, 1, false, skin);
        lineWidthSlider.setValue(graphDrawerDrawable.getLineWidth());
        table.add(lineWidthSlider);
        
        final Label lineWidthLabel = new Label(Integer.toString(MathUtils.round(graphDrawerDrawable.getLineWidth())), skin);
        table.add(lineWidthLabel).minWidth(50).uniformX();
        lineWidthSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                lineWidthLabel.setText(Integer.toString(MathUtils.round(lineWidthSlider.getValue())));
                graphDrawerDrawable.setLineWidth(lineWidthSlider.getValue());
            }
        });
    
        label = new Label("Samples:", skin);
        table.add(label);
    
        final Slider samplesSlider = new Slider(3, 300, 1, false, skin);
        samplesSlider.setValue(graphDrawerDrawable.getSamples());
        table.add(samplesSlider);
    
        final Label samplesLabel = new Label(Integer.toString(MathUtils.round(graphDrawerDrawable.getSamples())), skin);
        table.add(samplesLabel).uniformX().fillX();
        samplesSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                samplesLabel.setText(Integer.toString(MathUtils.round(samplesSlider.getValue())));
                graphDrawerDrawable.setSamples(MathUtils.round(samplesSlider.getValue()));
            }
        });
        
        table.row();
        label = new Label("Plot Begin:", skin);
        table.add(label);
    
        final Slider plotBeginSlider = new Slider(-2, graphDrawerDrawable.domainEnd - .01f, .01f, false, skin);
        plotBeginSlider.setValue(graphDrawerDrawable.getDomainBegin());
        table.add(plotBeginSlider);
    
        final Label plotBeginLabel = new Label(Integer.toString(MathUtils.round(graphDrawerDrawable.getDomainBegin())), skin);
        table.add(plotBeginLabel).uniformX().fillX();
        
        label = new Label("Plot End:", skin);
        table.add(label);
    
        final Slider plotEndSlider = new Slider(graphDrawerDrawable.domainBegin + .01f, 2, .01f, false, skin);
        plotEndSlider.setValue(graphDrawerDrawable.getDomainEnd());
        table.add(plotEndSlider);
    
        final Label plotEndLabel = new Label(Integer.toString(MathUtils.round(graphDrawerDrawable.getDomainEnd())), skin);
        table.add(plotEndLabel).uniformX().fillX();
        
        table.row();
        label = new Label("Domain Begin:", skin);
        table.add(label);
    
        final Slider domainBeginSlider = new Slider(graphDrawerDrawable.domainBegin, graphDrawerDrawable.plotEnd - .01f, .01f, false, skin);
        domainBeginSlider.setValue(graphDrawerDrawable.getPlotBegin());
        table.add(domainBeginSlider);
    
        final Label domainBeginLabel = new Label(Integer.toString(MathUtils.round(graphDrawerDrawable.getPlotBegin())), skin);
        table.add(domainBeginLabel).uniformX().fillX();
        
        label = new Label("Domain End:", skin);
        table.add(label);
    
        final Slider domainEndSlider = new Slider(graphDrawerDrawable.plotBegin + .01f, graphDrawerDrawable.domainEnd, .01f, false, skin);
        domainEndSlider.setValue(graphDrawerDrawable.getPlotEnd());
        table.add(domainEndSlider);
    
        final Label domainEndLabel = new Label(Integer.toString(MathUtils.round(graphDrawerDrawable.getPlotEnd())), skin);
        table.add(domainEndLabel).uniformX().fillX();
        plotBeginSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                plotBeginLabel.setText(Integer.toString(MathUtils.round(plotBeginSlider.getValue())));
                graphDrawerDrawable.setPlotBegin(plotBeginSlider.getValue());
                plotEndSlider.setRange(plotBeginSlider.getValue() + .01f, plotEndSlider.getMaxValue());
                domainBeginSlider.setRange(plotBeginSlider.getValue(), domainBeginSlider.getMaxValue());
                domainBeginSlider.setValue(plotBeginSlider.getValue());
            }
        });
        plotEndSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                plotEndLabel.setText(Integer.toString(MathUtils.round(plotEndSlider.getValue())));
                graphDrawerDrawable.setPlotEnd(plotEndSlider.getValue());
                plotBeginSlider.setRange(plotBeginSlider.getMinValue(), plotEndSlider.getValue() - .01f);
                domainEndSlider.setRange(domainEndSlider.getMinValue(), plotEndSlider.getValue());
                domainEndSlider.setValue(plotEndSlider.getValue());
            }
        });
        domainBeginSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                domainBeginLabel.setText(Integer.toString(MathUtils.round(domainBeginSlider.getValue())));
                graphDrawerDrawable.setDomainBegin(domainBeginSlider.getValue());
                domainEndSlider.setRange(domainBeginSlider.getValue() + .01f, domainEndSlider.getMaxValue());
            }
        });
        domainEndSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                domainEndLabel.setText(Integer.toString(MathUtils.round(domainEndSlider.getValue())));
                graphDrawerDrawable.setDomainEnd(domainEndSlider.getValue());
                domainBeginSlider.setRange(domainBeginSlider.getMinValue(), domainEndSlider.getValue() - .01f);
            }
        });
        
        root.row();
        final TextButton textButton = new TextButton("Resize", skin, "toggle");
        textButton.setChecked(graphDrawerDrawable.rescale);
        root.add(textButton);
        textButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                graphDrawerDrawable.setRescale(textButton.isChecked());
            }
        });
    }
    
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0f, 0f, 1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        stage.act();
        stage.draw();
    }
    
    @Override
    public void dispose() {
        skin.dispose();
    }
    
    /**
     * Returns all the default interpolations available. This method uses reflection and thereby not usable in GWT.
     * @return The {@link Array} of {@link Interpolation} instances.
     */
    public static Array<Interpolation> getInterpolations() {
        Array<Interpolation> array = new Array<Interpolation>();
        try {
            for (Field field : Interpolation.class.getFields()) {
                if (Interpolation.class.isAssignableFrom(field.getType())) {
                    array.add((Interpolation) field.get(null));
                }
            }
        } catch (IllegalAccessException e) { }
        
        return array;
    }
    
    public static Array<String> getInterpolationNames() {
        Array<String> array = new Array<String>();
        for (Field field : Interpolation.class.getFields()) {
            if (Interpolation.class.isAssignableFrom(field.getType())) {
                array.add(field.getName());
            }
        }
        
        return array;
    }
}