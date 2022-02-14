package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.brain.Behaviour;

import java.util.Random;

public abstract class MovementBehaviour implements Behaviour {
    protected double speed;
    protected Random random = new Random();

    protected MovementBehaviour(double speed) {
        this.speed = speed;
    }

    protected Coordinate calculateMovementInDirection(final Coordinate direction) {
        double magnitude = direction.distanceToOrigin();
        double chanceToMoveX = Math.abs(direction.x/magnitude);
        double chanceToMoveZ = Math.abs(direction.z/magnitude);
        return new Coordinate(
                Math.round(chanceToMoveZ == 0 || this.random.nextDouble() < chanceToMoveX ? Math.signum(direction.x) : 0f),
                Math.round(chanceToMoveX == 0 || this.random.nextDouble() < chanceToMoveZ ? Math.signum(direction.z) : 0f)
        ).multiply(Math.round(this.speed * random.nextDouble()));
    }
}
