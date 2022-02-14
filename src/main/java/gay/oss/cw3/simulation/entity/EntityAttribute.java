package gay.oss.cw3.simulation.entity;

public enum EntityAttribute {
    MOVEMENT_SPEED(1, 0.5, Double.POSITIVE_INFINITY, 0.5),
    MAX_HEALTH(1, 1, Double.POSITIVE_INFINITY, 2),
    MINIMUM_BREEDING_AGE(10, 0, Double.POSITIVE_INFINITY, 10),
    TICKS_BETWEEN_BREEDING_ATTEMPTS(10, 0, Double.POSITIVE_INFINITY, 10),
    MAX_FULLNESS(10, 0, Double.POSITIVE_INFINITY, 3),
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
