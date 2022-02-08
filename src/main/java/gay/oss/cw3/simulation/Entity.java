package gay.oss.cw3.simulation;

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
    }

    public Entity(World world, int initialAgeTicks, boolean alive) {
        this(world, new Coordinate(), initialAgeTicks, alive);
    }

    public World getWorld() {
        return world;
    }

    public Coordinate getLocation() {
        return this.location;
    }

    public boolean isAlive() {
        return alive;
    }

    public int getAgeTicks() {
        return ageTicks;
    }

    public void setLocation(Coordinate location) {
        this.location = location;
    }
    
    public void incrementAge() {
        this.ageTicks++;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public abstract void tick();
}
