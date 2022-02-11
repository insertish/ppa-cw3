package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;

public abstract class AbstractBreedableEntity extends Entity implements Breedable {
    private final int minBreedingAge;
    private final int ticksBetweenBreedAttempts;

    private int lastBreedAttempt;

    public AbstractBreedableEntity(World world, Coordinate location, int initialAgeTicks, boolean alive, int maxHealth, int minBreedingAge, int ticksBetweenBreedAttempts) {
        super(world, location, initialAgeTicks, alive, maxHealth);
        this.minBreedingAge = minBreedingAge;
        this.ticksBetweenBreedAttempts = ticksBetweenBreedAttempts;
    }

    public AbstractBreedableEntity(World world, int initialAgeTicks, boolean alive, int maxHealth, int minBreedingAge, int ticksBetweenBreedAttempts) {
        super(world, initialAgeTicks, alive, maxHealth);
        this.minBreedingAge = minBreedingAge;
        this.ticksBetweenBreedAttempts = ticksBetweenBreedAttempts;
    }

    @Override
    public boolean canBreed() {
        return this.getAgeTicks() >= this.minBreedingAge
                && this.getAgeTicks() >= this.lastBreedAttempt + this.ticksBetweenBreedAttempts;
    }

    @Override
    public void startBreedingAttempt() {
        this.lastBreedAttempt = this.getAgeTicks();
    }
}
