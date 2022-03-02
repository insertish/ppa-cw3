package gay.oss.cw3.simulation.entity.brain;

import gay.oss.cw3.simulation.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * An EntityBrain controls the AI of an {@link Entity Entity}, managing its
 * {@link Behaviour Behaviours}.
 *
 * <p>It has a list of behaviours, and will tick the first behaviour in the list which can start/continue. Therefore,
 * behaviours earlier in the list have priority over later ones. <em>Only one behaviour will be used each tick.</em></p>
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public final class EntityBrain {
    private final List<Behaviour> behaviours = new ArrayList<>();
    private Behaviour currentBehaviour = null;

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
        final var lastRunBehaviour = this.currentBehaviour;
        this.currentBehaviour = null;
        for (Behaviour behaviour : this.behaviours) {
            if (behaviour == lastRunBehaviour) {
                if (behaviour.canContinue()) {
                    behaviour.tick();
                    this.currentBehaviour = behaviour;
                    break;
                }
            } else if (behaviour.canStart()) {
                behaviour.start();
                behaviour.tick();
                this.currentBehaviour = behaviour;
                break;
            }
        }
    }
}
