package gay.oss.cw3.simulation;

import org.jetbrains.annotations.Nullable;

public class Grid<T> {
    private final Object lock = new Object();

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
            System.out.printf("%1$s -> %2$s x %3$s%n", obj, t, new Coordinate(x, z));
            return t;
        }
    }

    public T set(Coordinate location, T obj) {
        return this.set(location.x, location.z, obj);
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
