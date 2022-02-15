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

    public static float[] calculateNormal(float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2) {
        // Based on pseudo-code from Khronos OpenGL wiki
        // https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal#Pseudo-code
        float ux = x1 - x0;
        float uy = y1 - y0;
        float uz = z1 - z0;

        float vx = x2 - x0;
        float vy = y2 - y0;
        float vz = z2 - z0;

        return new float[] {
            (uy * vz) - (uz * vy),
            (uz * vx) - (ux * vz),
            (ux * vy) - (uy * vx)
        };
    }

    public static float[] calculateNormalFromPointer(float data[], int index) {
        return calculateNormal(
            data[index  ], data[index+1], data[index+2],
            data[index+3], data[index+4], data[index+5],
            data[index+6], data[index+7], data[index+8]
        );
    }
}
