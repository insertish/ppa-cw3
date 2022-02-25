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

    public static float[] rgbToOklab(float[] color) {
        float l = (float) Math.cbrt(0.4122214708f * color[0] + 0.5363325363f * color[1] + 0.0514459929f * color[2]);
        float m = (float) Math.cbrt(0.2119034982f * color[0] + 0.6806995451f * color[1] + 0.1073969566f * color[2]);
        float s = (float) Math.cbrt(0.0883024619f * color[0] + 0.2817188376f * color[1] + 0.6299787005f * color[2]);

        return new float[] {
                0.2104542553f*l + 0.7936177850f*m - 0.0040720468f*s,
                1.9779984951f*l - 2.4285922050f*m + 0.4505937099f*s,
                0.0259040371f*l + 0.7827717662f*m - 0.8086757660f*s
        };
    }

    public static float[] oklabToRgb(float[] color) {
        float l_ = color[0] + 0.3963377774f * color[1] + 0.2158037573f * color[2];
        float m_ = color[0] - 0.1055613458f * color[1] - 0.0638541728f * color[2];
        float s_ = color[0] - 0.0894841775f * color[1] - 1.2914855480f * color[2];

        float l = l_*l_*l_;
        float m = m_*m_*m_;
        float s = s_*s_*s_;

        return new float[] {
                +4.0767416621f * l - 3.3077115913f * m + 0.2309699292f * s,
                -1.2684380046f * l + 2.6097574011f * m - 0.3413193965f * s,
                -0.0041960863f * l - 0.7034186147f * m + 1.7076147010f * s,
        };
    }

    public static float[] intColourToFloats(final int colour) {
        int r = (colour & 0x00ff0000) >> 16;
        int g = (colour & 0x0000ff00) >> 8;
        int b = (colour & 0x000000ff);

        return new float[] {
                r / 256f,
                g / 256f,
                b / 256f
        };
    }
}
