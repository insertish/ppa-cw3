package gay.oss.cw3.simulation.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import gay.oss.cw3.renderer.simulation.particle.Particle;
import gay.oss.cw3.renderer.simulation.particle.ParticleManager;
import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * The world handles the simulation.
 *
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class World {
    private final Map map;
    private final List<Entity> entities;
    private final List<Entity> entitiesToSpawn;
    private final ParticleManager particleManager;
    private final Random random = new Random();
    private int time = 0;

    /**
     * Creates a new world.
     *
     * @param width     the width of the world map
     * @param depth     the depth of the world map
     */
    public World(int width, int depth) {
        this.map = new Map(width, depth);
        this.map.generate();

        this.entities = Collections.synchronizedList(new ArrayList<>());
        this.entitiesToSpawn = Collections.synchronizedList(new ArrayList<>());
        particleManager = new ParticleManager();
    }

    /**
     * Ticks the world. This ticks the entire simulation forward.
     */
    public void tick() {
        time++;

        synchronized (entities) {
            Iterator<Entity> iter = entities.iterator();
            while (iter.hasNext()) {
                Entity entity = iter.next();
                entity.incrementAge();
                entity.tick();

                if (!entity.isAlive()) {
                    iter.remove();
                    this.despawn(entity);
                }
            }
        }

        this.entities.addAll(this.entitiesToSpawn);
        this.entitiesToSpawn.clear();

        this.particleManager.tick();
    }

    /**
     * Spawn a new entity into the world.
     *
     * @param entity the entity
     */
    public void spawn(Entity entity) {
        this.map.getEntities(entity.getLayer()).setWithoutOverwrite(entity.getLocation(), entity);
        this.entitiesToSpawn.add(entity);
    }

    private void despawn(Entity entity) {
        if (this.map.getEntities(entity.getLayer()).get(entity.getLocation()) == entity) {
            this.map.getEntities(entity.getLayer()).set(entity.getLocation(), null);
        }

        entity.setAlive(false);

        var deathParticle = entity.deathParticleType();

        if (deathParticle != null) {
            this.getParticleManager().addParticle(
                    new Particle(
                            deathParticle,
                            entity.getLocation(),
                            entity.yOffset() + this.getMap().getHeight(entity.getLocation().x, entity.getLocation().z),
                            (float) this.getRandom().nextGaussian() * 0.2f,
                            this.getRandom().nextFloat() * 2f,
                            (float) this.getRandom().nextGaussian() * 0.2f
                    )
            );
        }
    }

    /**
     * Get the entity at a certain position and layer in the world.
     *
     * @param layer the layer
     * @param x     the x position of the entity
     * @param z     the z position of the entity
     * @return      the entity, or null if there is no entity there
     */
    public @Nullable Entity getEntity(EntityLayer layer, int x, int z) {
        return this.map.getEntities(layer).get(x, z);
    }

    /**
     * @return the number of entities in the world
     */
    public int getEntityCount() {
        return this.entities.size();
    }

    /**
     * Gets the number of entities of a certain class in the world.
     *
     * @param clazz the class
     * @return      the number of entities of that class
     */
    public int getEntityCount(Class<?> clazz) {
        synchronized (this.entities) {
            return (int) this.entities
                .stream()
                .filter(entity -> entity.getClass().isAssignableFrom(clazz))
                .count();
        }
    }

    /**
     * @return the {@link Map} of this world
     */
    public Map getMap() {
        return this.map;
    }

    /**
     * @return the list of entities in this world
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Returns a list of entities within a square of the given radius around a coordinate, except an entity.
     *
     * @param layer     the layer to look in
     * @param except    the entity to exclude (can be null)
     * @param around    the coordinate to centre the search around
     * @param radius    the half-side-length of the square to search around
     *
     * @return          a list of nearby entities
     */
    public List<Entity> getEntitiesAround(EntityLayer layer, @Nullable Entity except, Coordinate around, int radius) {
        return this.map.getEntities(layer).getInRadius(except, around, radius);
    }

    /**
     * @return the number of ticks that have elapsed in the simulation
     */
    public int getTime() {
        return time;
    }

    /**
     * @return the current {@link DayCycle} value of the world
     */
    public DayCycle getDayCycle() {
        return DayCycle.fromTick(this.time);
    }

    /**
     * Determines if a coordinate is within bounds for the world.
     *
     * @param location  the coordinate
     *
     * @return          whether the coordinate is in bounds
     *
     * @see Map#isInBounds(int, int)
     */
    public boolean isInBounds(Coordinate location) {
        return this.map.isInBounds(location.x, location.z);
    }

    /**
     * Determines if a coordinate is above the water level of this world
     *
     * @param location  the coordinate
     *
     * @return          whether the coordinate is above the water level
     *
     * @see Map#getWaterLevel()
     */
    public boolean isAboveWater(Coordinate location) {
        return this.map.getHeight(location.x, location.z) > this.map.getWaterLevel();
    }

    /**
     * Moves an entity to a new position, killing whatever is in the new position.
     *
     * @param entity    the entity to move
     * @param from      the entity's old position
     * @param to        the position to move the entity to
     *
     * @see #moveEntity(Entity, Coordinate, Coordinate)
     */
    public void moveEntityDisplacing(final Entity entity, final Coordinate from, final Coordinate to) {
        if (from.equals(to)) {
            return;
        }

        final @Nullable Entity previous = this.map.getEntities(entity.getLayer()).set(from, null);
        if (previous != entity) {
            throw new IllegalStateException(String.format("Attempted to move %1$s from %2$s to %3$s but it is not at %2$s, instead found %4$s!", entity, from, to, previous));
        }

        final @Nullable Entity displaced = this.map.getEntities(entity.getLayer()).set(to, entity);
        if (displaced != null) {
            displaced.setAlive(false);
        }
    }

    /**
     * Moves an entity to a new position, throwing {@link IllegalStateException} if something already occupies the space.
     *
     * @param entity    the entity to move
     * @param from      the entity's old position
     * @param to        the position to move the entity to
     *
     * @see #moveEntityDisplacing(Entity, Coordinate, Coordinate)
     */
    public void moveEntity(final Entity entity, final Coordinate from, final Coordinate to) {
        if (from.equals(to)) {
            return;
        }

        final @Nullable Entity previous = this.map.getEntities(entity.getLayer()).set(from, null);
        if (previous != entity) {
            throw new IllegalStateException(String.format("Attempted to move %1$s from %2$s to %3$s but it is not at %2$s, instead found %4$s!", entity, from, to, previous));
        }

        this.map.getEntities(entity.getLayer()).setWithoutOverwrite(to, entity);
    }

    /**
     * Finds positions around a given position that match a certain predicate..
     *
     * @param around    the position to search around
     * @param radius    the radius to search in
     * @param predicate the predicate
     *
     * @return          a list of positions which match
     */
    public List<Coordinate> findMatchingLocations(final Coordinate around, final int radius, final Predicate<Coordinate> predicate) {
        final List<Coordinate> locations = new ArrayList<>();

        for (int dX = -radius; dX <= radius; dX++) {
            for (int dZ = -radius; dZ <= radius; dZ++) {
                var coord = around.add(dX, dZ);

                if (predicate.test(coord)) {
                    locations.add(coord);
                }
            }
        }

        return locations;
    }

    /**
     * Finds positions around a given position above water with no entities in a given layer.
     *
     * @param layer     the layer to check in
     * @param around    the position to search around
     * @param radius    the radius to search in
     *
     * @return          a list of positions which match
     *
     * @see #findMatchingLocations(Coordinate, int, Predicate)
     */
    public List<Coordinate> findFreeLocationsAboveWater(final EntityLayer layer, final Coordinate around, final int radius) {
        return this.findMatchingLocations(around, radius, coord ->
                this.isInBounds(coord)
                        && this.isAboveWater(coord)
                        && this.getEntity(layer, coord.x, coord.z) == null
        );
    }

    /**
     * @return the random instance
     */
    public Random getRandom() {
        return random;
    }

    /**
     * @return the world's particle manager
     */
    public ParticleManager getParticleManager() {
        return particleManager;
    }
}
