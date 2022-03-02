package gay.oss.cw3.renderer.simulation;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RED;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL20.glTexSubImage2D;

import java.util.Random;

import org.joml.Matrix4f;

import gay.oss.cw3.lib.FastNoiseLite;
import gay.oss.cw3.lib.FastNoiseLite.DomainWarpType;
import gay.oss.cw3.lib.FastNoiseLite.FractalType;
import gay.oss.cw3.lib.FastNoiseLite.NoiseType;
import gay.oss.cw3.lib.FastNoiseLite.RotationType3D;
import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.MeshUtil;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.shaders.ShaderProgram;

/**
 * Very basic cloud renderer
 */
public class CloudRenderer {
    private FastNoiseLite noise;
    private int textureID;
    private Mesh mesh;
    private ShaderProgram shader;

    private boolean written;

    /**
     * Construct a new CloudRenderer
     * @throws Exception if one or more resources failed to initialise
     */
    public CloudRenderer() throws Exception {
        this.noise = new FastNoiseLite(new Random().nextInt());
        this.noise.SetNoiseType(NoiseType.Perlin);
        //this.noise.SetFrequency(0.030f);
        this.noise.SetFrequency(0.090f);

        this.noise.SetFractalType(FractalType.FBm);
        this.noise.SetFractalOctaves(6);
        this.noise.SetFractalLacunarity(1.50f);
        this.noise.SetFractalGain(0.60f);
        this.noise.SetFractalWeightedStrength(0.20f);

        this.noise.SetDomainWarpType(DomainWarpType.OpenSimplex2);
        this.noise.SetRotationType3D(RotationType3D.ImproveXYPlanes);
        this.noise.SetDomainWarpAmp(15);

        this.textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

        this.mesh = MeshUtil.makeIndexedPlane(1, 1, false, 1);
        this.shader = Resources.getShader("cloud");
    }

    /**
     * Generate noise and write the texture to the GPU.
     * @param tick Current World tick
     */
    private void writeTexture(int tick) {
        glBindTexture(GL_TEXTURE_2D, this.textureID);

        float z = tick * 0.05f;

        float data[] = new float[64 * 64];
        for (int x=0;x<64;x++) {
            for (int y=0;y<64;y++) {
                data[y * 64 + x] = this.noise.GetNoise(x, y, z);
            }
        }

        if (written) {
            glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, 64, 64, GL_RED, GL_FLOAT, data);
        } else {
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, 64, 64, 0, GL_RED, GL_FLOAT, data);
            this.written = true;
        }
    }

    /**
     * Draw cloud layer.
     * @param camera Camera
     * @param tick Current World tick
     */
    public void draw(Camera camera, int tick) {
        this.shader.use();
        this.writeTexture(tick);

        camera.upload(
            new Matrix4f()
                .translation(0, 35, 0)
                .scale(256, 1, 256)
        );

        glDisable(GL_CULL_FACE);
        this.mesh.draw();
        glEnable(GL_CULL_FACE);
    }
}
