package gay.oss.cw3.tests;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.provided.Field;
import gay.oss.cw3.provided.SimulatorView;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.EatFoliageBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.FleeBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class TestBrains {
    public static void main(String[] args) {
        World world = new World(128, 128);

        for (int x=0;x<128;x++) {
            for (int z=0;z<128;z++) {
                if (new Random().nextFloat() < 0.05) {
                    new Rabbit(world, new Coordinate(x, z));
                } else if (new Random().nextFloat() < 0.005) {
                    new Hunter(world, new Coordinate(x, z));
                }

                if (new Random().nextFloat() < 0.3) {
                    new Grass(world, new Coordinate(x, z));
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
        view.setColor(Rabbit.class, Color.LIGHT_GRAY);
        view.setColor(Grass.class, new Color(0x00dd00));
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


    static class Rabbit extends AbstractBreedableEntity {
        public Rabbit(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.ANIMALS);
            this.getBrain().addBehaviour(new FleeBehaviour(this, 1.0, 10, Hunter.class));
            this.getBrain().addBehaviour(new EatFoliageBehaviour(this, 1.0, 0.7, Grass.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 1.0));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.setFullness(this.getMaxFullness());
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
                this.removeFullness(0.01);
                if (this.getFullness() <= 0) {
                    this.addHealth(-1);
                }
            }
        }

        @Override
        public Entity createChild(Entity otherParent, Coordinate location) {
            var result = new Rabbit(this.getWorld(), location);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }

        @Override
        public boolean isCompatible(Entity entity) {
            return entity.isAlive();
        }
    }

    static class Hunter extends AbstractBreedableEntity {
        public Hunter(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.ANIMALS);
            this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, Rabbit.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 0.6));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness()/2.0);
            this.setFullness(this.getMaxFullness());
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
                this.removeFullness(0.05);
                if (this.getFullness() <= 0) {
                    this.addHealth(-1);
                }
            }
        }

        @Override
        public @Nullable Entity createChild(Entity otherParent, Coordinate coordinate) {
            var result = new Hunter(this.getWorld(), coordinate);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }

        @Override
        public boolean isCompatible(Entity entity) {
            return true;
        }
    }

    static class Grass extends Entity {
        private final Random random = new Random();

        public Grass(World world, Coordinate location) {
            super(world, EntityLayer.FOLIAGE, location, 0, true);
            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 3.0);
            this.setFullness(1.0);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                // photosynthesis
                if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    this.addFullness(0.2);
                }

                // spreading
                if (this.getFullness() >= 2.0) {
                    List<Coordinate> locations = new ArrayList<>();

                    for (int dX = -1; dX <= 1; dX++) {
                        for (int dZ = -1; dZ <= 1; dZ++) {
                            var coord = this.getLocation().add(dX, dZ);

                            if (this.getWorld().isInBounds(coord) && this.getWorld().getEntity(EntityLayer.FOLIAGE, coord.x, coord.z) != null) {
                                locations.add(coord);
                            }
                        }
                    }

                    if (!locations.isEmpty()) {
                        var coord = locations.get(random.nextInt(locations.size()));
                        new Grass(this.getWorld(), coord);
                        this.removeFullness(1.0);
                    }
                }

            }
        }
    }
}
