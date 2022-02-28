package gay.oss.cw3.renderer.ui;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.joml.Matrix4f;
import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.shaders.ShaderProgram;

/**
 * UI helper class
 */
public abstract class UI {
    private Matrix4f viewProjection;

    private ShaderProgram colourShader;
    private ShaderProgram textureShader;

    private Mesh squareMesh;

    protected int width;
    protected int height;

    /**
     * Create a new UI render helper
     * @throws Exception if we fail to load one or more resources
     */
    public UI() throws Exception {
        this.colourShader = ShaderProgram.fromName("ui/colour");
        this.textureShader = ShaderProgram.fromName("ui/textured");

        this.squareMesh = Mesh.builder()
            .vertex(new float[] {
                // BR
                0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                // TL
                0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
            })
            .render(new float[] {
                // BR
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                // TL
                0.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
            }, 2)
            .build();
    }

    /**
     * Upload a given transformation applied to the UI's view projection
     * @param transformation Tranformation Matrix
     */
    public void upload(Matrix4f transformation) {
        Camera.upload(viewProjection, transformation);
    }

    /**
     * Calculate a transformation matrix for the given coordinates
     * @param x X position
     * @param y Y position
     * @param w Width
     * @param h Height
     */
    public void upload(int x, int y, int w, int h) {
        this.upload(
            new Matrix4f()
                .translation(x, this.height - y - h, 0)
                .scale(w, h, 0)
        );
    }

    /**
     * Get the square mesh used for rendering
     * @return Square Mesh
     */
    public Mesh getSquareMesh() {
        return this.squareMesh;
    }

    /**
     * Calculate the view projection matrix based on the width and height provided
     * @param width Width
     * @param height Height
     */
    private void calculate(int width, int height) {
        this.width = width;
        this.height = height;
        this.viewProjection = new Matrix4f()
            .ortho2D(0, width, 0, height);
    }

    /**
     * Draw a textured rectangle
     * @param x X position
     * @param y Y position
     * @param w Width
     * @param h Height
     * @param texture Texture to draw
     */
    public void drawRect(int x, int y, int w, int h, Texture texture) {
        texture.bind();
        this.textureShader.use();
        this.upload(x, y, w, h);
        this.squareMesh.draw();
    }

    /**
     * Draw a coloured rectangle
     * @param x X position
     * @param y Y position
     * @param w Width
     * @param h Height
     * @param colour RGBA colour space vector
     */
    public void drawRect(int x, int y, int w, int h, Vector4f colour) {
        this.colourShader.use();
        this.colourShader.setUniform("colour", colour);
        this.upload(x, y, w, h);
        this.squareMesh.draw();
    }

    /**
     * Abstract method to draw the UI given accessible width and height
     * @param width Width
     * @param height Height
     */
    protected abstract void drawUI(int width, int height);

    /**
     * Draw this UI root node to the screen
     * @param width Width
     * @param height Height
     */
    public void draw(int width, int height) {
        this.calculate(width, height);
        glDisable(GL_DEPTH_TEST);
        this.drawUI(width, height);
        glEnable(GL_DEPTH_TEST);
    }
}
