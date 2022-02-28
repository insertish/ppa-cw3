package gay.oss.cw3.renderer.interfaces;

import org.lwjgl.glfw.GLFW;

/**
 * Mouse button callback, this is a partial of GLFWMouseButtonCallbackI.
 */
public interface IMouseButtonCallback {
    /**
     * Will be called when a mouse button is pressed or released.
     *
     * @param button the mouse button that was pressed or released
     * @param action the button action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td></tr></table>
     * @param mods   bitfield describing which modifiers keys were held down
     */
    void invoke(int button, int action, int mods);
}
