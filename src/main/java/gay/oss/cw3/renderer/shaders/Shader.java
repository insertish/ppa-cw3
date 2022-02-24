package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.regex.Pattern;

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
     * Delete this shader
     */
    public void delete() {
        glDeleteShader(this.id);
    }

    /**
     * Create a new shader from the provided source code
     * @param type Type of shader to be created. One of: {@link org.lwjgl.opengl.GL20C#GL_VERTEX_SHADER VERTEX_SHADER} {@link org.lwjgl.opengl.GL20C#GL_FRAGMENT_SHADER FRAGMENT_SHADER} {@link org.lwjgl.opengl.GL32#GL_GEOMETRY_SHADER GEOMETRY_SHADER} {@link org.lwjgl.opengl.GL40#GL_TESS_CONTROL_SHADER TESS_CONTROL_SHADER} {@link org.lwjgl.opengl.GL40#GL_TESS_EVALUATION_SHADER TESS_EVALUATION_SHADER}
     * @param source GLSL source code
     * @return Newly constructed {@link Shader}
     * @throws Exception if the shader fails to compile
     */
    public static Shader create(int type, String source) throws Exception {
        final int id = glCreateShader(type);

        // Compile the shader source code
        glShaderSource(id, source);
        glCompileShader(id);

        // Handle compilation error
        int[] params = new int[] { 1 };
        glGetShaderiv(id, GL_COMPILE_STATUS, params);

        if (params[0] == 0) {
            String log = glGetShaderInfoLog(id);
            System.err.println(log);

            throw new Exception("Failed to compile the shader!");
        }

        return new Shader(id);
    }

    private static final Pattern pattern = Pattern.compile("^\\s*#include \"([\\w\\.]+)\"$", Pattern.MULTILINE);

    private static String loadResource(String path) throws IOException {
        return new String(ShaderProgram.class.getResourceAsStream("/shaders/" + path + ".glsl").readAllBytes());
    }

    public static String load(String path) throws IOException {
        String source = Shader.loadResource(path);

        return pattern
            .matcher(source)
            .replaceAll(mr -> {
                try {
                    return Shader.loadResource("lib/" + mr.group(1));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }
}
