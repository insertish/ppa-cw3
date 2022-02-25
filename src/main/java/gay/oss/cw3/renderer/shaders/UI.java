package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import org.joml.Matrix4f;

import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Texture;

public abstract class UI {
    private Matrix4f viewProjection;

    private ShaderProgram textureShader;
    private Mesh squareMesh;

    protected int width;
    protected int height;

    public UI() throws Exception {
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

    private void calculate(int width, int height) {
        this.width = width;
        this.height = height;
        this.viewProjection = new Matrix4f()
            .ortho2D(0, width, 0, height);
    }

    public void upload(Matrix4f transformation) {
        Camera.upload(viewProjection, transformation);
    }

    protected void drawImage(int x, int y, int w, int h, Texture texture) {
        texture.bind();
        this.textureShader.use();
        this.upload(new Matrix4f().identity().translate(x, this.height - y - h, 0).scale(w, h, 0));
        this.squareMesh.draw();
    }

    protected abstract void drawUI();

    public void draw(int width, int height) {
        this.calculate(width, height);
        glDisable(GL_DEPTH_TEST);
        this.drawUI();
        glEnable(GL_DEPTH_TEST);
    }
}
