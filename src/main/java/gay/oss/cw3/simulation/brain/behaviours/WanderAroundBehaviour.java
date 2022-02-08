package gay.oss.cw3.simulation.brain.behaviours;

import gay.oss.cw3.provided.Location;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.Entity;
import gay.oss.cw3.simulation.brain.Behaviour;

import java.util.Random;

public class WanderAroundBehaviour implements Behaviour {
    private final Random random = new Random();
    private final Entity entity;

    public WanderAroundBehaviour(Entity entity) {
        this.entity = entity;
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
        this.entity.setLocation(this.entity.getLocation().add(new Coordinate(random.nextInt(3)-1, random.nextInt(3)-1)));
    }
}
