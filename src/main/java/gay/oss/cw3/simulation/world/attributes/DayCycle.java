package gay.oss.cw3.simulation.world.attributes;

import gay.oss.cw3.renderer.Util;

import static gay.oss.cw3.renderer.Util.intColourToFloats;

/**
 * Enumeration of day-night cycle.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public enum DayCycle {
    MORNING(0xffffff, 0x9bb8c5),
    AFTERNOON(0xfffcbf, 0xffffff),
    EVENING(0xe6b891, 0x455970),
    NIGHT(0x4a5757, 0x1d1d42);

    private final float[] sunColour;
    private final float[] skyColour;

    /**
     * Construct a new DayCycle
     * @param sunColour Colour used for light from sun
     * @param skyColour Colour used for rendering the skybox
     */
    DayCycle(int sunColour, int skyColour) {
        this.sunColour = intColourToFloats(sunColour);
        this.skyColour = intColourToFloats(skyColour);
    }

    /**
     * Get the next value from current value.
     * @return DayCycle
     */
    public DayCycle next() {
        switch (this) {
            default:
            case MORNING:   return AFTERNOON;
            case AFTERNOON: return EVENING;
            case EVENING:   return NIGHT;
            case NIGHT:     return MORNING;
        }
    }

    /**
     * Get the averaged colour of the sun between the current and next cycle
     * @param progressionToNext Fractional progression the next stage
     * @return RGB colour array
     */
    public float[] getSunColour(float progressionToNext) {
        float[] current = Util.rgbToOklab(this.sunColour);
        float[] next = Util.rgbToOklab(this.next().sunColour);

        return Util.oklabToRgb(new float[] {
            current[0] * (1 - progressionToNext) + next[0] * progressionToNext,
            current[1] * (1 - progressionToNext) + next[1] * progressionToNext,
            current[2] * (1 - progressionToNext) + next[2] * progressionToNext,
        });
    }

    /**
     * Get the averaged colour of the sky between the current and next cycle
     * @param progressionToNext Fractional progression the next stage
     * @return RGB colour array
     */
    public float[] getSkyColour(float progressionToNext) {
        float[] current = Util.rgbToOklab(this.skyColour);
        float[] next = Util.rgbToOklab(this.next().skyColour);

        return Util.oklabToRgb(new float[] {
                        current[0] * (1 - progressionToNext) + next[0] * progressionToNext,
                        current[1] * (1 - progressionToNext) + next[1] * progressionToNext,
                        current[2] * (1 - progressionToNext) + next[2] * progressionToNext,
                }
        );
    }

    public static final int DAY_TICKS = 100;
    private static final DayCycle[] ENTRIES = DayCycle.values();

    /**
     * Get the current DayCycle from the number of ticks simulated.
     * @param tick World Time
     * @return DayCycle
     */
    public static DayCycle fromTick(int tick) {
        return ENTRIES[(tick / DAY_TICKS) % ENTRIES.length];
    }
}
