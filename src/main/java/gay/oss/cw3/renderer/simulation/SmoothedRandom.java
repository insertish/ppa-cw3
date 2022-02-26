package gay.oss.cw3.renderer.simulation;

import java.util.Random;

public class SmoothedRandom {
    private float value;
    private final float variation;
    private final Random random = new Random();

    public SmoothedRandom(float initialValue, float variation) {
        this.value = initialValue;
        this.variation = variation;
    }

    public float next() {
        this.value += this.random.nextFloat() * this.variation;
        return this.value;
    }
}
