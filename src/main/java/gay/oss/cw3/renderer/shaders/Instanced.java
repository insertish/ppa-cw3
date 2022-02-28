package gay.oss.cw3.renderer.shaders;

import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL30.glBindBufferBase;
import static org.lwjgl.opengl.GL43.GL_SHADER_STORAGE_BUFFER;

import java.util.List;

import org.joml.Matrix4f;

import gay.oss.cw3.renderer.objects.Mesh;

/**
 * Instanced mesh renderer, this currently represents an OpenGL SSBO.
 */
public class Instanced {
    private int ssbo;

    /**
     * Construct a new instanced mesh renderer.
     */
    public Instanced() {
        this.ssbo = glGenBuffers();
    }

    /**
     * Bind the instanced mesh renderer.
     */
    public void bind() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, this.ssbo);
        glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, ssbo);
    }

    /**
     * Unbind the instanced mesh renderer.
     */
    public static void unbind() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, 0);
    }

    /**
     * Upload bulk transformation data for the meshes.
     * @param data List of 4x4 Matrices
     */
    private void upload(List<Matrix4f> data) {
        float buffer[] = new float[data.size() * 16];
        for (int i=0;i<data.size();i++) {
            data.get(i).get(buffer, i * 16);
        }

        glBufferData(GL_SHADER_STORAGE_BUFFER, buffer, GL_STREAM_DRAW);
    }

    /**
     * Instance draw an indexed mesh with the given transformation.
     * @param mesh Provided Mesh
     * @param transformations List of 4x4 Transformation Matrices
     */
    public void draw(Mesh mesh, List<Matrix4f> transformations) {
        this.bind();
        this.upload(transformations);
        mesh.drawInstanced(transformations.size());
        Instanced.unbind();
    }
}
