package gay.oss.cw3.simulation.entity;

import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

// binary moment :I
public enum Sex {
    MALE,
    FEMALE,
    ;

    public static boolean isCompatible(@Nullable Sex a, @Nullable Sex b) {
        return a != null && b != null && a != b;
    }
}
