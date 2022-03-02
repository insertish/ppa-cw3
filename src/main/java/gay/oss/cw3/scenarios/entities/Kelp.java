package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * Kelp is essentially the aquatic equivalent of {@link Grass}.
 */
public class Kelp extends Entity {
    /**
     * Creates a new Kelp entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public Kelp(World world, Coordinate location) {
        super(world, EntityLayer.FOLIAGE, location);
        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
        this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 0.6);
        this.setFullness(0.5);
    }

    @Override
    protected boolean canGoInWater() {
        return true;
    }

    @Override
    protected boolean canGoOnLand() {
        return false;
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
                var locations = this.getWorld().findMatchingLocations(this.getLocation(), 1, coord ->
                        this.getWorld().isInBounds(coord)
                                && this.getWorld().getEntity(this.getLayer(), coord.x, coord.z) == null
                                && !this.getWorld().isAboveWater(coord)
                );

                if (!locations.isEmpty()) {
                    var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                    this.getWorld().spawn(new Kelp(this.getWorld(), coord));
                    this.removeFullness(0.25);
                }
            }
        }
    }

    @Override
    public float yOffset() {
        var height = this.getWorld().getMap().getHeight(this.getLocation().x, this.getLocation().z);
        var offset = this.getWorld().getMap().getWaterLevel() - height - 10f;
        return height > height+offset ? 0f : offset;
    }

    @Override
    public ParticleType deathParticleType() {
        return ParticleType.GRASS;
    }
}
