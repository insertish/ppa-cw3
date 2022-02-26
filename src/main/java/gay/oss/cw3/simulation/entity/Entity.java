package gay.oss.cw3.simulation.entity;

import java.util.ArrayList;
import java.util.List;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.brain.EntityBrain;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * An Entity is a basic actor in the world. It has a position in the world, an age, and a health value. It can die,
 * if its health goes to zero. It can run custom logic per-tick.
 */
public abstract class Entity {
    private final World world;
    private final EntityLayer layer;
    private Coordinate location;

    private int ageTicks;
    private boolean alive;

    private int health = 1;
    private double fullness;

    protected final EntityBrain brain = new EntityBrain();
    protected final EntityAttributeMap attributes = new EntityAttributeMap();

    /**
     * Creates <em>and automatically spawns</em> an entity.
     *
     * @param world             the world the entity will reside in
     * @param layer             the entity's layer
     * @param location          the entity's initial location
     * @param initialAgeTicks   the entity's initial age
     * @param alive             whether the entity is alive
     */
    public Entity(World world, EntityLayer layer, Coordinate location, int initialAgeTicks, boolean alive) {
        this.world = world;
        this.layer = layer;
        this.location = location;
        this.ageTicks = initialAgeTicks;
        this.alive = alive;
        this.world.spawn(this);
    }

    /**
     * Creates <em>and automatically spawns</em> an entity at the origin of the world.
     *
     * @param world             the world the entity will reside in
     * @param initialAgeTicks   the entity's initial age
     * @param alive             whether the entity is alive
     * @param layer             the entity's layer
     */
    public Entity(World world, int initialAgeTicks, boolean alive, EntityLayer layer) {
        this(world, layer, Coordinate.ORIGIN, initialAgeTicks, alive);
    }

    /**
     * @return the entity's world
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * @return the entity's layer
     */
    public EntityLayer getLayer() {
        return layer;
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
    public void moveTo(Coordinate location) {
        this.world.moveEntity(this, this.location, location);
        this.location = location;
    }

    /**
     * Sets the entity's location, overwriting other entities that may exist in that space
     *
     * @param location the location
     */
    public void moveToOverwriting(Coordinate location) {
        this.world.moveEntityDisplacing(this, this.location, location);
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
        return (int) Math.round(this.getAttributes().get(EntityAttribute.MAX_HEALTH));
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
     * @return the entity's current fullness
     */
    public double getFullness() {
        return fullness;
    }

    /**
     * @return the entity's maximum fullness, by default equal to its {@link EntityAttribute#MAX_FULLNESS MAX_FULLNESS attribute}.
     */
    public double getMaxFullness() {
        return this.getAttributes().get(EntityAttribute.MAX_FULLNESS);
    }

    /**
     * Sets the entity's fullness, clamped to the range [0, max fullness]
     *
     * @param amount the value to set the fullness to
     */
    public void setFullness(final double amount) {
        this.fullness = Math.max(0, Math.min(this.getMaxFullness(), amount));
    }

    /**
     * Adds the specified amount to the entity's fullness.
     *
     * @param amount the amount to add
     */
    public void addFullness(final double amount) {
        this.setFullness(this.getFullness() + amount);
    }

    /**
     * Removes the specified amount from the entity's fullness.
     *
     * @param amount the amount to remove
     */
    public void removeFullness(final double amount) {
        this.setFullness(this.getFullness() - amount);
    }

    /**
     * @return the entity's brain
     */
    public EntityBrain getBrain() {
        return brain;
    }

    /**
     * @return the entity's attribute map
     */
    public EntityAttributeMap getAttributes() {
        return attributes;
    }

    /**
     * Finds a list of entities within the square of radius given around this entity.
     *
     * @param layer     the layer to look in
     * @param radius    the half-side-length of the square to search around
     *
     * @return          a list of nearby entities
     */
    public List<Entity> getAdjacentEntities(EntityLayer layer, int radius) {
        Coordinate location = this.getLocation();
        int x = location.x;
        int z = location.z;

        List<Entity> entities = new ArrayList<>();
        for (int i=-radius;i<radius+1;i++) {
            for (int j=-radius;j<radius+1;j++) {
                Entity e = this.world.getEntity(layer, x + i, z + j);
                if (e != null && e != this) entities.add(e);
            }
        }

        return entities;
    }

    /**
     * Convenience overload for {@link #getAdjacentEntities(EntityLayer, int)} that checks on the entity's own layer.
     *
     * @param radius the half-side-length of the square to search in
     *
     * @return a list of nearby entities
     */
    public List<Entity> getAdjacentEntities(int radius) {
        return this.getAdjacentEntities(this.getLayer(), radius);
    }

    /**
     * Determines whether the entity could move to a location, assuming nothing else is occupying it.
     *
     * @param location  the location to check
     *
     * @return          whether the entity can occupy that position
     */
    public boolean canMoveToDisregardingOccupancy(final Coordinate location) {
        return this.getWorld().isInBounds(location)
                && (this.getWorld().isAboveWater(location) ? this.canGoOnLand() : this.canGoInWater());
    }

    /**
     * Determines whether the entity can move to a location, considering occupation by other entities.
     *
     * @param location  the location to check
     *
     * @return          whether the entity can occupy that position
     */
    public boolean canMoveTo(final Coordinate location) {
        return this.canMoveToDisregardingOccupancy(location)
                && this.getWorld().getEntity(this.getLayer(), location.x, location.z) == null;
    }

    /**
     * @return whether this entity can occupy locations land
     */
    protected boolean canGoOnLand() {
        return true;
    }

    /**
     * @return whether this entity can occupy locations in water
     */
    protected boolean canGoInWater() {
        return false;
    }

    /**
     * Provides an offset on the y-axis for rendering this entity. This is applied in addition to the
     * {@link EntityLayer#yOffset layer's y-offset}.
     *
     * <p>This is mostly useful for aerial or aquatic entities.</p>
     *
     * @return the rendering y-offset for this entity.
     */
    public float yOffset() {
        return 0f;
    }

    /**
     * Ticks this entity.
     */
    public abstract void tick();
}
