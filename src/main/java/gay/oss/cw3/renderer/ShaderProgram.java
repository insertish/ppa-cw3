package gay.oss.cw3.renderer;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
    private final int id;

    private ShaderProgram(int id) {
        this.id = id;
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
            throw new Exception();
        }

        // After compilation, you can detach shaders
        for (Shader shader : shaders) {
            glDetachShader(id, shader.getID());
        }

        return new ShaderProgram(id);
    }
}
