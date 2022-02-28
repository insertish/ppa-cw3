package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.shaders.LevelOfDetail;

/**
 * Representation of an Entity as a Model.
 */
public class ModelEntity {
    private float scale = 1;
    private RenderMode renderMode = RenderMode.Normal;

    private Material material;
    private Mesh mesh;
    private Mesh meshLOD2;
    private Mesh meshLOD3;

    /**
     * Construct a new ModelEntity given its properties.
     * @param textureName Texture used for rendering this Entity
     * @param modelName Name of the Model, used to look up the obj file
     * @param scale Scale used for rendering this Model
     * @param renderMode Whether this Model has transparency and needs to be done in a separate render pass (and if it needs to have culling disabled)
     * @throws Exception if we fail to initialise one or more resources
     */
    public ModelEntity(String textureName, String modelName, float scale, RenderMode renderMode) throws Exception {
        this.mesh = Mesh.loadObjFromResource("entities/" + modelName).build();        
        this.material = new Material(Resources.getShader("entity"), Resources.getTexture(textureName));

        if (renderMode == RenderMode.UseLOD) {
            this.meshLOD2 = Mesh.loadObjFromResource("entities/lod2/" + modelName).build();
            this.meshLOD3 = Mesh.loadObjFromResource("entities/lod3/" + modelName).build();
        }

        this.scale = scale;
        this.renderMode = renderMode;
    }

    /**
     * Get the scale of this model.
     * @return Scale
     */
    public float getScale() {
        return this.scale;
    }

    /**
     * Get this model's render mode.
     * @return RenderMode
     */
    public RenderMode getRenderMode() {
        return this.renderMode;
    }

    public void use() {
        this.material.use();
    }

    public Mesh getMesh(LevelOfDetail lod) {
        if (lod == LevelOfDetail.Low && meshLOD3 != null) {
            return meshLOD3;
        } else if (lod == LevelOfDetail.Low && meshLOD3 != null) {
            return meshLOD2;
        } else {
            return mesh;
        }
    }
}
