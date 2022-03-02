package gay.oss.cw3.simulation.world.grid;

import gay.oss.cw3.simulation.Coordinate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple implementation of {@link Grid}
 *
 * @param <T> the type stored in this grid
 *
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class BasicGrid<T> implements Grid<T> {
    protected final Object lock = new Object();

    protected final Object[][] grid;
    protected final int width;
    protected final int depth;

    /**
     * Creates a new BasicGrid
     *
     * @param width the width
     * @param depth the depth
     */
    public BasicGrid(int width, int depth) {
        this.grid = new Object[width][depth];
        this.width = width;
        this.depth = depth;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getDepth() {
        return this.depth;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T set(int x, int z, T obj) {
        synchronized (lock) {
            if (!this.isInBounds(x, z)) return null; // or throw
            T t = (T) this.grid[x][z];
            this.grid[x][z] = obj;
            return t;
        }
    }

    @Override
    public void setWithoutOverwrite(Coordinate location, T obj) {
        synchronized (lock) {
            final @Nullable T previous;
            if ((previous = this.get(location)) != null) {
                throw new IllegalArgumentException(String.format("%1$s should be empty, contained %2$s", location, previous));
            }

            this.set(location, obj);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable T get(int x, int z) {
        synchronized (lock) {
            if (!this.isInBounds(x, z)) return null;
            return (T) this.grid[x][z];
        }
    }

    @Override
    public List<T> getInRadius(@Nullable T except, Coordinate around, int radius) {
        int x = around.x;
        int z = around.z;

        List<T> entries = new ArrayList<>();
        for (int i=-radius;i<radius+1;i++) {
            for (int j=-radius;j<radius+1;j++) {
                T e = this.get(x + i, z + j);
                if (e != null && e != except) entries.add(e);
            }
        }

        return entries;
    }

    @Override
    public @Nullable T get(Coordinate location) {
        return this.get(location.x, location.z);
    }
}
