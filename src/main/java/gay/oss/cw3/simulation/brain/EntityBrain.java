package gay.oss.cw3.simulation.brain;

import java.util.ArrayList;
import java.util.List;

/**
 * An EntityBrain controls the AI of an {@link gay.oss.cw3.simulation.Entity Entity}, managing its
 * {@link Behaviour Behaviours}.
 *
 * <p>It has a list of behaviours, and will tick the first behaviour in the list which can start/continue. Therefore,
 * behaviours earlier in the list have priority over later ones. <em>Only one behaviour will be used each tick.</em></p>
 */
public final class EntityBrain {
    private final List<Behaviour> behaviours = new ArrayList<>();
    private Behaviour lastRunBehaviour = null;

    /**
     * Adds a behaviour to this brain, at the lowest priority.
     *
     * @param behaviour the behaviour to add
     */
    public void addBehaviour(final Behaviour behaviour) {
        this.behaviours.add(behaviour);
    }

    /**
     * Adds a behaviour to this brain at a given index, allowing changing the priority.
     *
     * @param index     the index to add the behaviour at
     * @param behaviour the behaviour to add
     */
    public void addBehaviour(final int index, final Behaviour behaviour) {
        this.behaviours.add(index, behaviour);
    }

    /**
     * Ticks this brain, checking behaviours and ticking the relevant one.
     */
    public void tick() {
        for (Behaviour behaviour : this.behaviours) {
            final boolean wasOnLastTime = behaviour == this.lastRunBehaviour;
            if (wasOnLastTime) {
                if (behaviour.canContinue()) {
                    behaviour.tick();
                    this.lastRunBehaviour = behaviour;
                    break;
                }
            } else if (behaviour.canStart()) {
                behaviour.start();
                behaviour.tick();
                this.lastRunBehaviour = behaviour;
                break;
            }
        }
    }
}