package gay.oss.cw3.simulation;

import java.awt.*;
import java.util.List;
import java.util.Random;

import gay.oss.cw3.provided.Field;
import gay.oss.cw3.provided.SimulatorView;
import gay.oss.cw3.simulation.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.brain.behaviours.WanderAroundBehaviour;

public class Tester {
    public static void main(String[] args) {
        World world = new World(128, 128);

        class EntityCell extends Entity {
            public EntityCell(World world, Coordinate location) {
                super(world, location, 0, true, 1);
            }

            @Override
            public void tick() {
                this.getBrain().tick();
            }
        }

        class Hunter extends Entity {
            public Hunter(World world, Coordinate location) {
                super(world, location, 0, true);
            }

            @Override
            public void tick() {
                this.getBrain().tick();
            }
        }

        for (int x=0;x<128;x++) {
            for (int z=0;z<128;z++) {
                if (new Random().nextFloat() < 0.05) {
                    var e = new EntityCell(world, new Coordinate(x, z));
                    e.getBrain().addBehaviour(new WanderAroundBehaviour(e));
                }

                if (new Random().nextFloat() < 0.005) {
                    var e = new Hunter(world, new Coordinate(x, z));
                    e.getBrain().addBehaviour(new HuntBehaviour(e, EntityCell.class));
                    e.getBrain().addBehaviour(new WanderAroundBehaviour(e));
                }
            }
        }

        int lastIter;
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
        view.setColor(Hunter.class, Color.ORANGE);
        view.setColor(EntityCell.class, Color.LIGHT_GRAY);
        while (true) {
            lastIter = world.getTime();
            world.tick();

            f.clear();

            for (Entity entity : world.getEntities()) {
                Coordinate loc = entity.getLocation();
                f.place(entity, loc.x, loc.z);
            }

            view.showStatus(lastIter, f);
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
