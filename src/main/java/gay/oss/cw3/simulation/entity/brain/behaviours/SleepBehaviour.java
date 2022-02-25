package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.brain.Behaviour;
import gay.oss.cw3.simulation.world.attributes.DayCycle;

public class SleepBehaviour implements Behaviour {
    private final Entity entity;
    private final boolean isNocturnal;

    public SleepBehaviour(Entity entity, boolean isNocturnal) {
        this.entity = entity;
        this.isNocturnal = isNocturnal;
    }

    @Override
    public boolean canStart() {
        return this.isNocturnal ?
                this.entity.getWorld().getDayCycle() == DayCycle.AFTERNOON
                : this.entity.getWorld().getDayCycle() == DayCycle.NIGHT;
    }

    @Override
    public boolean canContinue() {
        return this.canStart();
    }

    @Override
    public void start() {}

    @Override
    public void tick() {
        // do nothing.
        // zzz...
    }
}
