package gay.oss.cw3.renderer.simulation.ui.components;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import java.io.IOException;
import java.util.Arrays;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.ui.events.Event;
import gay.oss.cw3.renderer.ui.events.KeyEvent;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Image;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.Alignment;
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
    public HelpMenuBox(Font font) throws IOException {
        super(null);

        int fontSize = 26;

        this.content = new FlowLayout(Arrays.asList(
            new Node[] {
                new FlowLayout(Arrays.asList(
                    new Node[] {
                        new Text(font, "Help Menu", fontSize + 4),
                        new Box(null).setMinHeight(4),
                        new Box(null)
                            .setMinWidth((fontSize + 4) * 9)
                            .setMinHeight(2)
                            .setColour(new Vector4f(1, 1, 1, 1)),
                        new Box(null).setMinHeight(fontSize),
                        new Text(font, "H: Toggle Help Menu", fontSize),
                        new Text(font, "N: Generate New World", fontSize),
                        new Text(font, "P: Toggle Particles", fontSize),
                        new Text(font, "SPACE: Toggle Simulation", fontSize),
                        new Text(font, "LMB DRAG: Rotate Camera", fontSize),
                        new Text(font, "SCROLL: Zoom Camera", fontSize),
                        new Text(font, "ESCAPE: Quit Simulation", fontSize),
                    }
                ))
                .setDirection(FlowDirection.Column),
                new Box(null).setMinHeight(fontSize),
                new Text(font, "Credit", fontSize + 4),
                new Box(null).setMinHeight(4),
                new Box(null)
                    .setMinWidth((fontSize + 4) * 6)
                    .setMinHeight(2)
                    .setColour(new Vector4f(1, 1, 1, 1)),
                new Box(null).setMinHeight(fontSize),
                new FlowLayout(Arrays.asList(
                    new Node[] {
                        new FlowLayout(Arrays.asList(
                            new Node[] {
                                new Box(new Image(Resources.getTexture("ui/insert_r.png")))
                                    .setMinSize(96),
                                new Text(font, "@insertish", fontSize),
                            }
                        ))
                        .setDirection(FlowDirection.Column)
                        .setAlignment(Alignment.Center)
                        .setGap(8),
                        new FlowLayout(Arrays.asList(
                            new Node[] {
                                new Box(new Image(Resources.getTexture("ui/will_r.png")))
                                    .setMinSize(96),
                                new Text(font, "@williambl", fontSize),
                            }
                        ))
                        .setDirection(FlowDirection.Column)
                        .setAlignment(Alignment.Center)
                        .setGap(8),
                    }
                ))
                .setGap(48)
            }
        ))
        .setDirection(FlowDirection.Column)
        .setAlignment(Alignment.Center);

        this.apply();
        this.setPadding(8);
        this.setColour(new Vector4f(0, 0, 0, 0.85f));
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
