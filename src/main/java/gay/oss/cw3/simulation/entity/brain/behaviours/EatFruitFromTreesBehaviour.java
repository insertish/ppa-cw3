package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.scenarios.entities.Tree;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

import java.util.Comparator;
import java.util.Random;

/**
 * A behaviour to make an entity eat fruit from trees.
 *
 * @see Tree
 */
public class EatFruitFromTreesBehaviour extends MovementBehaviour {
    private final double targetFullnessFraction;

    private Tree target;
    private int ticksCouldntMove = 0;

    /**
     * Create a new EatFruitFromTreesBehaviour.
     *
     * @param entity                    the entity
     * @param speed                     the movement speed modifier
     * @param targetFullnessFraction    the fullness that this entity will try to reach by eating fruit
     */
    public EatFruitFromTreesBehaviour(Entity entity, double speed, double targetFullnessFraction) {
        super(speed, entity);
        this.targetFullnessFraction = targetFullnessFraction;
    }

    @Override
    public boolean canStart() {
        this.target = null;
        this.ticksCouldntMove = 0;

        if (this.entity.getFullness() >= this.targetFullnessFraction*this.entity.getMaxFullness()) {
            return false;
        }

        this.target = this.entity.getWorld().getEntitiesAround(EntityLayer.FOLIAGE, null, this.entity.getLocation(), 10).stream()
                .filter(Tree.class::isInstance)
                .map(Tree.class::cast)
                .filter(Tree::hasFruit)
                .min(Comparator.comparing(e -> e.getLocation().sqrDistanceTo(this.entity.getLocation())))
                .orElse(null);

        return this.target != null;
    }

    @Override
    public boolean canContinue() {
        return this.target.isAlive() && this.target.hasFruit() && this.ticksCouldntMove < 3;
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        if (this.entity.getLocation().sqrDistanceTo(this.target.getLocation()) <= 2) {
            this.ticksCouldntMove = 0;
            this.target.removeFruit();
            this.entity.addFullness(Tree.FRUIT_FULLNESS*0.7);
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
}
