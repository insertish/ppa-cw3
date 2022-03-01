package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;

import java.util.function.Supplier;

public enum ParticleType {
    HEART(ParticleType::createHeartModel, 50),
    SKULL(ParticleType::createSkullModel, 50),
    GRASS(ParticleType::createGrassModel, 2),
    ;

    public final Supplier<Model> modelSup;
    public final int lifetime;

    ParticleType(Supplier<Model> modelSup, int lifetime) {
        this.modelSup = modelSup;
        this.lifetime = lifetime;
    }

    private static Model createHeartModel() {
        try {
            return new Model(
                    Mesh.loadObjFromResource("particles/particleX").build(),
                    new Material(Resources.getShader("particle"), Resources.getTexture("particles/heart.png"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Model createSkullModel() {
        try {
            return new Model(
                    Mesh.loadObjFromResource("particles/particleX").build(),
                    new Material(Resources.getShader("particle"), Resources.getTexture("particles/skull.png"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Model createGrassModel() {
        try {
            return new Model(
                    Mesh.loadObjFromResource("particles/particleX").build(),
                    new Material(Resources.getShader("particle"), Resources.getTexture("particles/grass.png"))
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
