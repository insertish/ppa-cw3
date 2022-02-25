package gay.oss.cw3.renderer.simulation.ui;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.simulation.ui.components.EntityList;
import gay.oss.cw3.renderer.simulation.ui.components.TickText;
import gay.oss.cw3.renderer.ui.Font;
import gay.oss.cw3.renderer.ui.FontRetro;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Image;
import gay.oss.cw3.renderer.ui.framework.layouts.Anchor;
import gay.oss.cw3.renderer.ui.framework.layouts.AnchorLayout;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;
import gay.oss.cw3.scenarios.Scenario;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;

public class SimulationUI extends UI {
    private Scenario scenario;

    private Node uiRoot;
    private Image dayCycleIndicator;
    
    private Map<DayCycle, Texture> dayCycleTextures;

    public SimulationUI(Scenario scenario) throws Exception {
        super();
        this.scenario = scenario;
        this.init();
    }

    private void init() throws Exception {
        dayCycleTextures = new EnumMap<>(DayCycle.class);
        dayCycleTextures.put(DayCycle.MORNING, Texture.fromResource("ui/daycycle/morning.png"));
        dayCycleTextures.put(DayCycle.AFTERNOON, Texture.fromResource("ui/daycycle/afternoon.png"));
        dayCycleTextures.put(DayCycle.EVENING, Texture.fromResource("ui/daycycle/evening.png"));
        dayCycleTextures.put(DayCycle.NIGHT, Texture.fromResource("ui/daycycle/night.png"));

        Font font = new FontRetro();
        this.dayCycleIndicator = new Image(null);
        this.uiRoot = new Box(
            new AnchorLayout()
                .add(
                    Anchor.TopLeft,
                    new FlowLayout(Arrays.asList(
                        new Node[] {
                            new Box(this.dayCycleIndicator)
                                .setPadding(8)
                                .setColour(new Vector4f(0, 0, 0, 0.5f))
                                .setMinWidth(64)
                                .setMinHeight(64),
                            new Box(new TickText(font, 24, this))
                                .setPadding(8)
                                .setColour(new Vector4f(0, 0, 0, 0.5f))
                        }
                    ))
                    .setGap(8)
                )
                .add(
                    Anchor.BottomRight,
                    new EntityList(this.scenario, font)
                )
                /*.add(
                    Anchor.BottomLeft,
                    new Text(font, "press h for help menu", 16)
                )*/
        )
        .setPadding(24);
    }

    public World getWorld() {
        return this.scenario.getWorld();
    }

    public Scenario getScenario() {
        return this.scenario;
    }

    public void setScenario(Scenario scenario) {
        this.scenario = scenario;

        try {
            this.init();
        } catch (Exception e) {
            System.err.println("Failed to reinitialise simulation UI!");
            e.printStackTrace();
        }
    }

    protected void drawUI(int width, int height) {
        this.dayCycleIndicator.setTexture(this.dayCycleTextures.get(this.getWorld().getDayCycle()));
        this.uiRoot.draw(this, 0, 0, width, height);
    }
}
