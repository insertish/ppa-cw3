package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;

import java.util.Random;

/**
 * A behaviour that makes entities wander around.
 *
 * <p>A direction is chosen when the behaviour starts, and the entity will move in that direction. With a 0.5% chance
 * (roughly every 200 ticks), the entity will change this direction.</p>
 */
public class WanderAroundBehaviour extends MovementBehaviour {
    private final Random random = new Random();

    private Coordinate direction = null;

    /**
     * Create a new WanderAroundBehaviour
     *
     * @param entity    the entity
     * @param speed     the movement speed modifier
     */
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
        this.setDirection();
    }

    @Override
    public void tick() {
        if (random.nextFloat() < 0.005) {
            this.setDirection();
        }

        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(direction));

        if (this.entity.canMoveTo(newLoc)) {
            this.entity.moveTo(newLoc);
        } else {
            this.setDirection();
        }
    }

    private void setDirection() {
        this.direction = new Coordinate(random.nextInt(3)-1, random.nextInt(3)-1);
    }
}
