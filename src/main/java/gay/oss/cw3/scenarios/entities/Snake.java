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
 * An animal entity representing a snake.
 *
 * <p>Snakes are carnivores who hunt {@link Bunny bunnies}.</p>
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Snake extends AbstractBreedableEntity {
    /**
     * Creates a new fox entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public Snake(World world, Coordinate location) {
        super(world, location, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
        this.getBrain().addBehaviour(new SleepBehaviour(this, true));
        this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, 10, Bunny.class));
        this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.2, 30));
        this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.8));

        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
        this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 200);
        this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 400);
        this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness() / 2.0);
        this.getAttributes().set(EntityAttribute.LIFE_EXPECTANCY, 5000);
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
    public void setAlive(boolean alive) {
        super.setAlive(alive);
    }

    @Override
    public @Nullable Entity createChild(Entity otherParent, Coordinate coordinate) {
        var result = new Snake(this.getWorld(), coordinate);
        result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
        return result;
    }

    @Override
    public ParticleType deathParticleType() {
        return ParticleType.SKULL;
    }
}
