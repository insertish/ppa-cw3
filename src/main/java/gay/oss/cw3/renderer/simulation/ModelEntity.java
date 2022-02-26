package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;

public class ModelEntity extends Model {
    private float scale = 1;
    private boolean transparent = false;

    public ModelEntity(String textureName, String modelName, float scale, boolean transparent) throws Exception {
        super(Mesh.loadObjFromResource(modelName).build(),
            new Material(Resources.getShader("entity"), Texture.fromResource(textureName)));
        
        this.scale = scale;
        this.transparent = transparent;
    }

    public float getScale() {
        return this.scale;
    }

    public boolean isTransparent() {
        return this.transparent;
    }
}
