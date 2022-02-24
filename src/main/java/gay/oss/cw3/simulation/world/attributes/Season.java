package gay.oss.cw3.simulation.world.attributes;

public enum Season {
    EARLY_SPRING,
    MID_SPRING,
    LATE_SPRING,
    
    EARLY_SUMMER,
    MID_SUMMER,
    LATE_SUMMER,

    EARLY_AUTUMN,
    MID_AUTUMN,
    LATE_AUTUMN,

    EARLY_WINTER,
    MID_WINTER,
    LATE_WINTER;
    
    private static final int TICKS_PER_SEASON = 4 * DayCycle.DAY_TICKS;
    private static final Season[] ENTRIES = Season.values();

    public static Season fromTick(int tick) {
        return ENTRIES[(tick / TICKS_PER_SEASON) % ENTRIES.length];
    }
}
