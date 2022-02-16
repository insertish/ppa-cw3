package gay.oss.cw3.simulation.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;

public class WorldGenerator {
    private final World world;
    private final Random random;

    private static class Spawn {
        private final Class<?> entityClass;
        private final float chance;

        public Spawn(Class<?> clazz, float chance) {
            this.entityClass = clazz;
            this.chance = chance;
        }
    }

    private final ArrayList<Spawn> spawnList = new ArrayList<>();

    public WorldGenerator(World world) {
        this.world = world;
        this.random = new Random();
    }

    public WorldGenerator(World world, long seed) {
        this.world = world;
        this.random = new Random(seed);
    }

    public void registerEntity(Class<?> entityClass, float spawnChance) {
        this.spawnList.add(new Spawn(entityClass, spawnChance));
    }

    public void generate() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        var map = this.world.getMap();

        for (int x=0;x<map.getWidth();x++) {
            for (int z=0;z<map.getDepth();z++) {
                for (Spawn spawn : this.spawnList) {
                    if (random.nextFloat() < spawn.chance) {
                        Constructor<?> constructor = spawn.entityClass.getConstructor(World.class, Coordinate.class);
                        constructor.newInstance(world, new Coordinate(x, z));
                        break;
                    }
                }
            }
        }

        world.tick();
    }
}
