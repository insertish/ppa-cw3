package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;

public abstract class AbstractBreedableEntity extends Entity implements Breedable {
    private int lastBreedAttempt;

    public AbstractBreedableEntity(World world, Coordinate location, int initialAgeTicks, boolean alive) {
        super(world, location, initialAgeTicks, alive);
    }

    public AbstractBreedableEntity(World world, int initialAgeTicks, boolean alive) {
        super(world, initialAgeTicks, alive);
    }

    @Override
    public boolean canBreed() {
        return this.getAgeTicks() >= this.getAttributes().get(EntityAttribute.MINIMUM_BREEDING_AGE)
                && this.getAgeTicks() >= this.lastBreedAttempt + this.getAttributes().get(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS);
    }

    @Override
    public void startBreedingAttempt() {
        this.lastBreedAttempt = this.getAgeTicks();
    }
}