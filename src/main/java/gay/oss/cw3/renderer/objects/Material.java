package gay.oss.cw3.renderer.objects;

import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.renderer.shaders.ShaderProgram;

/**
 * A packaging of a {@link ShaderProgram} and a {@link Texture} into one object for convenience.
 */
public class Material {
    private final ShaderProgram shaderProgram;
    private final @Nullable Texture texture;

    /**
     * Creates a new material.
     * @param shaderProgram the shader program to use
     * @param texture       the texture to use
     */
    public Material(ShaderProgram shaderProgram, @Nullable Texture texture) {
        this.shaderProgram = shaderProgram;
        this.texture = texture;
    }

    /**
     * Use this material for rendering. This binds the texture and uses the shader program.
     */
    public void use() {
        if (this.texture != null) {
            this.texture.bind();
        }
        
        this.shaderProgram.use();
    }

    public ShaderProgram getProgram() {
        return this.shaderProgram;
    }
}
