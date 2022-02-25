package gay.oss.cw3.simulation.world.attributes;

import gay.oss.cw3.renderer.Util;

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

    public float[] getSunColour(float progressionToNext) {
        float[] current = Util.rgbToOklab(this.sunColour);
        float[] next = Util.rgbToOklab(this.next().sunColour);

        return Util.oklabToRgb(new float[] {
            current[0] * (1 - progressionToNext) + next[0] * progressionToNext,
            current[1] * (1 - progressionToNext) + next[1] * progressionToNext,
            current[2] * (1 - progressionToNext) + next[2] * progressionToNext,
        });
    }

    public static final int DAY_TICKS = 100;
    private static final DayCycle[] ENTRIES = DayCycle.values();

    public static DayCycle fromTick(int tick) {
        return ENTRIES[(tick / DAY_TICKS) % ENTRIES.length];
    }
}
