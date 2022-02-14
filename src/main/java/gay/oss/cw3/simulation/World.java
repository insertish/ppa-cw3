package gay.oss.cw3.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.DayCycle;

import org.jetbrains.annotations.Nullable;

public class World {
    private final Grid<Entity> map;
    private final List<Entity> entities;
    private final List<Entity> entitiesToSpawn;
    private int time = 0;

    public World(int width, int depth) {
        this.map = new Grid<>(width, depth);
        this.entities = Collections.synchronizedList(new ArrayList<>());
        this.entitiesToSpawn = Collections.synchronizedList(new ArrayList<>());
    }

    public void tick() {
        synchronized (entities) {
            time++;
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

        Entity old_entity = this.map.set(entity.getLocation(), entity);
        if (old_entity != null) old_entity.setAlive(false);
    }

    public @Nullable Entity getEntity(int x, int z) {
        return this.map.get(x, z);
    }

    public int getEntityCount() {
        return this.entities.size();
    }

    public List<Entity> getEntities() {
        return this.entities;
    }

    public int getTime() {
        return time;
    }

    public DayCycle getDayCycle() {
        switch ((this.time / 100) % 4) {
            default:
            case 0: return DayCycle.MORNING;
            case 1: return DayCycle.AFTERNOON;
            case 2: return DayCycle.EVENING;
            case 3: return DayCycle.NIGHT;
        }
    }

    public boolean isInBounds(Coordinate location) {
        return this.map.isInBounds(location.x, location.z);
    }

    public void moveEntity(final Entity entity, final Coordinate from, final Coordinate to) {
        final @Nullable Entity previous;
        if ((previous = this.map.set(from, null)) != entity) {
            throw new IllegalStateException(String.format("Attempted to move %1$s from %2$s to %3$s but it is not at %2$s, instead found %4$s!", entity, from, to, previous));
        }

        this.map.set(to, entity);
    }
}
