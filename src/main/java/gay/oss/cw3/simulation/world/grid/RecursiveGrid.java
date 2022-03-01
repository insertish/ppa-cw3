package gay.oss.cw3.simulation.world.grid;

import gay.oss.cw3.simulation.Coordinate;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class RecursiveGrid<T> implements Grid<T> {
    private static final int SUBDIVISIONS = 8;

    protected final Grid<T>[][] grid;
    protected final List<T>[][] entriesInGrids;
    protected final int width;
    protected final int depth;

    @SuppressWarnings("unchecked")
    public RecursiveGrid(int width, int depth) {
        this.grid = (Grid<T>[][]) new Grid[width/SUBDIVISIONS][depth/SUBDIVISIONS];
        this.entriesInGrids = (List<T>[][]) new List[width/SUBDIVISIONS][depth/SUBDIVISIONS];
        this.width = width;
        this.depth = depth;
        for (int x = 0; x < width/SUBDIVISIONS; x++) {
            for (int z = 0; z < depth/SUBDIVISIONS; z++) {
                this.grid[x][z] = new BasicGrid<T>(SUBDIVISIONS, SUBDIVISIONS);
                this.entriesInGrids[x][z] = new ArrayList<>();
            }
        }
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
    public T set(int x, int z, T obj) {
        Grid<T> subgrid;
        List<T> subgridEntities;
        if (!this.isInBounds(x, z)) return null; // or throw
        subgrid = this.grid[x/SUBDIVISIONS][z/SUBDIVISIONS];
        subgridEntities = this.entriesInGrids[x/SUBDIVISIONS][z/SUBDIVISIONS];

        var result = subgrid.set(x % SUBDIVISIONS, z % SUBDIVISIONS, obj);
        subgridEntities.remove(result);
        subgridEntities.add(obj);
        return result;
    }

    @Override
    public void setWithoutOverwrite(Coordinate location, T obj) {
        final @Nullable T previous;
        if ((previous = this.get(location)) != null) {
            throw new IllegalArgumentException(String.format("%1$s should be empty, contained %2$s", location, previous));
        }

        this.set(location, obj);
    }

    @Override
    public @Nullable T get(int x, int z) {
        Grid<T> subgrid;
        if (!this.isInBounds(x, z)) return null; // or throw
        subgrid = this.grid[x/SUBDIVISIONS][z/SUBDIVISIONS];

        return subgrid.get(x % SUBDIVISIONS, z % SUBDIVISIONS);
    }

    @Override
    public List<T> getInRadius(@Nullable T except, Coordinate around, int radius) {
        int x = around.x;
        int z = around.z;

        List<Coordinate> emptyGrids = new ArrayList<>();
        List<T> entries = new ArrayList<>();

        for (int i=-radius;i<radius+1;i++) {
            for (int j=-radius;j<radius+1;j++) {
                Coordinate coordinate = new Coordinate((x+i) / SUBDIVISIONS, (z+j) / SUBDIVISIONS);
                if (!this.isInBounds(x+i, z+j)) continue;

                if (emptyGrids.contains(coordinate)) {
                    continue;
                }

                List<T> entriesInSubGrid = this.entriesInGrids[coordinate.x][coordinate.z];
                if (entriesInSubGrid.isEmpty()) {
                    emptyGrids.add(coordinate);
                    continue;
                }

                var e = this.grid[coordinate.x][coordinate.z].get((x+i) % SUBDIVISIONS, (z+j) % SUBDIVISIONS);
                if (e != null && e != except) entries.add(e);
            }
        }

        return entries;
    }
}
