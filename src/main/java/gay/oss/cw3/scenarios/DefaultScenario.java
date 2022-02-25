package gay.oss.cw3.scenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;

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

public class DefaultScenario extends Scenario {
    public DefaultScenario(int width, int depth, boolean isOpenGL) throws Exception {
        super(width, depth, isOpenGL);
    }

    @Override
    public void init() throws Exception {
        super.init();

        var generator = this.getGenerator();
        generator.registerEntity(EntityLayer.ANIMALS, Rabbit.class, 0.05f);
        generator.registerEntity(EntityLayer.ANIMALS, Hunter.class, 0.005f);
        generator.registerEntity(EntityLayer.FOLIAGE, Grass.class, 0.3f);

        if (this.isOpenGL) {
            // Configure models.
            var renderer = this.getRenderer();
            //renderer.autoLoadModel(Hunter.class, "hunter.jpg");
            renderer.autoLoadModel(Hunter.class, "amogus.png", "entities/amogus", 1);
            renderer.autoLoadModel(Rabbit.class, "bunny.png", "entities/bunny", 0.02f);
            renderer.autoLoadModel(Grass.class, "grass.png", "entities/grass", 0.5f);
            //renderer.autoLoadModel(Grass.class, "pine.png", "entities/pine", 10);
        }
    }

    public static class Rabbit extends AbstractBreedableEntity {
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

    public static class Hunter extends AbstractBreedableEntity {
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

    public static class Grass extends Entity {
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
