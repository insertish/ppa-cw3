package gay.oss.cw3.simulation.entity;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;
import gay.oss.cw3.simulation.entity.brain.EntityBrain;

import java.util.ArrayList;
import java.util.List;

/**
 * An Entity is a basic actor in the world. It has a position in the world, an age, and a health value. It can die,
 * if its health goes to zero. It can run custom logic per-tick.
 */
public abstract class Entity {
    private final World world;
    private Coordinate location;

    private int ageTicks;
    private boolean alive;

    private int health;
    private final int maxHealth;

    protected final EntityBrain brain = new EntityBrain();

    /**
     * Creates <em>and automatically spawns</em> an entity.
     *
     * @param world             the world the entity will reside in
     * @param location          the entity's initial location
     * @param initialAgeTicks   the entity's initial age
     * @param alive             whether the entity is alive
     * @param maxHealth         the entity's max health
     */
    public Entity(World world, Coordinate location, int initialAgeTicks, boolean alive, int maxHealth) {
        this.world = world;
        this.location = location;
        this.ageTicks = initialAgeTicks;
        this.alive = alive;
        this.maxHealth = maxHealth;
        this.world.spawn(this);
    }

    /**
     * Creates <em>and automatically spawns</em> an entity at the origin of the world.
     *
     * @param world             the world the entity will reside in
     * @param initialAgeTicks   the entity's initial age
     * @param alive             whether the entity is alive
     * @param maxHealth         the entity's max health
     */
    public Entity(World world, int initialAgeTicks, boolean alive, int maxHealth) {
        this(world, Coordinate.ORIGIN, initialAgeTicks, alive, maxHealth);
    }

    /**
     * @return the entity's world
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * @return the entity's current location
     */
    public Coordinate getLocation() {
        return this.location;
    }

    /**
     * @return whether the entity is currently alive
     */
    public boolean isAlive() {
        return this.alive;
    }

    /**
     * @return the entity's current age in ticks
     */
    public int getAgeTicks() {
        return this.ageTicks;
    }

    /**
     * Sets the entity's location.
     *
     * @param location the location
     */
    public void setLocation(Coordinate location) {
        this.world.moveEntity(this, this.location, location);
        this.location = location;
    }

    /**
     * Increments the entity's age by one tick.
     */
    public void incrementAge() {
        this.ageTicks++;
    }

    /**
     * Sets the entity to be alive or not.
     *
     * @param alive the aliveness value
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    /**
     * @return the entity's current health
     */
    public int getHealth() {
        return health;
    }

    /**
     * @return the entity's maximum health
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the entity's current health, within bounds. Will kill the entity if health is set below one.
     *
     * @param amount the amount of health to set to
     */
    public void setHealth(final int amount) {
        this.health = Math.max(0, Math.min(this.getMaxHealth(), amount));
        if (this.health <= 0) {
            this.setAlive(false);
        }
    }

    /**
     * Adds an amount of health to the entity, with the same logic as {@link #setHealth(int)}
     *
     * @param amount the amount to add to the entity's health
     */
    public void addHealth(final int amount) {
        this.setHealth(this.getHealth()+amount);
    }

    /**
     * @return the entity's brain
     */
    public EntityBrain getBrain() {
        return brain;
    }

    /**
     * Finds a list of entities within the square of radius given around this entity.
     *
     * @param radius    the half-side-length of the square to search around
     *
     * @return          a list of nearby entities
     */
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

    /**
     * Ticks this entity.
     */
    public abstract void tick();
}
