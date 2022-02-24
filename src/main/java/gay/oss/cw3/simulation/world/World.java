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
                }
            }
        }

        synchronized (entitiesToSpawn) {
            this.entitiesToSpawn.forEach(this::spawnInternal);
            this.entitiesToSpawn.clear();
        }
    }

    public void spawn(Entity entity) {
        this.entitiesToSpawn.add(entity);
    }

    private void spawnInternal(Entity entity) {
        this.entities.add(entity);

        Entity old_entity = this.map.getEntities(entity.getLayer()).set(entity.getLocation(), entity);
        if (old_entity != null) old_entity.setAlive(false);
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

    public void moveEntity(final Entity entity, final Coordinate from, final Coordinate to) {
        final @Nullable Entity previous;
        if ((previous = this.map.getEntities(entity.getLayer()).set(from, null)) != entity) {
            throw new IllegalStateException(String.format("Attempted to move %1$s from %2$s to %3$s but it is not at %2$s, instead found %4$s!", entity, from, to, previous));
        }

        this.map.getEntities(entity.getLayer()).set(to, entity);
    }
}