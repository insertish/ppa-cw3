package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.Behaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.EatFruitFromTreesBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.PerchInTreeBehaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

public class Bird extends AbstractBreedableEntity {
    private Vector2d velocity = new Vector2d();

    public Bird(World world, Coordinate location) {
        super(world, location, 0, true, EntityLayer.AERIAL_ANIMALS, world.getRandom().nextBoolean() ? Sex.FEMALE : Sex.MALE);
        //this.getBrain().addBehaviour(new SleepBehaviour(this, true));
        //this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, Rabbit.class));
        this.getBrain().addBehaviour(new EatFruitFromTreesBehaviour(this, 1.0, 0.7));
        this.getBrain().addBehaviour(new BreedBehaviour<>(this, 0.6));
        this.getBrain().addBehaviour(new PerchInTreeBehaviour(1.0, this));
        this.getBrain().addBehaviour(new BoidBehaviour(30));

        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
        this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
        this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
        this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness() / 2.0);
        this.setFullness(this.getMaxFullness());
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            this.getBrain().tick();
            this.removeFullness(0.005);
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

    @Override
    public void moveTo(Coordinate location) {
        var delta = location.subtract(Bird.this.getLocation());
        this.velocity = new Vector2d(delta.x, delta.z);

        super.moveTo(location);
    }

    private void moveToWithoutModifyingVelocity(Coordinate location) {
        super.moveTo(location);
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
                    .multiply(1.0 / targets.size());

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
                    .reduce(new Vector2d(), (a, b) -> new Vector2d(a.x + b.x, a.y + b.y))
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
                Bird.this.moveToWithoutModifyingVelocity(newLoc);
            }
        }
    }

    private int probabalisticallyRound(double value) {
        final int floor = (int) Math.floor(value);
        final double fractional = value - Math.floor(value);

        return this.getWorld().getRandom().nextDouble() <= fractional ? floor + 1 : floor;
    }
}
