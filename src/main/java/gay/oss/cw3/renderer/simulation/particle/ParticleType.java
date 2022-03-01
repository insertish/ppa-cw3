package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.MeshUtil;
import gay.oss.cw3.renderer.objects.Model;

import java.util.function.Supplier;

public enum ParticleType {
    HEART(ParticleType::createHeartModel, 50);

    public final Supplier<Model> modelSup;
    public final int lifetime;

    ParticleType(Supplier<Model> modelSup, int lifetime) {
        this.modelSup = modelSup;
        this.lifetime = lifetime;
    }

    private static Model createHeartModel() {
        try {
            return new Model(
                    Mesh.loadObjFromResource("particles/particle").build(),
                    new Material(Resources.getShader("entity"), Resources.getTexture("particles/heart.png"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
