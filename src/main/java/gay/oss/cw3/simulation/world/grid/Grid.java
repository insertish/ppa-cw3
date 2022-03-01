package gay.oss.cw3.simulation.world.grid;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Grid<T> {
    int getWidth();

    int getDepth();

    T set(int x, int z, T obj);

    default T set(Coordinate location, T obj) {
        return this.set(location.x, location.z, obj);
    }

    void setWithoutOverwrite(Coordinate location, T obj);

    T get(int x, int z);

    List<T> getInRadius(@Nullable T except, Coordinate around, int radius);

    default T get(Coordinate location) {
        return this.get(location.x, location.z);
    }

    default boolean isInBounds(int x, int z) {
        return !(x < 0 || z < 0 || x >= this.getWidth() || z >= this.getDepth());
    }
}
