package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.UI;
import gay.oss.cw3.simulation.world.World;

public class SimulationUI extends UI {
    private World world;

    private Texture textureCycle1;
    private Texture textureCycle2;
    private Texture textureCycle3;
    private Texture textureCycle4;

    public SimulationUI(World world) throws Exception {
        super();
        this.world = world;
        this.textureCycle1 = Texture.fromResource("ui/daycycle/morning.png");
        this.textureCycle2 = Texture.fromResource("ui/daycycle/afternoon.png");
        this.textureCycle3 = Texture.fromResource("ui/daycycle/evening.png");
        this.textureCycle4 = Texture.fromResource("ui/daycycle/night.png");
    }

    public void setWorld(World world) {
        this.world = world;
    }

    protected void drawUI() {
        Texture tex;
        switch (this.world.getDayCycle()) {
            default:
            case MORNING: tex = this.textureCycle1; break;
            case AFTERNOON: tex = this.textureCycle2; break;
            case EVENING: tex = this.textureCycle3; break;
            case NIGHT: tex = this.textureCycle4;
        }

        this.drawImage(32, 32, 64, 64, tex);
    }
}
