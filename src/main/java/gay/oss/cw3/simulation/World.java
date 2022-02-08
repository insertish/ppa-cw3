package gay.oss.cw3.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class World {
    private Grid<Entity> map;
    private List<Entity> entities;

    public World(int width, int depth) {
        this.map = new Grid<>(width, depth);
        this.entities = Collections.synchronizedList(new ArrayList<>());
    }

    public void tick() {
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
    }

    public void spawn(Entity entity) {
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
}
