package gay.oss.cw3.renderer.simulation.ui.components;

import java.util.Arrays;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;
import gay.oss.cw3.scenarios.Scenario;

/**
 * UI element representing data from a single Entity type.
 */
public class EntityBox extends Box {
    private Scenario scenario;
    private Class<?> clazz;

    private Text count;

    /**
     * Construct a new EntityBox
     * @param scenario Scenario we should gather data from
     * @param clazz Relevant entity class
     * @param font Font we should render text with
     */
    public EntityBox(Scenario scenario, Class<?> clazz, Font font) {
        super(null);

        this.count = new Text(font, "", 20);
        this.child = new FlowLayout(
            Arrays.asList(new Node[] {
                count,
                new Text(font, scenario.getEntityName(clazz), 20)
            })
        )
        .setDirection(FlowDirection.Row)
        .setGap(20);

        this.setColour(new Vector4f(0, 0, 0, 0.5f));
        this.setPadding(8);

        this.scenario = scenario;
        this.clazz = clazz;
    }
    
    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        int count = scenario.getWorld().getEntityCount(clazz);

        if (count > 0) {
            this.count.setValue("" + count);
        } else {
            this.count.setValue("DEAD");
            this.count.setColour(new Vector4f(1, 0, 0, 1));
        }

        super.draw(ui, x, y, w, h);
    }
}
