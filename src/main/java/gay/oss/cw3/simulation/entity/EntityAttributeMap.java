package gay.oss.cw3.simulation.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class EntityAttributeMap {
    private final Map<EntityAttribute, Double> map = new EnumMap<>(EntityAttribute.class);

    private EntityAttributeMap(final Map<EntityAttribute, Double> map) {
        this.map.putAll(map);
    }

    public EntityAttributeMap() {
    }

    public void set(final EntityAttribute attribute, final double value) {
        if (value < attribute.min) {
            throw new IllegalArgumentException(String.format("Tried to set %s to %f, which is less than its minimum value %f!", attribute.name(), value, attribute.min));
        }

        if (value > attribute.max) {
            throw new IllegalArgumentException(String.format("Tried to set %s to %f, which is greater than its maximum value %f!", attribute.name(), value, attribute.max));
        }

        this.map.put(attribute, value);
    }

    public double get(final EntityAttribute attribute) {
        return this.map.getOrDefault(attribute, attribute.defaultValue);
    }

    public void inheritFromParents(final EntityAttributeMap parentA, final EntityAttributeMap parentB, final double randomness, final Random random) {
        final Map<EntityAttribute, Double> results = new EnumMap<>(EntityAttribute.class);

        for (EntityAttribute attribute : EntityAttribute.values()) {
            final Double valueA = parentA.map.get(attribute);
            final Double valueB = parentB.map.get(attribute);

            if (valueA == null && valueB != null) {
                results.put(attribute, valueB + calculateRandomVariation(attribute, randomness, random));
            } else if (valueB == null && valueA != null) {
                results.put(attribute, valueA + calculateRandomVariation(attribute, randomness, random));
            } else if (valueA != null) {
                results.put(attribute, ((valueA+valueB)/2.0) + calculateRandomVariation(attribute, randomness, random));
            }
        }

        this.map.putAll(results);
    }

    private static double calculateRandomVariation(final EntityAttribute attribute, final double randomness, final Random random) {
        final double range = attribute.max-attribute.min;

        final double variation = random.nextGaussian() * randomness;

        return variation * range;
    }
}
