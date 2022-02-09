package gay.oss.cw3.simulation.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.Entity;
import gay.oss.cw3.simulation.brain.Behaviour;

import java.util.Random;

public class HuntBehaviour implements Behaviour {
    private final Random random = new Random();
    private final Entity entity;
    private final Class<? extends Entity>[] targetClasses;

    private Entity target;
    private int ticksCouldntMove = 0;

    @SafeVarargs
    public HuntBehaviour(Entity entity, Class<? extends Entity>... targetClasses) {
        this.entity = entity;
        this.targetClasses = targetClasses;
    }

    @Override
    public boolean canStart() {
        this.target = null;
        this.ticksCouldntMove = 0;

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
        if (this.entity.getLocation().distanceTo(this.target.getLocation()) < 2) {
            this.ticksCouldntMove = 0;
            this.target.setAlive(false);
            this.entity.setLocation(this.target.getLocation());
            System.out.println("gotcha A");
            return;
        }

        var dir = this.target.getLocation().subtract(this.entity.getLocation());
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (this.entity.getWorld().isInBounds(newLoc)) {
            var entityAtLoc = this.entity.getWorld().getEntity(newLoc.x, newLoc.z);

            if (entityAtLoc == null) {
                this.ticksCouldntMove = 0;
                this.entity.setLocation(newLoc);
            } else if (entityAtLoc == this.target) {
                this.ticksCouldntMove = 0;
                this.target.setAlive(false);
                this.entity.setLocation(newLoc);
                System.out.println("gotcha B");
            } else {
                this.ticksCouldntMove++;
            }
        } else {
            this.ticksCouldntMove++;
        }
    }

    private Coordinate calculateMovementInDirection(final Coordinate direction) {
        double magnitude = direction.distanceToOrigin();
        double chanceToMoveX = Math.abs(direction.x/magnitude);
        double chanceToMoveZ = Math.abs(direction.z/magnitude);
        return new Coordinate(
                Math.round(chanceToMoveZ == 0 || this.random.nextDouble() < chanceToMoveX ? Math.signum(direction.x) : 0f),
                Math.round(chanceToMoveX == 0 || this.random.nextDouble() < chanceToMoveZ ? Math.signum(direction.z) : 0f)
        );
    }
}
