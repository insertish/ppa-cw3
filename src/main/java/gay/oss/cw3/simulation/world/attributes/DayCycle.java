package gay.oss.cw3.simulation.world.attributes;

import static gay.oss.cw3.renderer.Util.intColourToFloats;

public enum DayCycle {
    MORNING(0xffffff),
    AFTERNOON(0xfffcbf),
    EVENING(0xe6b891),
    NIGHT(0x4a5757);

    private final float[] sunColour;

    DayCycle(int sunColour) {
        this.sunColour = intColourToFloats(sunColour);
    }

    public DayCycle next() {
        switch (this) {
            default:
            case MORNING:   return AFTERNOON;
            case AFTERNOON: return EVENING;
            case EVENING:   return NIGHT;
            case NIGHT:     return MORNING;
        }
    }

    public float[] averageToNext(float progression) {
        float[] current = this.sunColour;
        float[] next = this.next().sunColour;

        return new float[] {
            current[0] * (1 - progression) + next[0] * progression,
            current[1] * (1 - progression) + next[1] * progression,
            current[2] * (1 - progression) + next[2] * progression,
        };
    }

    public static final int DAY_TICKS = 100;
    private static final DayCycle[] ENTRIES = DayCycle.values();

    public static DayCycle fromTick(int tick) {
        return ENTRIES[(tick / DAY_TICKS) % ENTRIES.length];
    }
}
