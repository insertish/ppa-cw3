package gay.oss.cw3.simulation.entity.brain;

import gay.oss.cw3.simulation.entity.Entity;

/**
 * A Behaviour is the basic 'unit of AI' for an {@link Entity Entity}.
 *
 * <p>Behaviours are attached to an entity through their {@link EntityBrain Brain}.</p>
 *
 * @see EntityBrain
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public interface Behaviour {
    /**
     * Determines whether this behaviour can start (having not been run last tick)
     *
     * @return whether this behaviour can start
     */
    boolean canStart();

    /**
     * Determines whether this behaviour can continue (having been run last tick)
     *
     * @return whether this behaviour can continue
     */
    boolean canContinue();

    /**
     * Starts this behaviour. Run once, before {@link #tick()}, when this behaviour is run having not run last tick.
     */
    void start();

    /**
     * Ticks this behaviour, running its logic. Will only be run if {@link #canStart()} or {@link #canContinue()}
     * has returned true this tick.
     */
    void tick();
}
