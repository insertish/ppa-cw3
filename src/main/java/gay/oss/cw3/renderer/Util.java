package gay.oss.cw3.renderer;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Common utility functions for working with graphics libraries
 */
public class Util {
    /**
     * Configure LWJGL for use
     * @throws IllegalStateException if it fails to initialise
     */
    public static void initialiseLWJGL() throws IllegalStateException {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }
    }
}
