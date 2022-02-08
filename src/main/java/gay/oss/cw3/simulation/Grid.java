package gay.oss.cw3.simulation;

import org.jetbrains.annotations.Nullable;

public class Grid<T> {
    private Object[][] grid;
    private final int width;
    private final int depth;

    public Grid(int width, int depth) {
        this.grid = new Object[width][depth];
        this.width = width;
        this.depth = depth;
    }

    @SuppressWarnings("unchecked")
    public T set(int x, int z, T obj) {
        if (x < 0 || z < 0 || x >= width || z >= depth) return null; // or throw
        T t = (T) this.grid[x][z];
        this.grid[x][z] = obj;
        return t;
    }

    public T set(Coordinate location, T obj) {
        return this.set(location.x, location.z, obj);
    }

    @SuppressWarnings("unchecked")
    public @Nullable T get(int x, int z) {
        if (x < 0 || z < 0 || x >= width || z >= depth) return null;
        return (T) this.grid[x][z];
    }

    public @Nullable T get(Coordinate location) {
        return this.get(location.x, location.z);
    }
}
