package gay.oss.cw3.renderer;

import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

public class ShaderProgram {
    private final int id;
    private final Map<String, Integer> uniformLocations;

    private ShaderProgram(int id) {
        this.id = id;
        this.uniformLocations = new HashMap<>();
    }

    public void use() {
        glUseProgram(this.id);
    }

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

            for (Shader shader : shaders) {
                log = glGetShaderInfoLog(shader.getID());
                System.err.println(log);
            }

            throw new Exception();
        }

        // After compilation, you can detach shaders
        for (Shader shader : shaders) {
            glDetachShader(id, shader.getID());
        }

        return new ShaderProgram(id);
    }

    private int getUniformLocation(String key) {
        Integer location;
        if ((location = this.uniformLocations.get(key)) != null) {
            return location;
        }

        location = glGetUniformLocation(this.id, key);
        this.uniformLocations.put(key, location);
        return location;
    }

    public void setUniform(String key, float value) {
        glUniform1f(this.getUniformLocation(key), value);
    }

    public void setUniform(String key, int value) {
        glUniform1i(this.getUniformLocation(key), value);
    }

    public void setUniformVec3(String key, float[] value) {
        glUniform3fv(this.getUniformLocation(key), value);
    }

    public void setUniformVec4(String key, float[] value) {
        glUniform4fv(this.getUniformLocation(key), value);
    }

    public void setUniformMat4(String key, float[] value) {
        glUniformMatrix4fv(this.getUniformLocation(key), false, value);
    }
}
