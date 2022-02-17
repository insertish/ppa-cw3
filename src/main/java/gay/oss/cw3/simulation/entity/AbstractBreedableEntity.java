package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;
import gay.oss.cw3.simulation.world.EntityLayer;

public abstract class AbstractBreedableEntity extends Entity implements Breedable {
    private int lastBreedAttempt;

    public AbstractBreedableEntity(World world, Coordinate location, int initialAgeTicks, boolean alive, EntityLayer layer) {
        super(world, layer, location, initialAgeTicks, alive);
    }

    public AbstractBreedableEntity(World world, int initialAgeTicks, boolean alive, EntityLayer layer) {
        super(world, initialAgeTicks, alive, layer);
    }

    @Override
    public boolean canBreed() {
        return this.getFullness() >= this.attributes.get(EntityAttribute.FULLNESS_TO_BREED)
                && this.getAgeTicks() >= this.getAttributes().get(EntityAttribute.MINIMUM_BREEDING_AGE)
                && this.getAgeTicks() >= this.lastBreedAttempt + this.getAttributes().get(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS);
    }

    @Override
    public void startBreedingAttempt() {
        this.lastBreedAttempt = this.getAgeTicks();
        this.removeFullness(this.attributes.get(EntityAttribute.FULLNESS_TO_BREED));
    }
}
