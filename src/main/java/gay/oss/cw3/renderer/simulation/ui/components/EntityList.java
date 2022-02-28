package gay.oss.cw3.renderer.simulation.ui.components;

import java.util.stream.Collectors;

import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.layouts.Alignment;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;
import gay.oss.cw3.scenarios.Scenario;

/**
 * UI element which shows a list of all entities given a Scenario.
 */
public class EntityList extends FlowLayout {
    /**
     * Construct a new EntityList
     * @param scenario Scenario we should gather data from
     * @param font Font we should use for rendering text
     */
    public EntityList(Scenario scenario, Font font) {
        super(
            scenario.getEntities()
                .stream()
                .map(clazz -> new EntityBox(scenario, clazz, font))
                .collect(Collectors.toList())
        );

        this.setDirection(FlowDirection.Column);
        this.setAlignment(Alignment.End);
    }
}
