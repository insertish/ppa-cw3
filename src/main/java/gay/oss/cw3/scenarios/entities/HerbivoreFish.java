package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.EatFoliageBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.SleepBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * The herbivore fish entity eats kelp to stay alive.
 */
public class HerbivoreFish extends AbstractBreedableEntity {
    /**
     * Creates a new herbivore fish entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     */
    public HerbivoreFish(World world, Coordinate location) {
        super(world, location, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
        this.getBrain().addBehaviour(new SleepBehaviour(this, false));
        this.getBrain().addBehaviour(new EatFoliageBehaviour(this, 1.0, 0.7, Kelp.class));
        this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0, 5));
        this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
        this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 30);
        this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
        this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness()*0.6);
        this.setFullness(this.getMaxFullness());
        this.setHealth(this.getMaxHealth());
    }

    @Override
    protected boolean canGoOnLand() {
        return false;
    }

    @Override
    protected boolean canGoInWater() {
        return true;
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
        var result = new HerbivoreFish(this.getWorld(), location);
        result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
        return result;
    }

    @Override
    public float yOffset() {
        var height = this.getWorld().getMap().getHeight(this.getLocation().x, this.getLocation().z);
        var offset = this.getWorld().getMap().getWaterLevel() - height - 10f;
        return height > height+offset ? 0f : offset;
    }

    @Override
    public ParticleType deathParticleType() {
        return ParticleType.SKULL;
    }
}
