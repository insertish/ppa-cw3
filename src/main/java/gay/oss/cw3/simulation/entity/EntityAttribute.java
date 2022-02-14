package gay.oss.cw3.simulation.entity;

public enum EntityAttribute {
    MOVEMENT_SPEED(1, 0.5, Double.POSITIVE_INFINITY),
    MAX_HEALTH(1, 1, Double.POSITIVE_INFINITY),
    MINIMUM_BREEDING_AGE(10, 0, Double.POSITIVE_INFINITY),
    TICKS_BETWEEN_BREEDING_ATTEMPTS(10, 0, Double.POSITIVE_INFINITY)
    ;

    final double defaultValue;
    final double min;
    final double max;

    EntityAttribute(double defaultValue, double min, double max) {
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
    }
}
