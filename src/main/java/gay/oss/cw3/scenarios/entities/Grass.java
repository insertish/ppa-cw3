package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * A foliage entity representing grass.
 */
public class Grass extends Entity {
    /**
     * Create a new Grass entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public Grass(World world, Coordinate location) {
        super(world, EntityLayer.FOLIAGE, location);
        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
        this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 0.6);
        this.setFullness(0.5);
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            // photosynthesis
            if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                this.addFullness(0.01);
            }

            // spreading
            if (this.getFullness() >= 0.5) {
                var locations = this.getWorld().findFreeLocationsAboveWater(this.getLayer(), this.getLocation(), 1);

                if (!locations.isEmpty()) {
                    var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                    this.getWorld().spawn(new Grass(this.getWorld(), coord));
                    this.removeFullness(0.25);
                }
            }

        }
    }

    @Override
    public ParticleType deathParticleType() {
        return ParticleType.GRASS;
    }
}
