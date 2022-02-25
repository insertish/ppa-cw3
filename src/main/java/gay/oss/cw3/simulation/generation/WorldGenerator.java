package gay.oss.cw3.simulation.generation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class WorldGenerator {
    private final World world;
    private final Random random;

    private static class Spawn {
        private final Class<?> entityClass;
        private final float chance;
        private final @Nullable BiomeType[] biome;

        public Spawn(Class<?> clazz, float chance, @Nullable BiomeType[] biome) {
            this.entityClass = clazz;
            this.chance = chance;
            this.biome = biome;
        }
    }

    private final Map<EntityLayer, ArrayList<Spawn>> spawnList = new EnumMap<>(EntityLayer.class);

    public WorldGenerator(World world) {
        this.world = world;
        this.random = new Random();
        this.populateList();
    }

    public WorldGenerator(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
        this.populateList();
    }

    private void populateList() {
        for (EntityLayer layer : EntityLayer.values()) {
            this.spawnList.put(layer, new ArrayList<>());
        }
    }

    public void registerEntity(EntityLayer layer, Class<?> entityClass, float spawnChance, @Nullable BiomeType[] biome) {
        this.spawnList.get(layer).add(new Spawn(entityClass, spawnChance, biome));
    }

    public void generate() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        var map = this.world.getMap();

        for (EntityLayer layer : EntityLayer.values()) {
            for (int x=0;x<map.getWidth();x++) {
                for (int z=0;z<map.getDepth();z++) {
                    float height = map.getHeight(x, z);
                    if (height <= map.getWaterLevel()) continue;

                    ArrayList<Spawn> entries = this.spawnList.get(layer);
                    for (Spawn spawn : entries) {
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
                            Constructor<?> constructor = spawn.entityClass.getConstructor(World.class, Coordinate.class);
                            constructor.newInstance(world, new Coordinate(x, z));
                            break;
                        }
                    }
                }
            }
        }

        world.tick();
    }
}
