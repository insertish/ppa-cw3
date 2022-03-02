package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

import java.util.Random;

/**
 * A behaviour to hunt down and kill other entities.
 *
 * <p>The other entities must be on the same {@link gay.oss.cw3.simulation.world.attributes.EntityLayer layer}. The
 * victims will be eaten and 70% of their fullness transferred to the entity this behaviour is attached to.</p>
 */
public class HuntBehaviour extends MovementBehaviour {
    private final Random random = new Random();
    private final double targetFullnessFraction;
    private final Class<? extends Entity>[] targetClasses;
    private final EntityLayer layer;
    private final int viewDistance;

    private Entity target;
    private int ticksCouldntMove = 0;

    /**
     * Creates a new HuntBehaviour.
     *  @param entity                    the entity
     * @param speed                     the movement speed modifier
     * @param targetFullnessFraction    the fullness that this entity will try to reach by hunting
     * @param viewDistance
     * @param targetClasses             the classes that are valid hunting targets
     */
    @SafeVarargs
    public HuntBehaviour(Entity entity, double speed, EntityLayer layer, double targetFullnessFraction, int viewDistance, Class<? extends Entity>... targetClasses) {
        super(speed, entity);
        this.layer = layer;
        this.targetFullnessFraction = targetFullnessFraction;
        this.viewDistance = viewDistance;
        this.targetClasses = targetClasses;
    }

    @SafeVarargs
    public HuntBehaviour(Entity entity, double speed, double targetFullnessFraction, int viewDistance, Class<? extends Entity>... targetClasses) {
        super(speed, entity);
        this.layer = entity.getLayer();
        this.targetFullnessFraction = targetFullnessFraction;
        this.viewDistance = viewDistance;
        this.targetClasses = targetClasses;
    }

    @Override
    public boolean canStart() {
        this.target = null;
        this.ticksCouldntMove = 0;

        if (this.entity.getFullness() >= this.targetFullnessFraction*this.entity.getMaxFullness()) {
            return false;
        }

        var potentialTargets = this.entity.getWorld().getEntitiesAround(this.layer, this.entity, this.entity.getLocation(), this.viewDistance);
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
        if (this.entity.getLocation().distanceTo(this.target.getLocation()) < 2) {
            this.ticksCouldntMove = 0;
            this.entity.moveToOverwriting(this.target.getLocation());
            this.target.setAlive(false);
            this.entity.addFullness(this.target.getFullness()*0.7);
            return;
        }

        var dir = this.target.getLocation().subtract(this.entity.getLocation());
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (this.entity.canMoveToDisregardingOccupancy(newLoc)) {
            var entityAtLoc = this.entity.getWorld().getEntity(this.entity.getLayer(), newLoc.x, newLoc.z);

            if (entityAtLoc == null) {
                this.ticksCouldntMove = 0;
                this.entity.moveTo(newLoc);
            } else if (entityAtLoc == this.target) {
                this.ticksCouldntMove = 0;
                this.entity.moveToOverwriting(newLoc);
                this.target.setAlive(false);
                this.entity.addFullness(this.target.getFullness()*0.7);
            } else {
                this.ticksCouldntMove++;
            }
        } else {
            this.ticksCouldntMove++;
        }
    }

}
