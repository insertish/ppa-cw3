package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.shaders.Instanced;
import gay.oss.cw3.renderer.simulation.WorldRenderer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParticleManager {
    private final Object lock = new Object();
    private final Map<ParticleType, Model> models = new EnumMap<>(ParticleType.class);
    private List<Particle> particles = new ArrayList<>();

    public void tick() {
        synchronized (lock) {
            this.particles = this.particles.stream().filter(Particle::tick).collect(Collectors.toList());
        }
    }

    public void addParticle(Particle p) {
        synchronized (lock) {
            this.particles.add(p);
        }
    }

    public List<Particle> getParticles() {
        synchronized (lock) {
            return new ArrayList<>(particles);
        }
    }

    public void computeModels() {
        if (this.models.isEmpty()) {
            for (ParticleType type : ParticleType.values()) {
                this.models.put(type, type.modelSup.get());
            }
        }
    }

    public Model getModelForParticle(Particle particle) {
        return this.models.get(particle.getType());
    }
}
