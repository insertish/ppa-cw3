package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.simulation.world.attributes.DayCycle;
import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.simulation.world.World;

import java.util.EnumMap;
import java.util.Map;

public class SimulationUI extends UI {
    private World world;

    private final Map<DayCycle, Texture> dayCycleTextures;

    public SimulationUI(World world) throws Exception {
        super();
        this.world = world;

        dayCycleTextures = new EnumMap<>(DayCycle.class);
        dayCycleTextures.put(DayCycle.MORNING, Texture.fromResource("ui/daycycle/morning.png"));
        dayCycleTextures.put(DayCycle.AFTERNOON, Texture.fromResource("ui/daycycle/afternoon.png"));
        dayCycleTextures.put(DayCycle.EVENING, Texture.fromResource("ui/daycycle/evening.png"));
        dayCycleTextures.put(DayCycle.NIGHT, Texture.fromResource("ui/daycycle/night.png"));
    }

    public void setWorld(World world) {
        this.world = world;
    }

    protected void drawUI() {
        String text = this.world.getTime() + " ticks";

        this.drawRect(24, 24, 80, 80, new Vector4f(0, 0, 0, 0.5f));
        this.drawRect(128, 24, 16 + 24 * text.length(), 40, new Vector4f(0, 0, 0, 0.5f));

        this.drawRect(32, 32, 64, 64, this.dayCycleTextures.get(this.world.getDayCycle()));

        this.drawText(136, 32, 24, text);
    }
}
