package gay.oss.cw3.simulation.entity;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * An Entity's EntityAttributeMap stores and provides its values for {@link EntityAttribute EntityAttributes}.
 */
public final class EntityAttributeMap {
    private final Map<EntityAttribute, Double> map = new EnumMap<>(EntityAttribute.class);
    private final Random random = new Random();

    private EntityAttributeMap(final Map<EntityAttribute, Double> map) {
        this.map.putAll(map);
    }

    /**
     * Creates a new EntityAttributeMap which provides default values for all attributes.
     */
    public EntityAttributeMap() {
    }

    /**
     * Sets the value of an attribute to the given value.
     *
     * <p>Will throw {@link IllegalArgumentException} if {@code value} is outside of the attribute's bounds.</p>
     *
     * @param attribute the attribute to set
     * @param value     the value to set it to
     */
    public void set(final EntityAttribute attribute, final double value) {
        checkBounds(attribute, value);

        this.map.put(attribute, value);
    }

    /**
     * Sets the value of an attribute to the given value with some random variation.
     *
     * <p>Will throw {@link IllegalArgumentException} if {@code value} is outside of the attribute's bounds.</p>
     *
     * @param attribute the attribute to set
     * @param value     the value to set it to
     * @param variation the factor of random variation to apply
     */
    public void set(final EntityAttribute attribute, final double value, final double variation) {
        checkBounds(attribute, value);

        final double modifiedValue = Math.max(attribute.min, Math.min(attribute.max, value + calculateRandomVariation(attribute, value, variation, random)));

        this.map.put(attribute, modifiedValue);
    }

    /**
     * Gets the value associated with the attribute, or else the default value of the attribute.
     *
     * @param attribute the attribute
     *
     * @return the value of the attribute
     */
    public double get(final EntityAttribute attribute) {
        return this.map.getOrDefault(attribute, attribute.defaultValue);
    }

    /**
     * Populate this EntityAttributeMap with values based on that of two others. This is used to make the attributes
     * of children be based on that of its parents.
     *
     *
     * <p>For each attribute A:</p>
     * <ul>
     *     <li>If both parents have a value for A, the result is the average of their values plus random variation.</li>
     *     <li>If one parent has a value for A, the result is that value plus some random variation.</li>
     *     <li>If neither parent has a value for A, no value will be set for A.</li>
     * </ul>
     *
     * @param parentA       one of the 'parent' attribute maps
     * @param parentB       the other 'parent' attribute map
     * @param randomness    the random variation factor
     */
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
