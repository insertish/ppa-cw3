package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.EntityLayer;

import java.util.Random;

public class EatFoliageBehaviour extends MovementBehaviour {
    private final Random random = new Random();
    private final double targetFullnessFraction;
    private final Class<? extends Entity>[] targetClasses;

    private Entity target;
    private int ticksCouldntMove = 0;

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

        var potentialTargets = this.entity.getAdjacentEntities(5);
        if (potentialTargets.isEmpty()) {
            return false;
        }

        var potentialTarget = potentialTargets.get(random.nextInt(potentialTargets.size()));
        for (Class<? extends Entity> targetClass : this.targetClasses) {
            if (potentialTarget.isAlive() && targetClass.isInstance(potentialTarget)) {
                this.target = potentialTarget;
                break;
            }
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
        if (this.entity.getLocation().equals(this.target.getLocation())) {
            this.ticksCouldntMove = 0;
            this.target.setAlive(false);
            this.entity.addFullness(this.target.getFullness()*0.7);
            return;
        }

        var dir = this.target.getLocation().subtract(this.entity.getLocation());
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (this.entity.getWorld().isInBounds(newLoc)) {
            var entityAtLocInTheWay = this.entity.getWorld().getEntity(this.entity.getLayer(), newLoc.x, newLoc.z);

            if (entityAtLocInTheWay == null) {
                this.ticksCouldntMove = 0;
                this.entity.setLocation(newLoc);
            } else {
                this.ticksCouldntMove++;
            }
        } else {
            this.ticksCouldntMove++;
        }
    }
}
