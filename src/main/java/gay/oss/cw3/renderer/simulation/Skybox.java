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

/**
 * Wrapper around Cubemap and Mesh providing a droppable skybox.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Skybox {
    private final ShaderProgram shader;
    private final Cubemap cubemap;
    private final Mesh mesh;

    /**
     * Construct a new Skybox
     * @throws Exception if one or more resources failed to load
     */
    public Skybox() throws Exception {
        this.shader = Resources.getShader("skybox");
        this.cubemap = new Cubemap("skybox", "png");
        this.mesh = MeshUtil.makeCube(1, 1, 1, true);
    }

    /**
     * Draw the skybox
     * @param camera Camera
     * @param skyColour Target sky colour
     */
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
