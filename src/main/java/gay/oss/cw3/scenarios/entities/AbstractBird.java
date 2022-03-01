package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.Sex;
import gay.oss.cw3.simulation.entity.brain.Behaviour;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import org.joml.Vector2d;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A bird-style aerial entity.
 *
 * <p>Birds uniquely keep track of their velocity. This is so that their {@link BoidBehaviour} can run properly.</p>
 */
public abstract class AbstractBird extends AbstractBreedableEntity {
    private Vector2d velocity = new Vector2d();

    /**
     * Creates an AbstractBird entity.
     *
     * @param world             the world the entity will reside in
     * @param layer             the entity's layer
     * @param location          the entity's initial location
     * @param initialAgeTicks   the entity's initial age
     * @param alive             whether the entity is alive
     * @param sex               the sex of this entity
     */
    public AbstractBird(World world, Coordinate location, int initialAgeTicks, boolean alive, EntityLayer layer, Sex sex) {
        super(world, location, initialAgeTicks, alive, layer, sex);
    }

    /**
     * @return the velocity of this bird
     */
    public Vector2d getVelocity() {
        return velocity;
    }

    @Override
    public void moveTo(Coordinate location) {
        var delta = location.subtract(AbstractBird.this.getLocation());
        this.velocity = new Vector2d(delta.x, delta.z);

        super.moveTo(location);
    }

    private void moveToWithoutModifyingVelocity(Coordinate location) {
        super.moveTo(location);
    }

    //FIXME
    @Override
    public float yOffset() {
        var factor = (2.0 - Math.min(this.getVelocity().length(), 2.0)) / 2.0;
        return (float) -(this.getLayer().yOffset * factor);
    }

    private int probabalisticallyRound(double value) {
        final int floor = (int) Math.floor(value);
        final double fractional = value - Math.floor(value);

        return this.getWorld().getRandom().nextDouble() <= fractional ? floor + 1 : floor;
    }

    /**
     * A behaviour that makes an entity move with 'Boid' AI.
     *
     * <p>Boids simulate flocking behaviour with a simple set of rules.</p>
     *
     * <p>The specific rules used here are from <a href="http://www.kfish.org/boids/pseudocode.html">this website</a>.</p>
     */
    //FIXME
    protected class BoidBehaviour implements Behaviour {
        private final int sightRadius;

        /**
         * Create a new BoidBehaviour.
         *
         * @param sightRadius the radius within which this bird can see others
         */
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
            final List<HerbivoreBird> targets = AbstractBird.this.getAdjacentEntities(this.sightRadius).stream()
                    .filter(HerbivoreBird.class::isInstance)
                    .map(HerbivoreBird.class::cast)
                    .collect(Collectors.toList());

            final Coordinate flockCentre = targets.stream()
                    .map(Entity::getLocation)
                    .reduce(Coordinate.ORIGIN, Coordinate::add)
                    .multiply(1.0 / targets.size());

            final Vector2d movementTowardsFlockCentre = new Vector2d(
                    flockCentre.x - AbstractBird.this.getLocation().x,
                    flockCentre.z - AbstractBird.this.getLocation().z
            ).div(100);

            final Vector2d movementAwayFromOtherBoids = new Vector2d();

            for (HerbivoreBird target : targets) {
                if (target.getLocation().sqrDistanceTo(AbstractBird.this.getLocation()) <= 2.4) {
                    var dirAway = target.getLocation().subtract(AbstractBird.this.getLocation());
                    movementAwayFromOtherBoids.add(dirAway.x, dirAway.z);
                }
            }

            final Vector2d movementTowardsFlockVelocity = targets.stream()
                    .map(HerbivoreBird::getVelocity)
                    .reduce(new Vector2d(), (a, b) -> new Vector2d(a.x + b.x, a.y + b.y))
                    .div(targets.size())
                    .sub(AbstractBird.this.getVelocity())
                    .div(8);

            final Vector2d movementAwayFromWorldBorder = new Vector2d();
            final double distToLowXBorder = AbstractBird.this.getLocation().x;
            final double distToLowZBorder = AbstractBird.this.getLocation().z;
            final double distToHighXBorder = AbstractBird.this.getLocation().x - AbstractBird.this.getWorld().getMap().getWidth();
            final double distToHighZBorder = AbstractBird.this.getLocation().z - AbstractBird.this.getWorld().getMap().getWidth();

            final double distToXBorder = Math.abs(distToLowXBorder) < Math.abs(distToHighXBorder) ? distToLowXBorder : distToHighXBorder;
            final double distToZBorder = Math.abs(distToLowZBorder) < Math.abs(distToHighZBorder) ? distToLowZBorder : distToHighZBorder;

            if (Math.abs(distToXBorder) <= 5) {
                movementAwayFromWorldBorder.add(distToXBorder, 0);
            }

            if (Math.abs(distToZBorder) <= 5) {
                movementAwayFromWorldBorder.add(0, distToZBorder);
            }

            AbstractBird.this.velocity.add(movementTowardsFlockCentre);
            AbstractBird.this.velocity.add(movementAwayFromOtherBoids);
            AbstractBird.this.velocity.add(movementTowardsFlockVelocity);
            AbstractBird.this.velocity.add(movementAwayFromWorldBorder);

            var newLoc = AbstractBird.this.getLocation().add(probabalisticallyRound(AbstractBird.this.velocity.x), probabalisticallyRound(AbstractBird.this.velocity.y));

            if (AbstractBird.this.canMoveTo(newLoc)) {
                AbstractBird.this.moveToWithoutModifyingVelocity(newLoc);
            }
        }
    }
}
