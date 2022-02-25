package gay.oss.cw3.simulation;

import org.jetbrains.annotations.Nullable;

public class Grid<T> {
    public final Object lock = new Object();

    private final Object[][] grid;
    private final int width;
    private final int depth;

    public Grid(int width, int depth) {
        this.grid = new Object[width][depth];
        this.width = width;
        this.depth = depth;
    }

    public int getWidth() {
        return this.width;
    }

    public int getDepth() {
        return this.depth;
    }

    @SuppressWarnings("unchecked")
    public T set(int x, int z, T obj) {
        synchronized (lock) {
            if (!this.isInBounds(x, z)) return null; // or throw
            T t = (T) this.grid[x][z];
            this.grid[x][z] = obj;
            return t;
        }
    }

    public T set(Coordinate location, T obj) {
        return this.set(location.x, location.z, obj);
    }

    public void setWithoutOverwrite(Coordinate location, T obj) {
        synchronized (lock) {
            final @Nullable T previous;
            if ((previous = this.get(location)) != null) {
                throw new IllegalArgumentException(String.format("%1$s should be empty, contained %2$s", location, previous));
            }

            this.set(location, obj);
        }
    }

    @SuppressWarnings("unchecked")
    public @Nullable T get(int x, int z) {
        synchronized (lock) {
            if (!this.isInBounds(x, z)) return null;
            return (T) this.grid[x][z];
        }
    }

    public @Nullable T get(Coordinate location) {
        return this.get(location.x, location.z);
    }

    public boolean isInBounds(int x, int z) {
        return !(x < 0 || z < 0 || x >= width || z >= depth);
    }
}
