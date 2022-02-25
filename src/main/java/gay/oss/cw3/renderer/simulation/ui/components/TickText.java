package gay.oss.cw3.renderer.simulation.ui.components;

import gay.oss.cw3.renderer.simulation.ui.SimulationUI;
import gay.oss.cw3.renderer.ui.Font;
import gay.oss.cw3.renderer.ui.framework.components.Text;

public class TickText extends Text {
    private SimulationUI simUI;

    public TickText(Font font, int size, SimulationUI simUI) {
        super(font, null, size);
        this.simUI = simUI;
    }

    @Override
    public String getValue() {
        return this.simUI.getWorld().getTime() + " ticks";
    }
}
