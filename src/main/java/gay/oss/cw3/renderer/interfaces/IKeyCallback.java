package gay.oss.cw3.renderer.interfaces;

import org.lwjgl.glfw.GLFW;

public interface IKeyCallback {
    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param key       the keyboard key that was pressed or released
     * @param action    the key action. One of:<ul><li>{@link GLFW#GLFW_PRESS PRESS}</li><li>{@link GLFW#GLFW_RELEASE RELEASE}</li><li>{@link GLFW#GLFW_REPEAT REPEAT}</li></ul>
     * @param mods bitfield describing which modifiers keys were held down
     */
    void invoke(int key, int action, int mods);
}
