package gay.oss.cw3.renderer.simulation;

import java.util.EnumMap;
import java.util.Map;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.ui.Font;
import gay.oss.cw3.renderer.ui.FontRetro;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Image;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.Anchor;
import gay.oss.cw3.renderer.ui.framework.layouts.AnchorLayout;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;

public class SimulationUI extends UI {
    private World world;

    private Node uiRoot;
    private Text tickText;
    private Image dayCycleIndicator;
    
    private final Map<DayCycle, Texture> dayCycleTextures;

    public SimulationUI(World world) throws Exception {
        super();
        this.world = world;

        dayCycleTextures = new EnumMap<>(DayCycle.class);
        dayCycleTextures.put(DayCycle.MORNING, Texture.fromResource("ui/daycycle/morning.png"));
        dayCycleTextures.put(DayCycle.AFTERNOON, Texture.fromResource("ui/daycycle/afternoon.png"));
        dayCycleTextures.put(DayCycle.EVENING, Texture.fromResource("ui/daycycle/evening.png"));
        dayCycleTextures.put(DayCycle.NIGHT, Texture.fromResource("ui/daycycle/night.png"));

        Font font = new FontRetro();
        this.tickText = new Text(font, "0 ticks", 24);
        this.dayCycleIndicator = new Image(null);
        this.uiRoot = new Box(
            new AnchorLayout()
                .add(
                    Anchor.TopLeft,
                    new FlowLayout(new Node[] {
                        new Box(this.dayCycleIndicator)
                            .setPadding(8)
                            .setColour(new Vector4f(0, 0, 0, 0.5f))
                            .setMinWidth(64)
                            .setMinHeight(64),
                        new Box(this.tickText)
                            .setPadding(8)
                            .setColour(new Vector4f(0, 0, 0, 0.5f))
                    })
                    .setGap(8)
                )
                .add(
                    Anchor.BottomRight,
                    new FlowLayout(new Node[] {
                        new Box(new Text(font, "interesting", 20))
                            .setPadding(8)
                            .setColour(new Vector4f(0, 0, 0, 0.5f)),
                        new Box(new Text(font, "interesting", 20))
                            .setPadding(8)
                            .setColour(new Vector4f(0, 0, 0, 0.5f)),
                        new Box(new Text(font, "interesting", 20))
                            .setPadding(8)
                            .setColour(new Vector4f(0, 0, 0, 0.5f))
                    })
                    .setGap(8)
                    .setDirection(FlowDirection.Column)
                )
                /*.add(
                    Anchor.BottomLeft,
                    new Text(font, "press h for help menu", 16)
                )*/
        )
        .setPadding(24);
    }

    public void setWorld(World world) {
        this.world = world;
    }

    protected void drawUI(int width, int height) {
        String text = this.world.getTime() + " ticks";

        this.tickText.setValue(text);
        this.dayCycleIndicator.setTexture(this.dayCycleTextures.get(this.world.getDayCycle()));
        this.uiRoot.draw(this, 0, 0, width, height);
    }
}
