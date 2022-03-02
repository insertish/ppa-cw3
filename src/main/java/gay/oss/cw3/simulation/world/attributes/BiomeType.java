package gay.oss.cw3.simulation.world.attributes;

/**
 * Enumeration of biome types
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public enum BiomeType {
    Plains(new float[] { 0.32f, 0.42f, 0.19f }),
    Forest(new float[] { 0.04f, 0.23f, 0.01f }),
    AridPlains(new float[] { 0.60f, 0.50f, 0.29f });

    private float[] colour;

    /**
     * Construct new BiomeType
     * @param colour Colour used for rendering this biome
     */
    private BiomeType(float[] colour) {
        this.colour = colour;
    }

    /**
     * Get biome colour
     * @return RGB float array
     */
    public float[] getColour() {
        return this.colour;
    }
}
