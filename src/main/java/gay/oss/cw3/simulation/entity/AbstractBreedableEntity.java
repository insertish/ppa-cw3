package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractBreedableEntity extends Entity implements Breedable {
    private int lastBreedAttempt;

    protected Sex sex;

    public AbstractBreedableEntity(World world, Coordinate location, int initialAgeTicks, boolean alive, EntityLayer layer, Sex sex) {
        super(world, layer, location, initialAgeTicks, alive);
        this.sex = sex;
    }

    public AbstractBreedableEntity(World world, int initialAgeTicks, boolean alive, EntityLayer layer, Sex sex) {
        super(world, initialAgeTicks, alive, layer);
        this.sex = sex;
    }

    @Nullable
    @Override
    public Sex getSex() {
        return sex;
    }

    @Override
    public boolean canGiveBirth() {
        return this.getSex() == Sex.FEMALE;
    }

    @Override
    public boolean isCompatible(Entity entity) {
        return entity instanceof Breedable && Sex.isCompatible(this.sex, ((Breedable)entity).getSex());
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
