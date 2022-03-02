package gay.oss.cw3.renderer.simulation.ui.components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import java.util.Arrays;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.events.Event;
import gay.oss.cw3.renderer.ui.events.KeyEvent;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.Alignment;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;

/**
 * UI component providing welcome information to the user
 */
public class WelcomeBox extends Box {
    private boolean visible = true;
    private Node content;

    /**
     * Construct a new WelcomeBox
     * @param font Font to use for rendering
     */
    public WelcomeBox(Font font) {
        super(null);

        int fontSize = 24;

        this.content = new FlowLayout(Arrays.asList(
            new Node[] {
                new Text(font, "WELCOME!", fontSize + 4),
                new Box(null).setMinHeight(4),
                new Box(null)
                    .setMinWidth((fontSize + 4) * 8)
                    .setMinHeight(2)
                    .setColour(new Vector4f(1, 1, 1, 1)),
                new Box(null).setMinHeight(fontSize),
                new Text(font, "First time?", fontSize),
                new FlowLayout(Arrays.asList(
                    new Node[] {
                        new Text(font, "Press", fontSize),
                        new Text(font, "H", fontSize)
                            .setColour(new Vector4f(0.5f, 0.25f, 1.0f, 1.0f)),
                        new Text(font, "for Controls", fontSize),
                    }
                ))
                .setGap(fontSize),
                new Box(null).setMinHeight(fontSize),
                new FlowLayout(Arrays.asList(
                    new Node[] {
                        new Text(font, "SPACE", fontSize)
                            .setColour(new Vector4f(0.5f, 0.25f, 1.0f, 1.0f)),
                        new Text(font, "to Start", fontSize)
                    }
                ))
                .setGap(fontSize),
            }
        ))
        .setDirection(FlowDirection.Column)
        .setAlignment(Alignment.Center);

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
            if ((keyEvent.getKey() == GLFW_KEY_SPACE || keyEvent.getKey() == GLFW_KEY_H)
                && keyEvent.getAction() == GLFW_PRESS) {
                this.visible = false;
                this.apply();
            }
        }
    }
}
