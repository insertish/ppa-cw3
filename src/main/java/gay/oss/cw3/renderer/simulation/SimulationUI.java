package gay.oss.cw3.renderer.simulation;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.simulation.world.World;

public class SimulationUI extends UI {
    private World world;

    private Texture[] dayCycleTextures;

    public SimulationUI(World world) throws Exception {
        super();
        this.world = world;

        dayCycleTextures = new Texture[] {
            Texture.fromResource("ui/daycycle/morning.png"),
            Texture.fromResource("ui/daycycle/afternoon.png"),
            Texture.fromResource("ui/daycycle/evening.png"),
            Texture.fromResource("ui/daycycle/night.png")
        };
    }

    public void setWorld(World world) {
        this.world = world;
    }

    protected void drawUI() {
        String text = this.world.getTime() + " ticks";

        this.drawRect(24, 24, 80, 80, new Vector4f(0, 0, 0, 0.5f));
        this.drawRect(128, 24, 16 + 24 * text.length(), 40, new Vector4f(0, 0, 0, 0.5f));

        this.drawRect(32, 32, 64, 64, this.dayCycleTextures[this.world.getDayCycle().getIndex()]);

        this.drawText(136, 32, 24, text);
    }
}
