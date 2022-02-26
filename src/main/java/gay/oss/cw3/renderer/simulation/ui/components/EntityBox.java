package gay.oss.cw3.renderer.simulation.ui.components;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.Font;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.scenarios.Scenario;

public class EntityBox extends Box {
    private Scenario scenario;
    private Class<?> clazz;

    public EntityBox(Scenario scenario, Class<?> clazz, Font font) {
        super(new Text(font, "", 20));

        this.setColour(new Vector4f(0, 0, 0, 0.5f));
        this.setPadding(8);

        this.scenario = scenario;
        this.clazz = clazz;
    }
    
    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        ((Text) this.child).setValue(
            scenario.getWorld().getEntityCount(clazz)
            + " " + scenario.getEntityName(clazz)
        );
        
        super.draw(ui, x, y, w, h);
    }
}
