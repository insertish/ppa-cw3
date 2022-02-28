package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.brain.Behaviour;
import gay.oss.cw3.simulation.world.attributes.DayCycle;

/**
 * A behaviour that makes entities sleep.
 *
 * <p>It supports two modes - in regular mode, the entity will sleep in the night. In the nocturnal mode, it will sleep
 * in the afternoon.</p>
 *
 * <p>It is recommended to put this behaviour above most others, but below fleeing behaviours.</p>
 */
public class SleepBehaviour implements Behaviour {
    private final Entity entity;
    private final boolean isNocturnal;

    /**
     * Create a new SleepBehaviour.
     *
     * @param entity        the entity
     * @param isNocturnal   whether this behaviour should be in nocturnal mode
     */
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
