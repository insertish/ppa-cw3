package gay.oss.cw3.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.renderer.simulation.WorldRenderer;
import gay.oss.cw3.simulation.generation.WorldGenerator;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public abstract class Scenario {
    private int width;
    private int depth;

    private World world;
    private WorldGenerator generator;

    protected boolean isOpenGL;
    private WorldRenderer renderer;

    private Map<Class<?>, String> entityNames;

    public Scenario(int width, int depth, boolean isOpenGL) throws Exception {
        this.width = width;
        this.depth = depth;
        this.isOpenGL = isOpenGL;
        this.entityNames = new HashMap<>();

        this.init();
    }

    protected void registerEntity(EntityLayer layer, String name, Class<?> entityClass, float spawnChance, boolean canSpawnOnWater, boolean canSpawnOnLand, @Nullable BiomeType[] biome) {
        this.entityNames.put(entityClass, name);
        this.generator.registerEntity(layer, entityClass, spawnChance, canSpawnOnWater, canSpawnOnLand, biome);
    }

    public String getEntityName(Class<?> entityClass) {
        return this.entityNames.get(entityClass);
    }

    public void init() throws Exception {
        this.world = new World(this.width, this.depth);
        this.generator = new WorldGenerator(world);

        if (isOpenGL) {
            this.renderer = new WorldRenderer(world);
        }
    }

    public void generate() throws Exception {
        this.generator.generate();

        if (this.isOpenGL) {
            this.renderer.init();
        }
    }

    public World getWorld() {
        return this.world;
    }

    protected WorldGenerator getGenerator() {
        return this.generator;
    }

    public WorldRenderer getRenderer() {
        return this.renderer;
    }

    public Set<Class<?>> getEntities() {
        return this.entityNames.keySet();
    }
}
