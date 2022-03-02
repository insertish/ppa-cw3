package gay.oss.cw3.simulation.entity.brain.behaviours;

import java.util.Random;

import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * A behaviour that makes entities eat foliage around them. Essentially a herbivore equivalent of {@link HuntBehaviour}.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class EatFoliageBehaviour extends MovementBehaviour {
    private final Random random = new Random();
    private final double targetFullnessFraction;
    private final Class<? extends Entity>[] targetClasses;

    private Entity target;
    private int ticksCouldntMove = 0;

    /**
     * Create a new EatFoliageBehaviour.
     *
     * @param entity                    the entity
     * @param speed                     the movement speed modifier
     * @param targetFullnessFraction    the fullness that this entity will try to reach by eating foliage
     * @param targetClasses             the classes that are valid food
     */
    @SafeVarargs
    public EatFoliageBehaviour(Entity entity, double speed, double targetFullnessFraction, Class<? extends Entity>... targetClasses) {
        super(speed, entity);
        this.targetFullnessFraction = targetFullnessFraction;
        this.targetClasses = targetClasses;
    }

    @Override
    public boolean canStart() {
        this.target = null;
        this.ticksCouldntMove = 0;

        if (this.entity.getFullness() >= this.targetFullnessFraction*this.entity.getMaxFullness()) {
            return false;
        }

        var potentialTargets = this.entity.getWorld().getEntitiesAround(EntityLayer.FOLIAGE, null, this.entity.getLocation(), 10);
        if (potentialTargets.isEmpty()) {
            return false;
        }

        var potentialTarget = potentialTargets.get(random.nextInt(potentialTargets.size()));
        if (this.isTarget(potentialTarget)) {
            this.target = potentialTarget;
        }

        return this.target != null;
    }

    @Override
    public boolean canContinue() {
        return this.target.isAlive() && this.ticksCouldntMove < 3;
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        var standingOn = this.entity.getWorld().getEntity(EntityLayer.FOLIAGE, this.entity.getLocation().x, this.entity.getLocation().z);
        if (standingOn != null && this.isTarget(standingOn)) {
            this.target = standingOn;
        }

        if (this.entity.getLocation().equals(this.target.getLocation())) {
            this.ticksCouldntMove = 0;
            this.target.setAlive(false);
            this.entity.addFullness(this.target.getFullness()*0.7);
            return;
        }

        var dir = this.target.getLocation().subtract(this.entity.getLocation());
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (this.entity.canMoveTo(newLoc)) {
            this.ticksCouldntMove = 0;
            this.entity.moveTo(newLoc);
        } else {
            this.ticksCouldntMove++;
        }
    }

    private boolean isTarget(Entity potentialTarget) {
        for (Class<? extends Entity> targetClass : this.targetClasses) {
            if (potentialTarget.isAlive() && targetClass.isInstance(potentialTarget)) {
                return true;
            }
        }

        return false;
    }
}
