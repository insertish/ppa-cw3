package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.PerchInTreeBehaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import org.jetbrains.annotations.Nullable;

/**
 * A carnivore bird entity that hunts and eats rabbits.
 */
public class BirdOfPrey extends AbstractBird {

    /**
     * Creates a new bird entity
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public BirdOfPrey(World world, Coordinate location) {
        super(world, location, 0, true, EntityLayer.AERIAL_ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
        //this.getBrain().addBehaviour(new SleepBehaviour(this, true));
        this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, EntityLayer.ANIMALS, 0.7, 30, Bunny.class));
        this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.3, 5));
        this.getBrain().addBehaviour(new PerchInTreeBehaviour(1.0, this));
        this.getBrain().addBehaviour(new BoidBehaviour(30));

        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
        this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 200);
        this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 200);
        this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness() / 2.0);
        this.setFullness(this.getMaxFullness());
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.getBrain().tick();
            this.removeFullness(0.005);
            if (this.getFullness() <= 0) {
                this.addHealth(-1);
            }
        }
    }

    @Override
    public @Nullable Entity createChild(Entity otherParent, Coordinate coordinate) {
        var result = new BirdOfPrey(this.getWorld(), coordinate);
        result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
        return result;
    }
}
