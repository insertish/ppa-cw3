package gay.oss.cw3.simulation.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import gay.oss.cw3.renderer.simulation.particle.Particle;
import gay.oss.cw3.renderer.simulation.particle.ParticleManager;
import gay.oss.cw3.renderer.simulation.particle.ParticleType;
import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import gay.oss.cw3.simulation.world.attributes.Season;

public class World {
    private final Map map;
    private final List<Entity> entities;
    private final List<Entity> entitiesToSpawn;
    private final ParticleManager particleManager;
    private final Random random = new Random();
    private int time = 0;

    public World(int width, int depth) {
        this.map = new Map(width, depth);
        this.map.generate();

        this.entities = Collections.synchronizedList(new ArrayList<>());
        this.entitiesToSpawn = Collections.synchronizedList(new ArrayList<>());
        particleManager = new ParticleManager();
    }

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

    public @Nullable Entity getEntity(EntityLayer layer, int x, int z) {
        return this.map.getEntities(layer).get(x, z);
    }

    public int getEntityCount() {
        return this.entities.size();
    }

    public int getEntityCount(Class<?> clazz) {
        synchronized (this.entities) {
            return (int) this.entities
                .stream()
                .filter(entity -> entity.getClass().isAssignableFrom(clazz))
                .count();
        }
    }

    public Map getMap() {
        return this.map;
    }

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

    public int getTime() {
        return time;
    }

    public DayCycle getDayCycle() {
        return DayCycle.fromTick(this.time);
    }

    public Season getSeason() {
        return Season.fromTick(this.time);
    }

    public boolean isInBounds(Coordinate location) {
        return this.map.isInBounds(location.x, location.z);
    }

    public boolean isAboveWater(Coordinate location) {
        return this.map.getHeight(location.x, location.z) > this.map.getWaterLevel();
    }

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

    public List<Coordinate> findFreeLocationsAboveWater(final EntityLayer layer, final Coordinate around, final int radius) {
        return this.findMatchingLocations(around, radius, coord ->
                this.isInBounds(coord)
                        && this.isAboveWater(coord)
                        && this.getEntity(layer, coord.x, coord.z) == null
        );
    }

    public Random getRandom() {
        return random;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }
}
