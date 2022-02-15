package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;

public class ModelEntity extends Model {
    public ModelEntity(Texture texture) throws Exception {
        super(MeshUtil.makeCube(1, 1, 1, true),
            new Material(Resources.getShader("texturedObject"), texture));
    }
}