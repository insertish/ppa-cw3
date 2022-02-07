package gay.oss.cw3.renderer.interfaces;

public interface IKeyCallback {
    /**
     * Will be called when a key is pressed, repeated or released.
     *
     * @param key       the keyboard key that was pressed or released
     * @param action    the key action. One of:<br><table><tr><td>{@link GLFW#GLFW_PRESS PRESS}</td><td>{@link GLFW#GLFW_RELEASE RELEASE}</td><td>{@link GLFW#GLFW_REPEAT REPEAT}</td></tr></table>
     * @param modifiers bitfield describing which modifiers keys were held down
     */
    void invoke(int key, int action, int mods);
}
