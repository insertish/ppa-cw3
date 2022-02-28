package gay.oss.cw3.simulation.entity;

/**
 * EntityAttributes are special values that entities have, stored in their {@link EntityAttributeMap}. They have
 * minimum and maximum values, as well as a default value used for entities with no specified value.
 */
public enum EntityAttribute {
    /**
     * The entity's movement speed. Lower values mean that the entity is more likely to skip movement for a tick
     * in {@link gay.oss.cw3.simulation.entity.brain.behaviours.MovementBehaviour MovementBehaviours}.
     */
    MOVEMENT_SPEED(1, 0.5, Double.POSITIVE_INFINITY, 0.5),
    /**
     * The entity's maximum health value.
     */
    MAX_HEALTH(1, 1, Double.POSITIVE_INFINITY, 2),
    /**
     * The minimum number of ticks that an entity with a
     * {@link gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour BreedBehaviour} must be alive for before
     * it tries to mate.
     */
    MINIMUM_BREEDING_AGE(10, 0, Double.POSITIVE_INFINITY, 10),
    /**
     * The minimum number of ticks that an entity must wait after breeding to do so again.
     */
    TICKS_BETWEEN_BREEDING_ATTEMPTS(10, 0, Double.POSITIVE_INFINITY, 10),
    /**
     * The maximum fullness of an entity.
     */
    MAX_FULLNESS(10, 0, Double.POSITIVE_INFINITY, 3),
    /**
     * The fullness that an entity uses up in breeding. Due to conservation of energy this should usually be half or
     * more of the default fullness of the entity.
     */
    FULLNESS_TO_BREED(2.0, 1.0, 10.0, 0.2)
    ;

    final double defaultValue;
    final double min;
    final double max;
    final double variation;

    EntityAttribute(double defaultValue, double min, double max, double variation) {
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.variation = variation;
    }
}
