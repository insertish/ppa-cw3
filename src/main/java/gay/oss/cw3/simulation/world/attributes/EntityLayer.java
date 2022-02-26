package gay.oss.cw3.simulation.world.attributes;

public enum EntityLayer {
    FOLIAGE(0f),
    ANIMALS(0.2f),
    AERIAL_ANIMALS(10f),
    ;

    public final float yOffset;

    EntityLayer(float yOffset) {
        this.yOffset = yOffset;
    }
}
