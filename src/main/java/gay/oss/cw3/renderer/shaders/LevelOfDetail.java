package gay.oss.cw3.renderer.shaders;

/**
 * Enumeration representing the level of detail to render a model at.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public enum LevelOfDetail {
    High(20),
    Medium(70),
    Low(250),
    DoNotRender(0);

    int maximumDistance;

    /**
     * Construct new LevelOfDetail
     * @param maximumDistance Maximum distance that models are rendered at this LOD for
     */
    private LevelOfDetail(int maximumDistance) {
        this.maximumDistance = maximumDistance;
    }

    /**
     * Get the maximum distance that models are rendered at this LOD for
     * @return Maximum Distance
     */
    public int getMinimumDistance() {
        return this.maximumDistance;
    }

    /**
     * Natural ordering of detail levels
     */
    public static final LevelOfDetail[] ORDERING = new LevelOfDetail[] {
        High, Medium, Low
    };

    /**
     * Convert a given distance to a Level of Detail
     * @param distance Distance
     * @return Level of Detail to use
     */
    public static LevelOfDetail fromDistance(int distance) {
        for (LevelOfDetail lod : ORDERING) {
            if (distance < lod.maximumDistance) {
                return lod;
            }
        }

        return DoNotRender;
    }
}
