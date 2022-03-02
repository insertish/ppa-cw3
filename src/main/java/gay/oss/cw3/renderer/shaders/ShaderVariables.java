package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.glfw.GLFW.glfwExtensionSupported;

/**
 * Helper class for determining OpenGL capabilities
 * and feeding that information into shaders.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class ShaderVariables {
    private static boolean initialised = false;
    private static boolean ssboSupported = false;

    /**
     * Check the capabilities of the current OpenGL context
     */
    private static void init() {
        if (!initialised) {
            // Refer to https://www.khronos.org/registry/OpenGL/index_gl.php
            ssboSupported = glfwExtensionSupported("GL_ARB_shader_storage_buffer_object");
            initialised = true;
        }
    }

    /**
     * Check whether Shader Storage Buffers are supported
     * @return Whether SSBOs are supported in current OpenGL context
     */
    public static boolean isSsboSupported() {
        return ssboSupported;
    }

    /**
     * Get String value for shader variable
     * @param key Variable key
     * @return Variable value
     */
    public static String get(String key) {
        ShaderVariables.init();

        switch (key) {
            case "model_uniform":
                return ssboSupported
                    ? "layout(std430, binding = 0) buffer modelMatrices { mat4 model[]; };"
                    : "uniform mat4 model;";
            case "model":
                return ssboSupported
                    ? "model[gl_InstanceID]"
                    : "model";
            case "instanced_lighting":
                return ssboSupported
                    ? "#include \"lighting.body.instanced.vert\""
                    : "#include \"lighting.body.vert\"";
            default:
                System.err.println("Unknown shader variable! #[" + key + "]");
                return "";
        }
    }
}
