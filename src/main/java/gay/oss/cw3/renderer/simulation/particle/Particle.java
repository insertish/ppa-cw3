package gay.oss.cw3.renderer.simulation.particle;

import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.simulation.Coordinate;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Particle {
    private final ParticleType type;

    private int age = 0;
    private float x;
    private float y;
    private float z;

    private final Vector3f velocity;

    public Particle(ParticleType type, float x, float y, float z, float velX, float velY, float velZ) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.velocity = new Vector3f(velX, velY, velZ);
    }

    public Particle(ParticleType type, Coordinate location, float y, float velX, float velY, float velZ) {
        this.type = type;
        this.x = location.x;
        this.y = y;
        this.z = location.z;
        this.velocity = new Vector3f(velX, velY, velZ);
    }

    public boolean tick() {
        this.age++;
        this.x += this.velocity.x;
        this.y += this.velocity.y;
        this.z += this.velocity.z;

        this.velocity.mul(0.8f);

        return this.age <= type.lifetime;
    }

    public Matrix4f getMatrix() {
        return new Matrix4f().translation(x, y, z);
    }

    public ParticleType getType() {
        return this.type;
    }
}
