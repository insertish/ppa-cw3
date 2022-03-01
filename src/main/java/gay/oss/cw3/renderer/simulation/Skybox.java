package gay.oss.cw3.renderer.simulation;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthMask;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Cubemap;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.MeshUtil;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.shaders.ShaderProgram;

public class Skybox {
    private final ShaderProgram shader;
    private final Cubemap cubemap;
    private final Mesh mesh;

    public Skybox() throws Exception {
        this.shader = Resources.getShader("skybox");
        this.cubemap = new Cubemap("skybox", "png");
        this.mesh = MeshUtil.makeCube(1, 1, 1, true);
    }

    public void draw(Camera camera, Vector3f skyColour) {
        this.cubemap.bind();
        
        this.shader.use();
        this.shader.setUniform("skyColour", skyColour);

        camera.upload(
            new Matrix4f()
                .translation(camera.getEyePositionVector())
        );

        glDepthMask(false);
        glCullFace(GL_FRONT);
        this.mesh.draw();
        glCullFace(GL_BACK);
        glDepthMask(true);
    }
}
