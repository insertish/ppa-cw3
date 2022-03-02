package gay.oss.cw3.simulation.world.attributes;

/**
 * Enumeration of different entity layers.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public enum EntityLayer {
    FOLIAGE(0f),
    ANIMALS(0.2f),
    AERIAL_ANIMALS(10f);

    public final float yOffset;

    /**
     * Construct a new EntityLayer
     * @param yOffset Y offset that this later exists at
     */
    EntityLayer(float yOffset) {
        this.yOffset = yOffset;
    }
}
