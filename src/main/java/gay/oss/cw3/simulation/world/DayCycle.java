package gay.oss.cw3.simulation.world;

public enum DayCycle {
    MORNING,
    AFTERNOON,
    EVENING,
    NIGHT;
    
    public static final int DAY_TICKS = 100;
    private static final DayCycle[] ENTRIES = DayCycle.values();

    public static DayCycle fromTick(int tick) {
        return ENTRIES[(tick / DAY_TICKS) % ENTRIES.length];
    }
}
