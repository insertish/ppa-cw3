package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.scenarios.entities.Bird;
import gay.oss.cw3.scenarios.entities.Tree;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class PerchInTreeBehaviour extends MovementBehaviour {
    private Tree tree = null;

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
        this.tree = entity.getAdjacentEntities(EntityLayer.FOLIAGE, 40).stream()
                .filter(Tree.class::isInstance)
                .map(Tree.class::cast)
                .findAny()
                .orElse(null);
    }
}
