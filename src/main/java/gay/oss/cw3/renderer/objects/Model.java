package gay.oss.cw3.renderer.objects;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import gay.oss.cw3.renderer.shaders.ShaderProgram;

public class Model {
    private final Mesh mesh;
    private final @Nullable Material material;

    private Matrix4f transformation;

    public Model(Mesh mesh, @Nullable Material material) {
        this.mesh = mesh;
        this.material = material;
        this.setTransformation(new Matrix4f().identity());
    }

    public Model(Mesh mesh) {
        this(mesh, null);
    }

    public Matrix4f getTransformation() {
        return transformation;
    }

    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }

    public void draw(Matrix4f viewProjection) {
        this.material.use();

        var program = ShaderProgram.getCurrent();
        program.setUniform("model", this.transformation);
        program.setUniform("modelViewProjection", new Matrix4f(viewProjection).mul(this.transformation));

        this.mesh.draw();
    }

    public void destroyMesh() {
        this.mesh.destroy();
    }
}
