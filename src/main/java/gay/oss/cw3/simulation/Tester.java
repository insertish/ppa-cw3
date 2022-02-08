package gay.oss.cw3.simulation;

public class Tester {
    public static void main(String[] args) {
        String arguments[] = args;

        World world = new World();
        
        for (int i=0;i<9_600;i++) {
            Entity something = new Entity(world, 0, false) {
                @Override
                public void tick() {
                    this.setLocation(this.getLocation().add(new Coordinate(1, 2)));
                    // System.out.println("Entity location is " + this.getLocation());
                }
            };

            world.spawn(something);
        }

        long start = System.currentTimeMillis();
        for (int i=0;i<200;i++) world.tick();
        System.out.println("time taken = " + (System.currentTimeMillis() - start));
    }
}
