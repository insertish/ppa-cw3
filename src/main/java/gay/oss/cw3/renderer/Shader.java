package gay.oss.cw3.renderer;

import static org.lwjgl.opengl.GL20.*;

/**
 * Wrapper class around an OpenGL shader
 */
public class Shader {
    private final int id;

    /**
     * Construct a new shader with a valid shader ID
     * @param id Shader ID
     */
    private Shader(int id) {
        this.id = id;
    }

    /**
     * Get the ID of this shader
     * @return Shader ID
     */
    public int getID() {
        return this.id;
    }

    /**
     * Create a new shader from the provided source code
     * @param type Type of shader to be created. One of: {@link org.lwjgl.opengl.GL20C#GL_VERTEX_SHADER VERTEX_SHADER} {@link org.lwjgl.opengl.GL20C#GL_FRAGMENT_SHADER FRAGMENT_SHADER} {@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GEOMETRY_SHADER} {@link org.lwjgl.opengl.GL40#GL_TESS_CONTROL_SHADER TESS_CONTROL_SHADER} {@link org.lwjgl.opengl.GL40#GL_TESS_EVALUATION_SHADER TESS_EVALUATION_SHADER}
     * @param source GLSL source code
     * @return Newly constructed {@link Shader}
     */
    public static Shader create(int type, String source) {
        final int id = glCreateShader(type);

        glShaderSource(id, source);
        glCompileShader(id);
        
        // FIXME: handle failure

        return new Shader(id);
    }
}
