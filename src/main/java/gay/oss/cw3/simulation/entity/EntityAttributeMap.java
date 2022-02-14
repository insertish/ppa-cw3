package gay.oss.cw3.simulation.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public final class EntityAttributeMap {
    private final Map<EntityAttribute, Double> map = new EnumMap<>(EntityAttribute.class);
    private final Random random = new Random();

    private EntityAttributeMap(final Map<EntityAttribute, Double> map) {
        this.map.putAll(map);
    }

    public EntityAttributeMap() {
    }

    public void set(final EntityAttribute attribute, final double value) {
        checkBounds(attribute, value);

        this.map.put(attribute, value);
    }

    public void set(final EntityAttribute attribute, final double value, final double variation) {
        checkBounds(attribute, value);

        final double modifiedValue = Math.max(attribute.min, Math.min(attribute.max, value + calculateRandomVariation(attribute, value, variation, random)));

        this.map.put(attribute, modifiedValue);
    }

    public double get(final EntityAttribute attribute) {
        return this.map.getOrDefault(attribute, attribute.defaultValue);
    }

    public void inheritFromParents(final EntityAttributeMap parentA, final EntityAttributeMap parentB, final double randomness) {
        final Map<EntityAttribute, Double> results = new EnumMap<>(EntityAttribute.class);

        for (EntityAttribute attribute : EntityAttribute.values()) {
            final Double valueA = parentA.map.get(attribute);
            final Double valueB = parentB.map.get(attribute);

            if (valueA == null && valueB != null) {
                results.put(attribute, valueB + calculateRandomVariation(attribute, valueB, randomness, random));
            } else if (valueB == null && valueA != null) {
                results.put(attribute, valueA + calculateRandomVariation(attribute, valueA, randomness, random));
            } else if (valueA != null) {
                final double average = ((valueA+valueB)/2.0);
                results.put(attribute, average + calculateRandomVariation(attribute, average, randomness, random));
            }
        }

        this.map.putAll(results);
    }

    private static double calculateRandomVariation(final EntityAttribute attribute, final double currentValue, final double randomness, final Random random) {
        final double range = Math.abs(1.0/((Math.abs(currentValue - attribute.defaultValue))/attribute.defaultValue))*attribute.variation;

        final double variation = random.nextGaussian() * randomness;

        return variation * range;
    }

    private static void checkBounds(final EntityAttribute attribute, final double value) {
        if (value < attribute.min) {
            throw new IllegalArgumentException(String.format("Tried to set %s to %f, which is less than its minimum value %f!", attribute.name(), value, attribute.min));
        }

        if (value > attribute.max) {
            throw new IllegalArgumentException(String.format("Tried to set %s to %f, which is greater than its maximum value %f!", attribute.name(), value, attribute.max));
        }
    }
}
