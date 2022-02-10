package gay.oss.cw3.simulation;

import java.util.List;

import gay.oss.cw3.provided.Field;
import gay.oss.cw3.provided.SimulatorView;

public class Tester {
    public static void main(String[] args) {
        World world = new World(128, 128);

        class EntityCell extends Entity {
            public EntityCell(World world, Coordinate location) {
                super(world, location, 0, true, 1);
            }

            @Override
            public void tick() {
                List<Entity> entities = this.getAdjacentEntities(1);
                if (entities.size() > 1 && Math.random() > 0.8) {
                    this.setAlive(false);
                }
            }
        }
        
        for (int x=0;x<128;x++) {
            for (int z=0;z<128;z++) {
                new EntityCell(world, new Coordinate(x, z));
            }
        }

        int lastIter = 0, iterations = 0;
        /*
        long start = System.currentTimeMillis();
        while (lastIter != world.getEntityCount()) {
            lastIter = world.getEntityCount();
            world.tick();
            iterations++;
        }
        System.out.println("time taken = " + (System.currentTimeMillis() - start) + "ms");
        System.out.println("iterations = " + iterations);*/

        var view = new SimulatorView(128, 128);
        var f = new Field(128, 128);
        while (lastIter != world.getEntityCount()) {
            lastIter = world.getEntityCount();
            world.tick();
            iterations++;

            f.clear();
            
            for (Entity entity : world.getEntities()) {
                Coordinate loc = entity.getLocation();
                f.place("", loc.x, loc.z);
            }

            view.showStatus(iterations, f);
        }

        /*FastNoiseLite noise = new FastNoiseLite();
        noise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);

        float[] data = new float[128 * 128];
        int index = 0;

        var view = new SimulatorView(128, 128);
        var f = new Field(128, 128);

        for (int x=0;x<128;x++) {
            for (int y=0;y<128;y++) {
                data[index++] = noise.GetNoise(x, y);

                if (data[index-1] > 0) {
                    f.place("", x, y);
                }
            }
        }

        view.showStatus(0, f);*/
    }
}
