package gay.oss.cw3.renderer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private final int id;

    private Shader(int id) {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public static Shader create(int type, String source) {
        final int id = glCreateShader(type);

        glShaderSource(id, source);
        glCompileShader(id);
        
        // FIXME: handle failure

        return new Shader(id);
    }
}
