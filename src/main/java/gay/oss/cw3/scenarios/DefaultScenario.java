package gay.oss.cw3.scenarios;

import org.jetbrains.annotations.Nullable;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.EatFoliageBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.FleeBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.SleepBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class DefaultScenario extends Scenario {
    public DefaultScenario(int width, int depth, boolean isOpenGL) throws Exception {
        super(width, depth, isOpenGL);
    }

    @Override
    public void init() throws Exception {
        super.init();

        this.registerEntity(EntityLayer.ANIMALS, "Rabbit", Rabbit.class, 0.05f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Hunter", Hunter.class, 0.005f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Herbivore Fish", HerbivoreFish.class, 0.005f, true, false, null);
        this.registerEntity(EntityLayer.FOLIAGE, "Grass", Grass.class, 0.15f, false, true, new BiomeType[] { BiomeType.Plains, BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Kelp", Kelp.class, 0.15f, true, false, null);

        if (this.isOpenGL) {
            // Configure models.
            var renderer = this.getRenderer();

            // amogus.png 1.0f
            // bird.png 0.3f
            // ferret.png 0.3f
            // grass.png 0.5f
            // pine.png 10.0f
            // raccoon.png 0.01f
            // snake.png 0.01f

            //renderer.autoLoadModel(Hunter.class, "hunter.jpg");
            renderer.autoLoadModel(Hunter.class, "snake.png", "snake", 0.01f);
            renderer.autoLoadModel(Rabbit.class, "bird.png", "bird", 0.3f);
            renderer.autoLoadModel(Grass.class, "grass-transparent.png", "grass", 0.5f);
            renderer.autoLoadModel(Kelp.class, "grass-transparent.png", "grass", 0.5f);
            renderer.autoLoadModel(HerbivoreFish.class, "amogus.png", "amogus", 1.5f);
            //renderer.autoLoadModel(Grass.class, "pine.png", "pine", 10);
        }
    }

    public static class Rabbit extends AbstractBreedableEntity {
        public Rabbit(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
            this.getBrain().addBehaviour(new FleeBehaviour(this, 1.0, 10, Hunter.class));
            this.getBrain().addBehaviour(new SleepBehaviour(this, false));
            this.getBrain().addBehaviour(new EatFoliageBehaviour(this, 1.0, 0.7, Grass.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 1.0));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.setFullness(this.getMaxFullness());
            this.setHealth(this.getMaxHealth());
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
    }

    public static class Hunter extends AbstractBreedableEntity {
        public Hunter(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
            this.getBrain().addBehaviour(new SleepBehaviour(this, true));
            this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, Rabbit.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 0.6));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness()/2.0);
            this.setFullness(this.getMaxFullness());
            this.setHealth(this.getMaxHealth());
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
        public @Nullable Entity createChild(Entity otherParent, Coordinate coordinate) {
            var result = new Hunter(this.getWorld(), coordinate);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }
    }

    public static class Grass extends Entity {
        public Grass(World world, Coordinate location) {
            super(world, EntityLayer.FOLIAGE, location, 0, true);
            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 0.6);
            this.setFullness(0.5);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                // photosynthesis
                if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    this.addFullness(0.01);
                }

                // spreading
                if (this.getFullness() >= 0.5) {
                    var locations = this.getWorld().findFreeLocationsAboveWater(this.getLayer(), this.getLocation(), 1);

                    if (!locations.isEmpty()) {
                        var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                        new Grass(this.getWorld(), coord);
                        this.removeFullness(0.25);
                    }
                }

            }
        }
    }

    public static class Kelp extends Entity {
        public Kelp(World world, Coordinate location) {
            super(world, EntityLayer.FOLIAGE, location, 0, true);
            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 0.6);
            this.setFullness(0.5);
        }

        @Override
        protected boolean canGoInWater() {
            return true;
        }

        @Override
        protected boolean canGoOnLand() {
            return false;
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                // photosynthesis
                if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    this.addFullness(0.01);
                }

                // spreading
                if (this.getFullness() >= 0.5) {
                    var locations = this.getWorld().findFreeLocationsAboveWater(this.getLayer(), this.getLocation(), 1);

                    if (!locations.isEmpty()) {
                        var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                        new Grass(this.getWorld(), coord);
                        this.removeFullness(0.25);
                    }
                }
            }
        }
    }

    public static class HerbivoreFish extends AbstractBreedableEntity {
        public HerbivoreFish(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
            this.getBrain().addBehaviour(new SleepBehaviour(this, false));
            this.getBrain().addBehaviour(new EatFoliageBehaviour(this, 1.0, 0.7, Kelp.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 30);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.setFullness(this.getMaxFullness());
            this.setHealth(this.getMaxHealth());
        }

        @Override
        protected boolean canGoOnLand() {
            return false;
        }

        @Override
        protected boolean canGoInWater() {
            return true;
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
            var result = new HerbivoreFish(this.getWorld(), location);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }
    }
}
