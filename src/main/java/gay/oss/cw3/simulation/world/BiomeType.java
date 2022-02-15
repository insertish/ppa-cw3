package gay.oss.cw3.simulation.world;

public enum BiomeType {
    Plains(new float[] { 0.32f, 0.42f, 0.19f }),
    Forest(new float[] { 0.60f, 0.50f, 0.29f }),
    Jungle(new float[] { 0.04f, 0.23f, 0.01f });

    private float[] colour;

    private BiomeType(float[] colour) {
        this.colour = colour;
    }

    public float[] getColour() {
        return this.colour;
    }

    public static BiomeType[] BIOME_TYPES = {
        Plains, Forest, Jungle
    };
}
