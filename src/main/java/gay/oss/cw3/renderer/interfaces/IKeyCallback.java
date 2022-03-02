package gay.oss.cw3.renderer.interfaces;

import org.lwjgl.glfw.GLFW;

/**
 * Key press callback, this is a partial of GLFWKeyCallbackI.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
@FunctionalInterface
public interface IKeyCallback {
    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param key    The keyboard key that was pressed or released
     * @param action The key action. One of:<ul><li>{@link GLFW#GLFW_PRESS PRESS}</li><li>{@link GLFW#GLFW_RELEASE RELEASE}</li><li>{@link GLFW#GLFW_REPEAT REPEAT}</li></ul>
     * @param mods   Bitfield describing which modifiers keys were held down
     */
    void invoke(int key, int action, int mods);
}
