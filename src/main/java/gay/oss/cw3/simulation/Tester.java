package gay.oss.cw3.simulation;

import java.awt.*;
import java.util.Random;

import gay.oss.cw3.provided.Field;
import gay.oss.cw3.provided.SimulatorView;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.FleeBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.entity.Entity;
import org.jetbrains.annotations.Nullable;

public class Tester {
    public static void main(String[] args) {
        World world = new World(128, 128);

        for (int x=0;x<128;x++) {
            for (int z=0;z<128;z++) {
                if (new Random().nextFloat() < 0.05) {
                    new EntityCell(world, new Coordinate(x, z));
                } else if (new Random().nextFloat() < 0.005) {
                    new Hunter(world, new Coordinate(x, z));
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
    }


    static class EntityCell extends AbstractBreedableEntity {
        public EntityCell(World world, Coordinate location) {
            super(world, location, 0, true);
            this.getBrain().addBehaviour(new FleeBehaviour(this, 1.0, 10, Hunter.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 1.0));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
            }
        }

        @Override
        public Entity createChild(Entity otherParent, Coordinate location) {
            var result = new EntityCell(this.getWorld(), location);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }

        @Override
        public boolean isCompatible(Entity entity) {
            return entity.isAlive();
        }
    }

    static class Hunter extends Entity {
        public Hunter(World world, Coordinate location) {
            super(world, location, 0, true);
            this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, EntityCell.class));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
            }
        }
    }
}
