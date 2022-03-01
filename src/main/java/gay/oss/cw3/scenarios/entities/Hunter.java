package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.SleepBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import org.jetbrains.annotations.Nullable;

/**
 * An animal entity representing a fox.
 *
 * <p>Foxes are carnivores who hunt {@link Rabbit rabbits}.</p>
 */
public class Hunter extends AbstractBreedableEntity {
    /**
     * Creates a new fox entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public Hunter(World world, Coordinate location) {
        super(world, location, 0, true, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
        this.getBrain().addBehaviour(new SleepBehaviour(this, true));
        this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, Rabbit.class));
        this.getBrain().addBehaviour(new BreedBehaviour<>(this, 0.6));
        this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
        this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
        this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
        this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness() / 2.0);
        this.setFullness(this.getMaxFullness());
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.getBrain().tick();
            this.removeFullness(0.01);
            if (this.getFullness() <= 0) {
                this.addHealth(-1);
            }
        }
    }

    @Override
    public @Nullable Entity createChild(Entity otherParent, Coordinate coordinate) {
        var result = new Hunter(this.getWorld(), coordinate);
        result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
        return result;
    }

    @Override
    public ParticleType deathParticleType() {
        return ParticleType.SKULL;
    }
}
