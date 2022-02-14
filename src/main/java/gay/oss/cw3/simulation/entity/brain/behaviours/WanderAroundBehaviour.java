package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;

import java.util.Random;

public class WanderAroundBehaviour extends MovementBehaviour {
    private final Random random = new Random();

    public WanderAroundBehaviour(Entity entity, double speed) {
        super(speed, entity);
    }

    @Override
    public boolean canStart() {
        return true;
    }

    @Override
    public boolean canContinue() {
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void tick() {
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(
                        new Coordinate(random.nextInt(3)-1, random.nextInt(3)-1))
                );

        if (this.entity.getWorld().isInBounds(newLoc) && this.entity.getWorld().getEntity(newLoc.x, newLoc.z) == null) {
            this.entity.setLocation(newLoc);
        }
    }
}
