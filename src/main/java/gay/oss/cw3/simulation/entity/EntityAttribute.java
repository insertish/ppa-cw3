package gay.oss.cw3.simulation.entity;

public enum EntityAttribute {
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
