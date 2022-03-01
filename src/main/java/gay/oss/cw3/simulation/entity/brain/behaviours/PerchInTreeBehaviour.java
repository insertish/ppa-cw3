package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.scenarios.entities.Tree;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * A behaviour that makes entities perch in trees during the evening and night.
 */
public class PerchInTreeBehaviour extends MovementBehaviour {
    private Tree tree = null;

    /**
     * Create a new PerchInTreeBehaviour.
     *
     * @param entity    the entity
     * @param speed     the movement speed modifier
     */
    public PerchInTreeBehaviour(double speed, Entity entity) {
        super(speed, entity);
    }

    @Override
    public boolean canStart() {
        if (entity.getWorld().getDayCycle() != DayCycle.EVENING && entity.getWorld().getDayCycle() != DayCycle.NIGHT) {
            return false;
        }

        this.tree = null;

        this.findTree();

        return this.tree != null;
    }

    @Override
    public boolean canContinue() {
        if (entity.getWorld().getDayCycle() != DayCycle.EVENING && entity.getWorld().getDayCycle() != DayCycle.NIGHT) {
            return false;
        }

        if (this.tree == null) {
            this.findTree();
        }

        return this.tree != null;
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        var dir = this.tree.getLocation().subtract(this.entity.getLocation());
        var newLoc = entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (entity.canMoveTo(newLoc)) {
            entity.moveTo(newLoc);
        }
    }

    private void findTree() {
        this.tree = this.entity.getWorld().getEntitiesAround(EntityLayer.FOLIAGE, null, this.entity.getLocation(), 40).stream()
                .filter(Tree.class::isInstance)
                .map(Tree.class::cast)
                .findAny()
                .orElse(null);
    }
}
