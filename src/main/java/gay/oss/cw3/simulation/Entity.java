package gay.oss.cw3.simulation;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
    private final World world;
    private Coordinate location;

    private int ageTicks;
    private boolean alive;

    public Entity(World world, Coordinate location, int initialAgeTicks, boolean alive) {
        this.world = world;
        this.location = location;
        this.ageTicks = initialAgeTicks;
        this.alive = alive;
        this.world.spawn(this);
    }

    public Entity(World world, int initialAgeTicks, boolean alive) {
        this(world, Coordinate.ORIGIN, initialAgeTicks, alive);
    }

    public World getWorld() {
        return this.world;
    }

    public Coordinate getLocation() {
        return this.location;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public int getAgeTicks() {
        return this.ageTicks;
    }

    public void setLocation(Coordinate location) {
        // this should cascade into Grid in World
        this.location = location;
    }
    
    public void incrementAge() {
        this.ageTicks++;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public List<Entity> getAdjacentEntities(int radius) {
        Coordinate location = this.getLocation();
        int x = location.x;
        int z = location.z;

        List<Entity> entities = new ArrayList<>();
        for (int i=-radius;i<radius+1;i++) {
            for (int j=-radius;j<radius+1;j++) {
                Entity e = this.world.getEntity(x + i, z + j);
                if (e != null) entities.add(e);
            }
        }

        return entities;
    }

    public abstract void tick();
}
