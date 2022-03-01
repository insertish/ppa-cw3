package gay.oss.cw3.renderer.ui.events;

import org.lwjgl.glfw.GLFW;

/**
 * Event representing keyboard input
 */
public class KeyEvent extends Event {
    private int key;
    private int action;
    private int modifiers;

    /**
     * Construct a new KeyEvent
     * @param key The keyboard key that was pressed
     * @param action The key action. One of:<ul><li>{@link GLFW#GLFW_PRESS PRESS}</li><li>{@link GLFW#GLFW_RELEASE RELEASE}</li><li>{@link GLFW#GLFW_REPEAT REPEAT}</li></ul>
     * @param modifiers Bitfield describing which modifiers keys were held down
     */
    public KeyEvent(int key, int action, int modifiers) {
        this.key = key;
        this.action = action;
        this.modifiers = modifiers;
    }

    /**
     * Get the keyboard key that was pressed
     * @return Key that was pressed
     */
    public int getKey() {
        return this.key;
    }

    /**
     * Get the key action
     * @return Key action
     */
    public int getAction() {
        return this.action;
    }

    /**
     * Get the bitfield describing modifier keys held down
     * @return Bitfield describing modifier keys held down
     */
    public int getModifiers() {
        return this.modifiers;
    }
}
