package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * Tree entities are quite unique - they spread very slowly, and have no predators. They produce fruit, which other
 * entities may eat. This prevents the trees from spreading but does not harm them.
 */
public class Tree extends Entity {
    /**
     * The amount of hunger stored in a fruit.
     */
    public static final double FRUIT_FULLNESS = 2.5;
    private boolean hasFruit = false;

    /**
     * Creates a new Tree entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public Tree(World world, Coordinate location) {
        super(world, EntityLayer.FOLIAGE, location);
        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 10);
        this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 10.0);
        this.setFullness(0.5);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            // photosynthesis
            if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                this.addFullness(0.01);
            }

            // fruiting
            if (this.getFullness() >= 5.0 && !this.hasFruit()) {
                this.hasFruit = true;
                this.removeFullness(FRUIT_FULLNESS);
            }

            if (this.hasFruit() && this.getWorld().getRandom().nextFloat() < 0.001) {
                var locations = this.getWorld().findFreeLocationsAboveWater(this.getLayer(), this.getLocation(), 1);

                if (!locations.isEmpty()) {
                    var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                    new Tree(this.getWorld(), coord);
                    this.hasFruit = false;
                }
            }
        }
    }

    /**
     * @return whether this entity currently has fruit
     */
    public boolean hasFruit() {
        return this.hasFruit;
    }

    /**
     * Remove a fruit from this entity
     */
    public void removeFruit() {
        this.hasFruit = false;
    }
}
