package gay.oss.cw3.simulation.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
    private int time = 0;

    public World(int width, int depth) {
        this.map = new Map(width, depth);
        this.map.generate();

        this.entities = Collections.synchronizedList(new ArrayList<>());
        this.entitiesToSpawn = Collections.synchronizedList(new ArrayList<>());
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
    }

    public @Nullable Entity getEntity(EntityLayer layer, int x, int z) {
        return this.map.getEntities(layer).get(x, z);
    }

    public int getEntityCount() {
        return this.entities.size();
    }

    public Map getMap() {
        return this.map;
    }

    public List<Entity> getEntities() {
        return this.entities;
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

    public List<Coordinate> findFreeLocations(final EntityLayer layer, final Coordinate around, final int radius) {
        final List<Coordinate> locations = new ArrayList<>();

        for (int dX = -radius; dX <= radius; dX++) {
            for (int dZ = -radius; dZ <= radius; dZ++) {
                var coord = around.add(dX, dZ);

                if (
                        this.isInBounds(coord)
                                && this.getEntity(layer, coord.x, coord.z) == null
                                && this.map.getHeight(coord.x, coord.z) > this.map.getWaterLevel()
                ) {
                    locations.add(coord);
                }
            }
        }

        return locations;
    }
}
