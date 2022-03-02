package gay.oss.cw3.simulation;

import java.util.Objects;

/**
 * Encodes a position on the 2D plane as cartesian coordinates.
 *
 * <p>Note that this uses {@link #x} and {@link #z}, rather than {@code x} and {@code y}. {@code y} is considered the
 * 'up' direction in the simulation, and is not used in coordinates.</p>
 *
 * <p>A coordinate can also be thought of as a two-dimensional vector, and many of this class's methods reflect that.</p>
 *
 * <p>Note that coordinates are <em>immutable</em>, and all their 'modifying' methods return new instances.</p>
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Coordinate {
    /**
     * The coordinate of the origin, {@code (0, 0)}.
     */
    public static final Coordinate ORIGIN = new Coordinate(0, 0);
    public final int x;
    public final int z;

    /**
     * Creates a new coordinate with the given x and z values.
     *
     * @param x the x value
     * @param z the z value
     */
    public Coordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    /**
     * Adds another coordinate to this one.
     *
     * @param value the other coordinate to add
     *
     * @return      the result of the addition
     */
    public Coordinate add(Coordinate value) {
        return new Coordinate(this.x + value.x, this.z + value.z);
    }

    /**
     * Adds another coordinate to this one, with the other given as int values.
     *
     * @param x the x coordinate to add
     * @param z the z coordinate to add
     *
     * @return  the result of the addition
     *
     * @see #add(Coordinate)
     */
    public Coordinate add(int x, int z) {
        return new Coordinate(this.x + x, this.z + z);
    }

    /**
     * Multiplies (or scales) this coordinate by a scalar.
     *
     * @param amount    the amount to multiply by
     *
     * @return          the result of the multiplication
     */
    public Coordinate multiply(double amount) {
        return new Coordinate((int) Math.round(this.x*amount), (int) Math.round(this.z*amount));
    }

    /**
     * Subtracts another coordinate from this one.
     *
     * @param value the other coordinate to subtract
     *
     * @return      the result of the subtraction
     */
    public Coordinate subtract(Coordinate value) {
        return new Coordinate(this.x-value.x, this.z-value.z);
    }

    /**
     * Subtracts another coordinate to this one, with the other given as int values.
     *
     * @param x the x coordinate to subtract
     * @param z the z coordinate to subtract
     *
     * @return  the result of the subtraction
     *
     * @see #subtract(Coordinate)
     */
    public Coordinate subtract(int x, int z) {
        return new Coordinate(this.x - x, this.z - z);
    }

    /**
     * Calculates the squared Euclidean distance from this coordinate to the origin. If the coordinate is interpreted as
     * a vector, this is the squared length of that vector.
     *
     * <p>Using this may be preferable over {@link #distanceToOrigin()} in performance-critical code.</p>
     *
     * @return the squared Euclidean distance to the origin
     */
    public int sqrDistanceToOrigin() {
        return this.x * this.x + this.z * this.z;
    }

    /**
     * Calculates the Euclidean distance from this coordinate to the origin. If the coordinate is interpreted as
     * a vector, this is the squared length of that vector.
     *
     * @return the Euclidean distance to the origin
     *
     * @see #sqrDistanceToOrigin()
     */
    public double distanceToOrigin() {
        return Math.sqrt(this.sqrDistanceToOrigin());
    }

    /**
     * Calculates the squared Euclidean distance between this coordinate and another.
     *
     * <p>Using this may be preferable over {@link #distanceTo(Coordinate)} in performance-critical code.</p>
     *
     * @param value the other coordinate
     *
     * @return      the squared distance to the other coordinate
     *
     * @see #sqrDistanceToOrigin()
     */
    public double sqrDistanceTo(Coordinate value) {
        return this.subtract(value).distanceToOrigin();
    }

    /**
     * Calculates the Euclidean distance between this coordinate and another.
     *
     * @param value the other coordinate
     *
     * @return      the distance to the other coordinate
     *
     * @see #distanceToOrigin()
     */
    public double distanceTo(Coordinate value) {
        return this.subtract(value).distanceToOrigin();
    }

    /**
     * Calculate this coordinate normalised (length 1, or as close to as the int precision allows)
     *
     * @return this coordinate, normalised
     */
    public Coordinate normalised() {
        return this.multiply(1.0/(this.distanceToOrigin()));
    }

    public String toString() {
        return String.format("Coordinate { x = %d, z = %d }", this.x, this.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinate that = (Coordinate) o;
        return x == that.x && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }
}
