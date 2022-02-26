package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;

public class ModelEntity extends Model {
    private float scale = 1;

    public ModelEntity(String textureName) throws Exception {
        super(MeshUtil.makeCube(0.5f, 1f, 0.5f, false),
            new Material(Resources.getShader("entity"), Texture.fromResource(textureName)));
    }

    public ModelEntity(String textureName, String modelName, float scale) throws Exception {
        super(Mesh.loadObjFromResource(modelName).build(),
            new Material(Resources.getShader("entity"), Texture.fromResource(textureName)));
        
        this.scale = scale;
    }

    public float getScale() {
        return this.scale;
    }
}
