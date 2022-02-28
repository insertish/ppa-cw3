package gay.oss.cw3.renderer.simulation.ui.components;

import java.util.Arrays;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;

public class HelpMenuBox extends Box {
    public HelpMenuBox(Font font) {
        super(
            new FlowLayout(Arrays.asList(
                new Node[] {
                    new Text(font, "Help Menu", 24),
                    new Box(null).setMinHeight(4),
                    new Box(null)
                        .setMinWidth(216)
                        .setMinHeight(2)
                        .setColour(new Vector4f(1, 1, 1, 1)),
                    new Box(null).setMinHeight(16),
                    new Text(font, "H: Toggle Help Menu", 20),
                    new Text(font, "N: Generate New World", 20),
                    new Text(font, "SPACE: Toggle Simulation", 20),
                    new Text(font, "LMB DRAG: Rotate Camera", 20),
                    new Text(font, "SCROLL: Zoom Camera", 20),
                    new Box(null).setMinHeight(16),
                    new Text(font, "Press SPACE to Start", 20)
                        .setColour(new Vector4f(0.7f, 0.18f, 1.0f, 1.0f)),
                }
            ))
            .setDirection(FlowDirection.Column)
        );

        this.setPadding(8);
        this.setColour(new Vector4f(0, 0, 0, 0.75f));
    }
}
