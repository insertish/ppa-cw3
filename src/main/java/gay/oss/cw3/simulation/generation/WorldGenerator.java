package gay.oss.cw3.simulation.generation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import gay.oss.cw3.simulation.entity.Entity;
import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * Helper class for generating new worlds
 */
public class WorldGenerator {
    private final World world;
    private final Random random;

    /**
     * Generator spawn rate entry
     */
    private static class Spawn {
        private final Class<? extends Entity> entityClass;
        private final float chance;
        private final boolean canSpawnOnWater;
        private final boolean canSpawnOnLand;
        private final @Nullable BiomeType[] biome;

        /**
         * Information about spawn rates for a certain entity type
         * @param clazz Class used to instantiate a new Entity
         * @param chance Spawn chance
         * @param canSpawnOnWater Whether the entity can spawn on water
         * @param canSpawnOnLand Whether the entity can spawn on land
         * @param biome Array of biomes the entity is permitted to spawn in
         */
        public Spawn(Class<? extends Entity> clazz, float chance, boolean canSpawnOnWater, boolean canSpawnOnLand, @Nullable BiomeType[] biome) {
            this.entityClass = clazz;
            this.chance = chance;
            this.canSpawnOnWater = canSpawnOnWater;
            this.canSpawnOnLand = canSpawnOnLand;
            this.biome = biome;
        }
    }

    /**
     * Keep track of all spawn rates registered
     */
    private final Map<EntityLayer, ArrayList<Spawn>> spawnList = new EnumMap<>(EntityLayer.class);

    /**
     * Construct a new WorldGenerator for a World
     * @param world World
     */
    public WorldGenerator(World world) {
        this.world = world;
        this.random = new Random();
        this.populateList();
    }

    /**
     * Construct a new WorldGenerator for a World and provide a generation seed
     * @param world World
     * @param seed Seed used for random data
     */
    public WorldGenerator(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
        this.populateList();
    }

    /**
     * Populate the spawn list with known entity layers
     */
    private void populateList() {
        for (EntityLayer layer : EntityLayer.values()) {
            this.spawnList.put(layer, new ArrayList<>());
        }
    }

    /**
     * Register a new Entity to spawn in the World
     * @param layer Layer at which this Entity will spawn
     * @param entityClass The relevant Class used to create new instances of this Entity
     * @param spawnChance Spawn chance
     * @param canSpawnOnWater Whether the entity can spawn on water
     * @param canSpawnOnLand Whether the entity can spawn on land
     * @param biome Array of biomes the entity is permitted to spawn in
     */
    public void registerEntity(EntityLayer layer, Class<? extends Entity> entityClass, float spawnChance, boolean canSpawnOnWater, boolean canSpawnOnLand, @Nullable BiomeType[] biome) {
        this.spawnList.get(layer).add(new Spawn(entityClass, spawnChance, canSpawnOnWater, canSpawnOnLand, biome));
    }

    /**
     * Generate a new World
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void generate() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        var map = this.world.getMap();

        for (EntityLayer layer : EntityLayer.values()) {
            for (int x=0;x<map.getWidth();x++) {
                for (int z=0;z<map.getDepth();z++) {
                    final boolean isInWater = map.getHeight(x, z) <= map.getWaterLevel();

                    ArrayList<Spawn> entries = this.spawnList.get(layer);
                    for (Spawn spawn : entries) {
                        if ((isInWater && !spawn.canSpawnOnWater) || (!isInWater && !spawn.canSpawnOnLand)) {
                            continue;
                        }

                        if (spawn.biome != null) {
                            boolean canSpawn = false;
                            BiomeType target = map.getBiome(x, z);
                            for (BiomeType biome : spawn.biome) {
                                if (target == biome) {
                                    canSpawn = true;
                                    break;
                                }
                            }

                            if (!canSpawn) continue;
                        }

                        if (random.nextFloat() < spawn.chance) {
                            Constructor<? extends Entity> constructor = spawn.entityClass.getConstructor(World.class, Coordinate.class);
                            world.spawn(constructor.newInstance(world, new Coordinate(x, z)));
                            break;
                        }
                    }
                }
            }
        }

        world.tick();
    }
}
