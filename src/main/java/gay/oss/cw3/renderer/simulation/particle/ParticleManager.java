package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.objects.Model;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for managing particles in the World
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class ParticleManager {
    private final Object lock = new Object();
    private final Map<ParticleType, Model> models = new EnumMap<>(ParticleType.class);
    private List<Particle> particles = new ArrayList<>();

    /**
     * Tick all particles forwards
     */
    public void tick() {
        synchronized (lock) {
            this.particles = this.particles.stream().filter(Particle::tick).collect(Collectors.toList());
        }
    }

    /**
     * Add a new Particle to the world
     * @param p Particle
     */
    public void addParticle(Particle p) {
        synchronized (lock) {
            this.particles.add(p);
        }
    }

    /**
     * Get a list of all particles in the World
     * @return List of Particles
     */
    public List<Particle> getParticles() {
        synchronized (lock) {
            return new ArrayList<>(particles);
        }
    }

    /**
     * Generate Models for all Particle types
     */
    public void computeModels() {
        if (this.models.isEmpty()) {
            for (ParticleType type : ParticleType.values()) {
                this.models.put(type, type.modelSup.get());
            }
        }
    }

    /**
     * Get Model for a specified Particle
     * @param particle Particle
     * @return Model for rendering
     */
    public Model getModelForParticle(Particle particle) {
        return this.models.get(particle.getType());
    }
}
