package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.brain.Behaviour;

import java.util.Random;

public abstract class MovementBehaviour implements Behaviour {
    protected final double speed;
    protected final Entity entity;
    protected final Random random = new Random();

    protected MovementBehaviour(double speed, Entity entity) {
        this.speed = speed;
        this.entity = entity;
    }

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
