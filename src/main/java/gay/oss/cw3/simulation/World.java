package gay.oss.cw3.simulation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class World {
    private List<Entity> entities;

    public World() {
        this.entities = Collections.synchronizedList(new ArrayList<>());
    }

    public void tick() {
        synchronized (entities) {
            for (Entity entity : entities) {
                entity.incrementAge();
                entity.tick();
            }
        }
    }

    public void spawn(Entity entity) {
        this.entities.add(entity);
    }
}
