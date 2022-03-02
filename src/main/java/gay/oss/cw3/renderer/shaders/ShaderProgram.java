package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform3fv;
import static org.lwjgl.opengl.GL20.glUniform4fv;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

/**
 * Wrapper class around OpenGL shader program
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class ShaderProgram {
    private static ShaderProgram CURRENT_SHADER;
    private static final Map<String, Object> uniformValues = new HashMap<>();

    private final int id;
    private final Map<String, Integer> uniformLocations;

    /**
     * Construct a new shader program with a valid program ID
     * @param id Program ID
     */
    private ShaderProgram(int id) {
        this.id = id;
        this.uniformLocations = new HashMap<>();
    }

    /**
     * Enable this shader program for use
     */
    public void use() {
        if (CURRENT_SHADER == this) return;
        glUseProgram(this.id);
        CURRENT_SHADER = this;

        try {
            this.applyStaticUniforms();
        } catch (Exception e) {
            System.err.println("Failed to apply static uniforms!");
            e.printStackTrace();
        }
    }

    /**
     * Get the memory location of a program uniform.
     * Value will be cached after first run.
     * @param key String key used to identify this uniform
     * @return Memory location of program uniform
     */
    private int getUniformLocation(String key) {
        Integer location;
        if ((location = this.uniformLocations.get(key)) != null) {
            return location;
        }

        location = glGetUniformLocation(this.id, key);
        this.uniformLocations.put(key, location);
        return location;
    }

    /**
     * Set float value at uniform key
     * @param key Key identifying uniform
     * @param value Float value to set
     */
    public void setUniform(String key, float value) {
        glUniform1f(this.getUniformLocation(key), value);
    }

    /**
     * Set integer value at uniform key
     * @param key Key identifying uniform
     * @param value Integer value to set
     */
    public void setUniform(String key, int value) {
        glUniform1i(this.getUniformLocation(key), value);
    }

    /**
     * Set {@link Vector3f} value at uniform key
     * @param key Key identifying uniform
     * @param value {@link Vector3f} value to set
     */
    public void setUniform(String key, Vector3f vec) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform3fv(this.getUniformLocation(key), vec.get(stack.mallocFloat(3)));
        }
    }

    /**
     * Set {@link Vector4f} value at uniform key
     * @param key Key identifying uniform
     * @param value {@link Vector4f} value to set
     */
    public void setUniform(String key, Vector4f vec) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniform4fv(this.getUniformLocation(key), vec.get(stack.mallocFloat(4)));
        }
    }

    /**
     * Set {@link Matrix4f} value at uniform key
     * @param key Key identifying uniform
     * @param value {@link Matrix4f} value to set
     */
    public void setUniform(String key, Matrix4f matrix) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            glUniformMatrix4fv(this.getUniformLocation(key), false, matrix.get(stack.mallocFloat(16)));
        }
    }

    /**
     * Set value at uniform key
     * @param key Key identifying uniform
     * @param value Arbitrary object to set
     */
    public static void setUniform(String key, Object value) {
        ShaderProgram.uniformValues.put(key, value);
    }

    /**
     * Apply static uniforms which were calculated earlier
     */
    public void applyStaticUniforms() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (String key : ShaderProgram.uniformValues.keySet()) {
            Object value = ShaderProgram.uniformValues.get(key);
            Method method = ShaderProgram.class.getMethod("setUniform", String.class, value.getClass());
            method.invoke(this, key, value);
        }
    }

    /**
     * Create a new shader program from the provided shaders
     * @param shaders Array of shaders
     * @return Newly constructed {@link ShaderProgram}
     * @throws Exception if the shader program fails to compile
     */
    public static ShaderProgram create(Shader[] shaders) throws Exception {
        final int id = glCreateProgram();

        // Attach shaders and compile
        for (Shader shader : shaders) {
            glAttachShader(id, shader.getID());
        }

        glLinkProgram(id);

        // Handle link error
        int[] params = new int[] { 1 };
        glGetProgramiv(id, GL_LINK_STATUS, params);

        if (params[0] == 0) {
            String log = glGetProgramInfoLog(id);
            System.err.println(log);

            throw new Exception("Failed to link shader program!");
        }

        // After compilation, you can detach shaders
        for (Shader shader : shaders) {
            glDetachShader(id, shader.getID());
            shader.delete();
        }

        return new ShaderProgram(id);
    }

    /**
     * Short-hand for creating a shader program from vertex and fragment shaders in resources.
     * Also prefixes /shaders/ and suffixes .glsl to the path given.
     * @param vertex Vertex shader path
     * @param fragment Fragment shader path
     * @param geometry Geometry shader path
     * @return Newly constructed {@link ShaderProgram}
     * @throws Exception if the shaders fail to compile or the shader program fails to link
     */
    public static ShaderProgram fromResources(String vertex, String fragment, @Nullable String geometry) throws Exception {
        Shader vertexShader = Shader.create(GL_VERTEX_SHADER, Shader.load(vertex));
        Shader fragShader   = Shader.create(GL_FRAGMENT_SHADER, Shader.load(fragment));

        if (geometry != null) {
            Shader geoShader = Shader.create(GL_GEOMETRY_SHADER, Shader.load(geometry));
            return ShaderProgram.create(new Shader[] { vertexShader, fragShader, geoShader });
        }

        return ShaderProgram.create(new Shader[] { vertexShader, fragShader });
    }

    /**
     * Short-hand for {@link fromResources} which automatically applies frag and vert suffixes.
     * @param name Name of the shader path
     * @return Newly constructed {@link ShaderProgram}
     * @throws Exception if the shaders fail to compile or the shader program fails to link
     */
    public static ShaderProgram fromName(String name) throws Exception {
        return ShaderProgram.fromResources(name + ".vert", name + ".frag", null);
    }

    /**
     * Get the currently active Shader Program.
     */
    public static ShaderProgram getCurrent() {
        return ShaderProgram.CURRENT_SHADER;
    }
}
