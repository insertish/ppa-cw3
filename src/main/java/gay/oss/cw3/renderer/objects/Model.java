package gay.oss.cw3.renderer.objects;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import gay.oss.cw3.renderer.shaders.Camera;

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

    public Mesh getMesh() {
        return this.mesh;
    }

    public Matrix4f getTransformation() {
        return this.transformation;
    }

    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }

    public void use() {
        this.material.use();
    }

    public void draw(Matrix4f viewProjection) {
        this.material.use();
        Camera.upload(viewProjection, this.transformation);
        this.mesh.draw();
    }

    public void draw(Camera camera) {
        this.material.use();
        camera.upload(this.transformation);
        this.mesh.draw();
    }

    public void destroyMesh() {
        this.mesh.destroy();
    }
}
