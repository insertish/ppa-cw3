package gay.oss.cw3.simulation.entity.brain.behaviours;

import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Breedable;
import gay.oss.cw3.simulation.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A behaviour that makes entities mate with other entities and have children.
 *
 * @param <T> the type of the entity this behaviour is attached to
 */
public class BreedBehaviour<T extends Entity & Breedable> extends MovementBehaviour {
    private final Random random = new Random();
    private final Predicate<Coordinate> birthPositionPredicate;
    private final int viewDistance;
    private final T entity;

    private T target;
    private int ticksCouldntMove;

    /**
     * Create a new BreedBehaviour.
     *  @param entity                    the entity
     * @param speed                     the movement speed modifier
     * @param birthPositionPredicate    a predicate for valid positions to spawn the child
     * @param viewDistance
     */
    public BreedBehaviour(T entity, double speed, Predicate<Coordinate> birthPositionPredicate, int viewDistance) {
        super(speed, entity);
        this.entity = entity;
        this.birthPositionPredicate = birthPositionPredicate;
        this.viewDistance = viewDistance;
    }

    /**
     * Create a new BreedBehaviour.
     *  @param entity    the entity
     * @param speed     the movement speed modifier
     * @param viewDistance
     */
    public BreedBehaviour(T entity, double speed, int viewDistance) {
        this(entity, speed, ((Entity)entity)::canMoveTo, viewDistance);
    }

    @SuppressWarnings("unchecked") // `isInstance` check means that (T) cast is safe
    @Override
    public boolean canStart() {
        if (!this.entity.canBreed() || !this.entity.canGiveBirth()) {
            return false;
        }

        this.target = null;
        this.ticksCouldntMove = 0;

        var potentialTargets = this.entity.getAdjacentEntities(this.viewDistance).stream()
                .filter(entity.getClass()::isInstance)
                .map(entity.getClass()::cast)
                .collect(Collectors.toList());

        if (potentialTargets.isEmpty()) {
            return false;
        }

        var potentialTarget = potentialTargets.get(random.nextInt(potentialTargets.size()));
        if (
                potentialTarget.isAlive()
                        && this.entity.isCompatible(potentialTarget)
                        && ((T) potentialTarget).canBreed()
                        && ((T) potentialTarget).isCompatible(this.entity)
        ) {
            this.target = (T) potentialTarget;
        }

        return this.target != null;
    }

    @Override
    public boolean canContinue() {
        return this.ticksCouldntMove < 3
                && this.entity.canBreed()
                && this.target.isAlive()
                && this.target.canBreed()
                && this.entity.isCompatible(this.target)
                && this.target.isCompatible(entity);
    }

    @Override
    public void start() {
    }

    @Override
    public void tick() {
        if (this.entity.getLocation().distanceTo(this.target.getLocation()) < 2) {
            this.breed();
            return;
        }

        var dir = this.target.getLocation().subtract(this.entity.getLocation());
        var newLoc = this.entity.getLocation().add(this.calculateMovementInDirection(dir));

        if (this.entity.canMoveTo(newLoc)) {
            var entityAtLoc = this.entity.getWorld().getEntity(entity.getLayer(), newLoc.x, newLoc.z);

            if (entityAtLoc == null) {
                this.ticksCouldntMove = 0;
                this.entity.moveTo(newLoc);
            } else if (entityAtLoc == this.target) {
                this.breed();
            } else {
                this.ticksCouldntMove++;
            }
        } else {
            this.ticksCouldntMove++;
        }
    }

    private void breed() {
        var babyPos = this.findAvailablePositionAround(2, this.entity, this.target);
        if (babyPos == null) {
            return;
        }

        var baby = this.entity.createChild(this.target, babyPos);
        if (baby == null) {
            return;
        } else {
            this.entity.getWorld().spawn(baby);
        }

        this.entity.startBreedingAttempt();
        this.target.startBreedingAttempt();
        this.ticksCouldntMove = 0;
    }

    private @Nullable Coordinate findAvailablePositionAround(int radius, Entity a, Entity b) {
        final Set<Coordinate> positions = new HashSet<>();
        positions.addAll(a.getWorld().findMatchingLocations(a.getLocation(), radius, birthPositionPredicate));
        positions.addAll(b.getWorld().findMatchingLocations(b.getLocation(), radius, birthPositionPredicate));

        if (positions.isEmpty()) {
            return null;
        }

        return new ArrayList<>(positions).get(this.random.nextInt(positions.size()));
    }
}
