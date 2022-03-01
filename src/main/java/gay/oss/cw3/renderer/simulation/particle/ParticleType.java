package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.MeshUtil;
import gay.oss.cw3.renderer.objects.Model;

import java.util.function.Supplier;

public enum ParticleType {
    HEART(ParticleType::createHeartModel, 10);

    public final Supplier<Model> modelSup;
    public final int lifetime;

    ParticleType(Supplier<Model> modelSup, int lifetime) {
        this.modelSup = modelSup;
        this.lifetime = lifetime;
    }

    private static Model createHeartModel() {
        try {
            return new Model(
                    MeshUtil.makeIndexedPlane(0.4f, 0.4f, true, 1),
                    new Material(Resources.getShader("entity"), Resources.getTexture("ui/daycycle/afternoon.png"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
