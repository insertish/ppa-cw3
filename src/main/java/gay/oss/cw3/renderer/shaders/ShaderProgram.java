package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

/**
 * Wrapper class around OpenGL shader program
 */
public class ShaderProgram {
    private static ShaderProgram CURRENT_SHADER;

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
        glUseProgram(this.id);
        CURRENT_SHADER = this;
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

    public static ShaderProgram getCurrent() {
        return ShaderProgram.CURRENT_SHADER;
    }
}
