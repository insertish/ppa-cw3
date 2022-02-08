package gay.oss.cw3.simulation;

public class Coordinate {
    public final int x;
    public final int z;

    public Coordinate() {
        this.x = 0;
        this.z = 0;
    }

    public Coordinate(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public Coordinate add(Coordinate value) {
        return new Coordinate(this.x + value.x, this.z + value.z);
    }

    public Coordinate add(int x, int z) {
        return new Coordinate(this.x + x, this.z + z);
    }

    public String toString() {
        return String.format("Coordinate { x = %d, z = %d }", this.x, this.z);
    }

    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Coordinate)) return false;

        Coordinate c = (Coordinate) obj;
        return c.x == this.x && c.z == this.z;
    }
}
