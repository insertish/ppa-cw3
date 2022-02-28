package gay.oss.cw3.renderer.simulation.ui.components;

import gay.oss.cw3.renderer.simulation.ui.SimulationUI;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.components.Text;

/**
 * UI element which shows the current World's tick / time.
 */
public class TickText extends Text {
    private SimulationUI simulationUI;

    /**
     * Construct a new TickText
     * @param font Font to render text with
     * @param size Size of the text to render
     * @param simulationUI Simulation UI relevant to this element
     */
    public TickText(Font font, int size, SimulationUI simulationUI) {
        super(font, null, size);
        this.simulationUI = simulationUI;
    }

    @Override
    public String getValue() {
        // We pull the World out of the simulation UI as
        // it may change at any moment.
        return this.simulationUI.getWorld().getTime() + " ticks";
    }
}
