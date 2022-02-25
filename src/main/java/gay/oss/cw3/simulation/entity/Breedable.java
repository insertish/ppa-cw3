package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import org.jetbrains.annotations.Nullable;

public interface Breedable {
    @Nullable Entity createChild(Entity otherParent, Coordinate coordinate);

    @Nullable Sex getSex();

    boolean isCompatible(Entity entity);

    boolean canBreed();

    boolean canGiveBirth();

    void startBreedingAttempt();
}
