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

    public static ShaderProgram create(Shader[] shaders) {
        final int id = glCreateProgram();

        for (Shader shader : shaders) {
            glAttachShader(id, shader.getID());
        }

        glLinkProgram(id);

        // FIXME: handle failure

        for (Shader shader : shaders) {
            glDetachShader(id, shader.getID());
        }

        return new ShaderProgram(id);
    }
}
