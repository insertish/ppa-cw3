package gay.oss.cw3.renderer;

import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;

/**
 * Common utility functions for working with graphics
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
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

    /**
     * Process a set of 3 vertices to calculate a face normal vector
     * @param x0 X position of vector 0
     * @param y0 Y position of vector 0
     * @param z0 Z position of vector 0
     * @param x1 X position of vector 1
     * @param y1 Y position of vector 1
     * @param z1 Z position of vector 1
     * @param x2 X position of vector 2
     * @param y2 Y position of vector 2
     * @param z2 Z position of vector 2
     * @return Face Normal Vector
     */
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

    /**
     * Infer the positions of vertex data from a given pointer to calculate a face normal
     * @param data Float data to index into
     * @param index Pointer to first element to read from
     * @return Face Normal Vector
     */
    public static float[] calculateNormalFromPointer(float data[], int index) {
        return calculateNormal(
            data[index  ], data[index+1], data[index+2],
            data[index+3], data[index+4], data[index+5],
            data[index+6], data[index+7], data[index+8]
        );
    }

    /**
     * Convert an RGB colour space vector to Oklab colour space
     * @param color RGB colour space vector
     * @return Oklab colour space vector
     */
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

    /**
     * Convert an Oklab colour space vector to RGB colour space
     * @param color Oklab colour space vector
     * @return RGB colour space vector
     */
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

    /**
     * Convert a given integer to an RGB colour space vector
     * @param colour Integer representing a colour
     * @return RGB colour space vector
     */
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

    /**
     * Given a certain height, adjust its value to conform with a plane.
     * @param height Height value
     * @param x X position
     * @param z Z position
     * @param width World Width
     * @param depth World Depth
     * @param BEACH_DISTANCE_FRACTION Fraction used for beach
     * @param SEA_DISTANCE_FRACTION Fraction used for sea
     * @param TARGET_HEIGHT Target plane height
     * @return Computed Height
     */
    public static float adjustHeightToPlane(final float height, final int x, final int z, final int width, final int depth, final float BEACH_DISTANCE_FRACTION, final float SEA_DISTANCE_FRACTION, final float TARGET_HEIGHT) {
        final int adjustedX = x - width/2;
        final int adjustedZ = z - depth/2;
        final float beachDistance = BEACH_DISTANCE_FRACTION*0.5f*width;
        final float seaDistance = SEA_DISTANCE_FRACTION*0.5f*width;
        float distToCentre = (float) Math.sqrt((adjustedX * adjustedX) + (adjustedZ * adjustedZ));

        if (distToCentre < beachDistance) {
            return height;
        }

        if (distToCentre > seaDistance) {
            return TARGET_HEIGHT;
        }

        var factor = easeInOutCubic((distToCentre-beachDistance)/(seaDistance-beachDistance));

        return (1f-factor)*height + factor*TARGET_HEIGHT;
    }

    /**
     * Perform easeInOutCubic on a given value.
     * @param x Input
     * @return Output
     */
    private static float easeInOutCubic(float x) {
        return x < 0.5f ? 4f * x * x * x : (float) (1f - Math.pow(-2f * x + 2f, 3f) / 2f);
    }
}
