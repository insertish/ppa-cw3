package gay.oss.cw3.simulation.world.grid;

import gay.oss.cw3.simulation.Coordinate;
import org.jetbrains.annotations.Nullable;

public class BasicGrid<T> implements Grid<T> {
    protected final Object lock = new Object();

    protected final Object[][] grid;
    protected final int width;
    protected final int depth;

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
    public @Nullable T get(Coordinate location) {
        return this.get(location.x, location.z);
    }

}
