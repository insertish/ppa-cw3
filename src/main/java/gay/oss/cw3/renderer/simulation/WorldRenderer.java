package gay.oss.cw3.renderer.simulation;

import java.util.HashMap;

import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.simulation.World;

public class WorldRenderer {
    private final World world;
    private final HashMap<Class<?>, Model> models;

    public WorldRenderer(World world) {
        this.world = world;
        this.models = new HashMap<>();
    }

    public void setModel(Class<?> clazz, Model model) {
        this.models.put(clazz, model);
    }
}
