package gay.oss.cw3.renderer.objects;

import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import gay.oss.cw3.renderer.shaders.Camera;

/**
 * A model represents a single mesh with a material and transformation.
 * This is a helper class for drawing objects in their simplest manner.
 */
public class Model {
    private final Mesh mesh;
    private final @Nullable Material material;

    private Matrix4f transformation;

    /**
     * Construct a new Model with a given mesh and material.
     * It will have no effective transformation.
     * @param mesh Mesh
     * @param material Material
     */
    public Model(Mesh mesh, @Nullable Material material) {
        this.mesh = mesh;
        this.material = material;

        // Creating a new Matrix4f intialises it to an identity matrix.
        this.setTransformation(new Matrix4f());
    }

    /**
     * Construct a new Model with a given mesh.
     * It will have no effective transformation or material.
     * @param mesh Mesh
     */
    public Model(Mesh mesh) {
        this(mesh, null);
    }

    /**
     * Get the Mesh used for this model.
     * @return Mesh
     */
    public Mesh getMesh() {
        return this.mesh;
    }

    /**
     * Get the Transformation of this model.
     * @return Transformation Matrix
     */
    public Matrix4f getTransformation() {
        return this.transformation;
    }

    /**
     * Set the Transformation of this model.
     * @param transformation Transformation Matrix
     */
    public void setTransformation(Matrix4f transformation) {
        this.transformation = transformation;
    }

    /**
     * Use this model's current Material.
     */
    public void use() {
        this.material.use();
    }

    /**
     * Draw this model given a specific view projection.
     * @param viewProjection View Projection Matrix
     */
    public void draw(Matrix4f viewProjection) {
        this.use();
        Camera.upload(viewProjection, this.transformation);
        this.mesh.draw();
    }

    /**
     * Draw this model using the provided Camera's view projection.
     * @param camera Camera
     */
    public void draw(Camera camera) {
        this.use();
        camera.upload(this.transformation);
        this.mesh.draw();
    }

    /**
     * Destroy this model's mesh.
     */
    public void destroyMesh() {
        this.mesh.destroy();
    }
}
