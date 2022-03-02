package gay.oss.cw3.renderer.simulation;

import java.util.Random;

/**
 * Helper class for producing a "smoothed random" value.
 * Essentially, we pick an initial value and
 * randomly go up or down based on some variation.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class SmoothedRandom {
    private float value;
    private final float variation;
    private final Random random = new Random();

    /**
     * Initialise a new SmoothedRandom value generator
     * @param initialValue Initial value to start at
     * @param variation Variation to clamp at each tick
     */
    public SmoothedRandom(float initialValue, float variation) {
        this.value = initialValue;
        this.variation = variation;
    }

    /**
     * Get the next value.
     * @return Next value
     */
    public float next() {
        this.value += this.random.nextFloat() * this.variation;
        return this.value;
    }
}
