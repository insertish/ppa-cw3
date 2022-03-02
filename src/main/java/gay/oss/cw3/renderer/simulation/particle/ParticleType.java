package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;

import java.util.function.Supplier;

/**
 * Enumeration of Particle Types
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public enum ParticleType {
    HEART(ParticleType::createHeartModel, 50),
    SKULL(ParticleType::createSkullModel, 50),
    GRASS(ParticleType::createGrassModel, 2);

    public final Supplier<Model> modelSup;
    public final int lifetime;

    /**
     * Construct a new ParticleType
     * @param modelSup Supplier of Models
     * @param lifetime Maximum lifetime of particle type
     */
    ParticleType(Supplier<Model> modelSup, int lifetime) {
        this.modelSup = modelSup;
        this.lifetime = lifetime;
    }

    /**
     * Create a new Heart model
     * @return Model
     */
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

    /**
     * Create a new Skull model
     * @return Model
     */
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

    /**
     * Create a new Grass model
     * @return Model
     */
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
