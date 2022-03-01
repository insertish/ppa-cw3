package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.behaviours.*;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * An animal entity representing a rabbit.
 *
 * <p>Rabbits are herbivores who eat grass and are hunted by {@link Hunter foxes}.</p>
 */
public class Rabbit extends AbstractBreedableEntity {
    /**
     * Create a new rabbit entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public Rabbit(World world, Coordinate location) {
        super(world, location, 0, true, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
        this.getBrain().addBehaviour(new FleeBehaviour(this, 1.0, 10, Hunter.class));
        this.getBrain().addBehaviour(new SleepBehaviour(this, false));
        this.getBrain().addBehaviour(new EatFoliageBehaviour(this, 1.0, 0.7, Grass.class));
        this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
        this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 1.0));

        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
        this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
        this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
        this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness() / 2.0);
        this.setFullness(this.getMaxFullness());
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.getBrain().tick();
            this.removeFullness(0.01);
            if (this.getFullness() <= 0) {
                this.addHealth(-1);
            }
        }
    }

    @Override
    public Entity createChild(Entity otherParent, Coordinate location) {
        var result = new Rabbit(this.getWorld(), location);
        result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
        return result;
    }

    @Override
    public ParticleType deathParticleType() {
        return ParticleType.SKULL;
    }
}
