package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an entity that can breed to create children.
 *
 * @see AbstractBreedableEntity
 * @see gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public interface Breedable {
    /**
     * Create a child entity at {@code coordinate} with the other parent being {@code otherParent}.
     *
     * <p>The result is nullable - null should be returned in such a situation where creating a child would not be
     * possible.</p>
     *
     * @param otherParent   the other parent
     * @param coordinate    the coordinate to spawn the child at
     *
     * @return              the child, or null
     */
    @Nullable Entity createChild(Entity otherParent, Coordinate coordinate);

    /**
     * The {@link Sex} of the entity. Can return null if the concept does not apply.
     *
     * @return  the sex of the entity
     */
    @Nullable Sex getSex();

    /**
     * Determines whether this entity is compatible for breeding with another.
     *
     * @param entity    the other entity
     *
     * @return          whether the two entities are compatible
     */
    boolean isCompatible(Entity entity);

    /**
     * Determines whether this entity can breed right now.
     *
     * @return whether this entity can breed right now
     */
    boolean canBreed();

    /**
     * Determines whether this entity is one that can give birth.
     *
     * @return whether this entity can give birth
     *
     * @see #getSex()
     */
    boolean canGiveBirth();

    /**
     * Mark the start of a breeding attempt.
     */
    void startBreedingAttempt();
}
