package gay.oss.cw3.simulation.entity;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * Represents the sex of an entity.
 */
// binary moment :I
public enum Sex {
    /**
     * The male sex.
     */
    MALE,
    /**
     * The female sex.
     */
    FEMALE,
    ;

    /**
     * Whether two sexes are compatible for producing children.
     *
     * @param a the first sex
     * @param b the other sex
     *
     * @return  whether the two can produce children
     */
    public static boolean isCompatible(@Nullable Sex a, @Nullable Sex b) {
        return a != null && b != null && a != b;
    }
}
