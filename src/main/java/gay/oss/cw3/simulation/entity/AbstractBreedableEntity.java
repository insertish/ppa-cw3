package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.renderer.simulation.particle.Particle;
import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import org.jetbrains.annotations.Nullable;

/**
 * A partial implementation of {@link Breedable} that handles much of the code that applies to all implementations.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public abstract class AbstractBreedableEntity extends Entity implements Breedable {
    private int lastBreedAttempt;

    protected Sex sex;

    /**
     * Create a new AbstractBreedableEntity.
     *
     * @param world             the world the entity will reside in
     * @param layer             the entity's layer
     * @param location          the entity's initial location
     * @param sex               the sex of this entity
     */
    public AbstractBreedableEntity(World world, Coordinate location, EntityLayer layer, Sex sex) {
        super(world, layer, location);
        this.sex = sex;
    }

    /**
     * Create a new AbstractBreedableEntity.
     *
     * @param world             the world the entity will reside in
     * @param layer             the entity's layer
     * @param sex               the sex of this entity
     */
    public AbstractBreedableEntity(World world, EntityLayer layer, Sex sex) {
        super(world, layer);
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
        this.getWorld().getParticleManager().addParticle(
                new Particle(
                        ParticleType.HEART,
                        this.getLocation(),
                        this.yOffset() + this.getWorld().getMap().getHeight(this.getLocation().x, this.getLocation().z),
                        (float) this.getWorld().getRandom().nextGaussian()*0.2f,
                        this.getWorld().getRandom().nextFloat(),
                        (float) this.getWorld().getRandom().nextGaussian()*0.2f
                )
        );
    }
}
