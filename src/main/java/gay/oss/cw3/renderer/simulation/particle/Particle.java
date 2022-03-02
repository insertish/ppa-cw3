package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.simulation.Coordinate;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Represents a particle in the World
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Particle {
    private final ParticleType type;

    private int age = 0;
    private float x;
    private float y;
    private float z;

    private final Vector3f velocity;

    /**
     * Construct a new Particle
     * @param type Particle Type
     * @param x World X position
     * @param y World Y position
     * @param z World Z position
     * @param velX Velocity in X
     * @param velY Velocity in Y
     * @param velZ Velocity in Z
     */
    public Particle(ParticleType type, float x, float y, float z, float velX, float velY, float velZ) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocity = new Vector3f(velX, velY, velZ);
    }

    /**
     * Construct a new Particle
     * @param type Particle Type
     * @param location Coordinate position
     * @param y World Y position
     * @param velX Velocity in X
     * @param velY Velocity in Y
     * @param velZ Velocity in Z
     */
    public Particle(ParticleType type, Coordinate location, float y, float velX, float velY, float velZ) {
        this.type = type;
        this.x = location.x;
        this.y = y;
        this.z = location.z;
        this.velocity = new Vector3f(velX, velY, velZ);
    }

    /**
     * Tick this Particle forwards
     * @return Whether this particle is still alive
     */
    public boolean tick() {
        this.age++;
        this.x += this.velocity.x;
        this.y += this.velocity.y;
        this.z += this.velocity.z;

        this.velocity.mul(0.8f);

        return this.age <= type.lifetime;
    }

    /**
     * Get the matrix required to transform this Particle
     * @param Camera
     * @return Transformation Matrix
     */
    public Matrix4f getMatrix(Camera camera) {
        return new Matrix4f()
            .translation(x, y, z)
            .rotateTowards(camera.getEyeDirectionVector(), new Vector3f(0, 1, 0));
    }

    /**
     * Get this particle's type
     * @return Particle Type
     */
    public ParticleType getType() {
        return this.type;
    }
}
