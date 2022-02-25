package gay.oss.cw3.simulation.world.attributes;

public enum DayCycle {
    MORNING(new float[] { 1.0f, 1.0f, 1.0f }),
    AFTERNOON(new float[] { 1.0f, 0.99f, 0.75f }),
    EVENING(new float[] { 0.9f, 0.72f, 0.57f }),
    NIGHT(new float[] { 0.29f, 0.34f, 0.34f });

    private float[] colour;
    
    private DayCycle(float[] colour) {
        this.colour = colour;
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
        float[] current = this.colour;
        float[] next = this.next().colour;

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
