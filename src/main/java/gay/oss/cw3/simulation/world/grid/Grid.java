package gay.oss.cw3.simulation.world.grid;

import gay.oss.cw3.simulation.Coordinate;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * An interface representing a two-dimensional grid of objects.
 *
 * @param <T> the type stored in this grid
 *
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public interface Grid<T> {
    /**
     * @return the width of this grid
     */
    int getWidth();

    /**
     * @return the depth of this map
     */
    int getDepth();

    /**
     * Inserts an object into the grid at a given position.
     *
     * @param x     the x coordinate
     * @param z     the z coordinate
     * @param obj   the object to insert
     *
     * @return      the object that used to be there, if any
     */
    T set(int x, int z, T obj);

    /**
     * Convenience wrapper for {@link #set(int, int, Object)}.
     *
     * @param location  the coordinate
     * @param obj       the object to insert
     *
     * @return          the object that used to be there, if any
     */
    default T set(Coordinate location, T obj) {
        return this.set(location.x, location.z, obj);
    }

    /**
     * Inserts an object into the grid, if there isn't something there already.
     *
     * @param location  the coordinate
     * @param obj       the object to insert
     */
    void setWithoutOverwrite(Coordinate location, T obj);

    /**
     * Gets the object at a position on the grid.
     *
     * @param x     the x position
     * @param z     the z position
     * @return      the object at that position
     */
    T get(int x, int z);

    /**
     * Get all objects on the grid around a position, except for a given one.
     *
     * @param except (nullable) object to exclude
     * @param around the position to search around
     * @param radius the radius to search around
     *
     * @return       a list of objects
     */
    List<T> getInRadius(@Nullable T except, Coordinate around, int radius);

    /**
     * Convenience wrapper for {@link #get(int, int)}
     *
     * @param location  the position
     * @return          the object at that position
     */
    default T get(Coordinate location) {
        return this.get(location.x, location.z);
    }

    /**
     * Determines whether a position is in bounds of the grid.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     *
     * @return  whether the position is in bounds
     */
    default boolean isInBounds(int x, int z) {
        return !(x < 0 || z < 0 || x >= this.getWidth() || z >= this.getDepth());
    }
}
