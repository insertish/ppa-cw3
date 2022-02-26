package gay.oss.cw3.scenarios;

import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.Behaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.*;
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
import org.joml.Vector2d;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class DefaultScenario extends Scenario {
    public DefaultScenario(int width, int depth, boolean isOpenGL) throws Exception {
        super(width, depth, isOpenGL);
    }

    @Override
    public void init() throws Exception {
        super.init();

        var generator = this.getGenerator();
        this.registerEntity(EntityLayer.ANIMALS, "Rabbit", Rabbit.class, 0.05f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Hunter", Hunter.class, 0.005f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Herbivore Fish", HerbivoreFish.class, 0.005f, true, false, null);
        this.registerEntity(EntityLayer.FOLIAGE, "Tree", Tree.class, 0.01f, false, true, new BiomeType[] { BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Grass", Grass.class, 0.15f, false, true, new BiomeType[] { BiomeType.Plains, BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Kelp", Kelp.class, 0.15f, true, false, null);
        this.registerEntity(EntityLayer.AERIAL_ANIMALS, "Bird", Bird.class, 0.005f, false, true, null);

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
            renderer.autoLoadModel(Rabbit.class, "ferret.png", "ferret", 0.5f);
            renderer.autoLoadModel(Grass.class, "grass-transparent.png", "grass", 0.5f);
            renderer.autoLoadModel(Tree.class, "pine.png", "pine", 50.0f);
            renderer.autoLoadModel(Kelp.class, "grass-transparent.png", "grass", 0.5f);
            renderer.autoLoadModel(HerbivoreFish.class, "amogus.png", "amogus", 1.5f);
            renderer.autoLoadModel(Bird.class, "bird.png", "bird", 0.5f);
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

    public static class Tree extends Entity {
        public Tree(World world, Coordinate location) {
            super(world, EntityLayer.FOLIAGE, location, 0, true);
            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 10);
            this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 10.0);
            this.setFullness(0.5);
            this.setHealth(this.getMaxHealth());
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                // photosynthesis
                if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    this.addFullness(0.01);
                }

                // spreading
                if (this.getFullness() >= 5.0) {
                    var locations = this.getWorld().findFreeLocationsAboveWater(this.getLayer(), this.getLocation(), 1);

                    if (!locations.isEmpty()) {
                        var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                        new Tree(this.getWorld(), coord);
                        this.removeFullness(2.5);
                    }
                }

            }
        }
    }

    public static class Bird extends AbstractBreedableEntity {
        private Vector2d velocity = new Vector2d();

        public Bird(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.AERIAL_ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
            //this.getBrain().addBehaviour(new SleepBehaviour(this, true));
            //this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, Rabbit.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 0.6));
            this.getBrain().addBehaviour(new PerchInTreeBehaviour(1.0));
            this.getBrain().addBehaviour(new BoidBehaviour(30));

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
            var result = new Bird(this.getWorld(), coordinate);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }

        public Vector2d getVelocity() {
            return velocity;
        }

        private class PerchInTreeBehaviour extends MovementBehaviour {
            private Tree tree = null;

            protected PerchInTreeBehaviour(double speed) {
                super(speed, Bird.this);
            }

            @Override
            public boolean canStart() {
                if (Bird.this.getWorld().getDayCycle() != DayCycle.EVENING && Bird.this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    return false;
                }

                this.tree = null;

                this.findTree();

                return this.tree != null;
            }

            @Override
            public boolean canContinue() {
                if (Bird.this.getWorld().getDayCycle() != DayCycle.EVENING && Bird.this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    return false;
                }

                if (this.tree == null) {
                    this.findTree();
                }

                return this.tree != null;
            }

            @Override
            public void start() {}

            @Override
            public void tick() {
                var dir = this.tree.getLocation().subtract(this.entity.getLocation());
                var newLoc = Bird.this.getLocation().add(this.calculateMovementInDirection(dir));

                var delta = newLoc.subtract(Bird.this.getLocation());
                Bird.this.velocity = new Vector2d(delta.x, delta.z);

                if (Bird.this.canMoveTo(newLoc)) {
                    Bird.this.moveTo(newLoc);
                }
            }

            private void findTree() {
                this.tree = Bird.this.getAdjacentEntities(EntityLayer.FOLIAGE, 40).stream()
                        .filter(Tree.class::isInstance)
                        .map(Tree.class::cast)
                        .findAny()
                        .orElse(null);
            }
        }

        // http://www.kfish.org/boids/pseudocode.html
        private class BoidBehaviour implements Behaviour {
            private final int sightRadius;

            public BoidBehaviour(int sightRadius) {
                this.sightRadius = sightRadius;
            }

            @Override
            public boolean canStart() {
                return true;
            }

            @Override
            public boolean canContinue() {
                return true;
            }

            @Override
            public void start() {
            }

            @Override
            public void tick() {
                final List<Bird> targets = Bird.this.getAdjacentEntities(this.sightRadius).stream()
                        .filter(Bird.class::isInstance)
                        .map(Bird.class::cast)
                        .collect(Collectors.toList());

                final Coordinate flockCentre = targets.stream()
                        .map(Entity::getLocation)
                        .reduce(Coordinate.ORIGIN, Coordinate::add)
                        .multiply(1.0/targets.size());

                final Vector2d movementTowardsFlockCentre = new Vector2d(
                        flockCentre.x - Bird.this.getLocation().x,
                        flockCentre.z - Bird.this.getLocation().z
                ).div(100);

                final Vector2d movementAwayFromOtherBoids = new Vector2d();

                for (Bird target : targets) {
                    if (target.getLocation().sqrDistanceTo(Bird.this.getLocation()) <= 2.4) {
                        var dirAway = target.getLocation().subtract(Bird.this.getLocation());
                        movementAwayFromOtherBoids.add(dirAway.x, dirAway.z);
                    }
                }

                final Vector2d movementTowardsFlockVelocity = targets.stream()
                        .map(Bird::getVelocity)
                        .reduce(new Vector2d(), (a, b) -> new Vector2d(a.x+b.x, a.y+b.y))
                        .div(targets.size())
                        .sub(Bird.this.getVelocity())
                        .div(8);

                final Vector2d movementAwayFromWorldBorder = new Vector2d();
                final double distToLowXBorder = Bird.this.getLocation().x;
                final double distToLowZBorder = Bird.this.getLocation().z;
                final double distToHighXBorder = Bird.this.getLocation().x - Bird.this.getWorld().getMap().getWidth();
                final double distToHighZBorder = Bird.this.getLocation().z - Bird.this.getWorld().getMap().getWidth();

                final double distToXBorder = Math.abs(distToLowXBorder) < Math.abs(distToHighXBorder) ? distToLowXBorder : distToHighXBorder;
                final double distToZBorder = Math.abs(distToLowZBorder) < Math.abs(distToHighZBorder) ? distToLowZBorder : distToHighZBorder;

                if (Math.abs(distToXBorder) <= 5) {
                    movementAwayFromWorldBorder.add(distToXBorder, 0);
                }

                if (Math.abs(distToZBorder) <= 5) {
                    movementAwayFromWorldBorder.add(0, distToZBorder);
                }

                Bird.this.velocity.add(movementTowardsFlockCentre);
                Bird.this.velocity.add(movementAwayFromOtherBoids);
                Bird.this.velocity.add(movementTowardsFlockVelocity);
                Bird.this.velocity.add(movementAwayFromWorldBorder);

                var newLoc = Bird.this.getLocation().add(probabalisticallyRound(Bird.this.velocity.x), probabalisticallyRound(Bird.this.velocity.y));

                if (Bird.this.canMoveTo(newLoc)) {
                    Bird.this.moveTo(newLoc);
                }
            }
        }

        private int probabalisticallyRound(double value) {
            final int floor = (int) Math.floor(value);
            final double fractional = value - Math.floor(value);

            return this.getWorld().getRandom().nextDouble() <= fractional ? floor + 1 : floor;
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
