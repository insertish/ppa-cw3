package gay.oss.cw3.simulation;

import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
    private final World world;
    private Coordinate location;

    private int ageTicks;
    private boolean alive;

    private int health;
    private final int maxHealth;

    public Entity(World world, Coordinate location, int initialAgeTicks, boolean alive, int maxHealth) {
        this.world = world;
        this.location = location;
        this.ageTicks = initialAgeTicks;
        this.alive = alive;
        this.maxHealth = maxHealth;
        this.world.spawn(this);
    }

    public Entity(World world, int initialAgeTicks, boolean alive, int maxHealth) {
        this(world, Coordinate.ORIGIN, initialAgeTicks, alive, maxHealth);
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
