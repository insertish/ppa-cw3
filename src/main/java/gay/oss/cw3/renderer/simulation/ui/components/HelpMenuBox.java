package gay.oss.cw3.renderer.simulation.ui.components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import java.util.Arrays;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.events.Event;
import gay.oss.cw3.renderer.ui.events.KeyEvent;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;

/**
 * UI component providing help information to the user
 */
public class HelpMenuBox extends Box {
    private boolean visible = false;
    private Node content;

    /**
     * Construct a new HelpMenuBox
     * @param font Font to use for rendering
     */
    public HelpMenuBox(Font font) {
        super(null);

        int fontSize = 26;

        this.content = new FlowLayout(Arrays.asList(
            new Node[] {
                new Text(font, "Help Menu", fontSize + 4),
                new Box(null).setMinHeight(4),
                new Box(null)
                    .setMinWidth((fontSize + 4) * 9)
                    .setMinHeight(2)
                    .setColour(new Vector4f(1, 1, 1, 1)),
                new Box(null).setMinHeight(16),
                new Text(font, "H: Toggle Help Menu", fontSize),
                new Text(font, "N: Generate New World", fontSize),
                new Text(font, "SPACE: Toggle Simulation", fontSize),
                new Text(font, "LMB DRAG: Rotate Camera", fontSize),
                new Text(font, "SCROLL: Zoom Camera", fontSize),
            }
        ))
        .setDirection(FlowDirection.Column);

        this.apply();
        this.setPadding(8);
        this.setColour(new Vector4f(0, 0, 0, 0.5f));
    }

    /**
     * Apply the current visibility state to the child.
     */
    private void apply() {
        if (this.visible) {
            this.child = this.content;
        } else {
            this.child = null;
        }
    }

    @Override
    public void handle(Event event) {
        if (event instanceof KeyEvent) {
            var keyEvent = (KeyEvent) event;
            if (keyEvent.getKey() == GLFW_KEY_H && keyEvent.getAction() == GLFW_PRESS) {
                this.visible = !this.visible;
                this.apply();
            }
        }
    }
}
