package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;

/**
 * Representation of an Entity as a Model.
 */
public class ModelEntity extends Model {
    private float scale = 1;
    private boolean transparent = false;

    /**
     * Construct a new ModelEntity given its properties.
     * @param textureName Texture used for rendering this Entity
     * @param modelName Name of the Model, used to look up the obj file
     * @param scale Scale used for rendering this Model
     * @param transparent Whether this Model has transparency and needs to be done in a separate render pass
     * @throws Exception if we fail to initialise one or more resources
     */
    public ModelEntity(String textureName, String modelName, float scale, boolean transparent) throws Exception {
        super(Mesh.loadObjFromResource(modelName).build(),
            new Material(Resources.getShader("entity"), Resources.getTexture(textureName)));
        
        this.scale = scale;
        this.transparent = transparent;
    }

    /**
     * Get the scale of this model.
     * @return Scale
     */
    public float getScale() {
        return this.scale;
    }

    /**
     * Get whether this model is transparent.
     * @return Whether this model is transparent
     */
    public boolean isTransparent() {
        return this.transparent;
    }
}
