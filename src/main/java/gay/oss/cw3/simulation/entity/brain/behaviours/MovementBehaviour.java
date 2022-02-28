package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.brain.Behaviour;

import java.util.Random;

/**
 * An abstract class for movement-based behaviours to build upon.
 *
 * <p>Contains many useful things for a behaviour that can move entities around, such as fields to keep track of
 * movement speed and the entity this behaviour is attached to, and a method to calculate the movement vector in a
 * particular direction given, regarding speed.</p>
 */
public abstract class MovementBehaviour implements Behaviour {
    protected final double speed;
    protected final Entity entity;
    protected final Random random = new Random();

    /**
     * Create a new MovementBehaviour.
     *
     * @param speed     the movement speed modifier
     * @param entity    the entity this behaviour is attached to
     */
    protected MovementBehaviour(double speed, Entity entity) {
        this.speed = speed;
        this.entity = entity;
    }

    /**
     * Calculate movement in a direction.
     *
     * <p>This uses random variation so that overall, entities with a higher movement speed will go further in the same
     * time.</p>
     *
     * @param direction a vector describing the direction to go
     *
     * @return          the coordinate of the entity's new position, centred on the entity
     */
    protected Coordinate calculateMovementInDirection(final Coordinate direction) {
        double magnitude = direction.distanceToOrigin();
        double chanceToMoveX = Math.abs(direction.x/magnitude);
        double chanceToMoveZ = Math.abs(direction.z/magnitude);
        return new Coordinate(
                Math.round(chanceToMoveZ == 0 || this.random.nextDouble() < chanceToMoveX ? Math.signum(direction.x) : 0f),
                Math.round(chanceToMoveX == 0 || this.random.nextDouble() < chanceToMoveZ ? Math.signum(direction.z) : 0f)
        ).multiply(Math.round(this.speed * this.entity.getAttributes().get(EntityAttribute.MOVEMENT_SPEED) * random.nextDouble()));
    }
}
