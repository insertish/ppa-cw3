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
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
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

    /**
     * Final Pattern for matching dynamic files includes in shaders.
     */
    private static final Pattern patternInclude = Pattern.compile("^\\s*#include \"([\\w\\.]+)\"$", Pattern.MULTILINE);

    /**
     * Final Pattern for matching variables in shaders.
     */
    private static final Pattern patternVariable = Pattern.compile("#\\[([\\w\\._]+)\\]", Pattern.MULTILINE);

    /**
     * Load a specific shader resource as a String.
     * @return String value
     * @throws IOException if the shader is not found.
     */
    private static String loadResource(String path) throws IOException {
        return new String(ShaderProgram.class.getResourceAsStream("/shaders/" + path + ".glsl").readAllBytes());
    }

    /**
     * Load a shader source by its path.
     * This will dynamically load any specificed imports.
     * @param path Path within shader resource bounds
     * @return Shader source
     * @throws IOException if the shader is not found.
     */
    public static String load(String path) throws IOException {
        String source = Shader.loadResource(path);

        // Scan the shader source for "imports" and
        // load them from the shaders/lib folder.
        return patternInclude
            .matcher(
                patternVariable.matcher(source)
                    .replaceAll(mr -> {
                        String variable = mr.group(1);
            
                        return ShaderVariables.get(variable);
                    })
            )
            .replaceAll(mr -> {
                String localPath = mr.group(1);

                try {
                    return Shader.loadResource("lib/" + localPath);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
    }
}
