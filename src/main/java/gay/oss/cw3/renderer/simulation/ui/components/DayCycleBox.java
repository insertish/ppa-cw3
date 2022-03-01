package gay.oss.cw3.renderer.simulation.ui.components;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.simulation.ui.SimulationUI;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.components.Image;
import gay.oss.cw3.simulation.world.attributes.DayCycle;

/**
 * UI element which shows an indicator of the current day cycle.
 */
public class DayCycleBox extends Box {
    private SimulationUI simulationUI;
    private Map<DayCycle, Texture> textures;

    /**
     * Construct a new DayCycleBox
     * @param simulationUI Simulation UI
     * @throws IOException if it fails to initialise textures
     */
    public DayCycleBox(SimulationUI simulationUI) throws IOException {
        super(new Image(null));
        this.simulationUI = simulationUI;

        textures = new EnumMap<>(DayCycle.class);
        textures.put(DayCycle.MORNING, Resources.getTexture("ui/daycycle/morning.png"));
        textures.put(DayCycle.AFTERNOON, Resources.getTexture("ui/daycycle/afternoon.png"));
        textures.put(DayCycle.EVENING, Resources.getTexture("ui/daycycle/evening.png"));
        textures.put(DayCycle.NIGHT, Resources.getTexture("ui/daycycle/night.png"));

        this.setPadding(8);
        this.setColour(new Vector4f(0, 0, 0, 0.5f));
    }
    
    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        // We pull the world out of the simulation UI as
        // it may change at any moment.
        ((Image) this.child).setTexture(this.textures.get(this.simulationUI.getWorld().getDayCycle()));
        super.draw(ui, x, y, w, h);
    }
}
