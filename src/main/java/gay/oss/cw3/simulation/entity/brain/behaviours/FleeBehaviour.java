package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.entity.Entity;

import java.util.Random;

public class FleeBehaviour extends MovementBehaviour {
    private final Random random = new Random();
    private final Entity entity;
    private final int fearDistance;
    private final Class<? extends Entity>[] fearedClasses;

    private Entity threat;
    private int ticksCouldntMove = 0;

    @SafeVarargs
    public FleeBehaviour(Entity entity, double speed, int fearDistance, Class<? extends Entity>... fearedClasses) {
        super(speed);
        this.entity = entity;
        this.fearDistance = fearDistance;
        this.fearedClasses = fearedClasses;
    }

    @Override
    public boolean canStart() {
        this.ticksCouldntMove = 0;

        var potentialThreats = this.entity.getAdjacentEntities(fearDistance);

        this.threat = potentialThreats.stream()
                .filter(e -> {
                    for (Class<? extends Entity> targetClass : this.fearedClasses) {
                        if (targetClass.isInstance(e) && e.isAlive()) {
                            return true;
                        }
                    }

                    return false;
                })
                .findAny()
                .orElse(null);

        return this.threat != null;
    }

    @Override
    public boolean canContinue() {
        return this.threat != null && this.threat.isAlive() && this.ticksCouldntMove < 3;
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        if (this.entity.getLocation().distanceTo(this.threat.getLocation()) > this.fearDistance) {
            this.ticksCouldntMove = 0;
            this.threat = null;
            return;
        }

        var dir = this.entity.getLocation().subtract(this.threat.getLocation());
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (this.entity.getWorld().isInBounds(newLoc)) {
            var entityAtLoc = this.entity.getWorld().getEntity(newLoc.x, newLoc.z);

            if (entityAtLoc == null) {
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
