package gay.oss.cw3.renderer.shaders;

public enum LevelOfDetail {
    High(20),
    Medium(70),
    Low(250),
    DoNotRender(0);

    int minimumDistance;

    private LevelOfDetail(int minimumDistance) {
        this.minimumDistance = minimumDistance;
    }

    public int getMinimumDistance() {
        return this.minimumDistance;
    }

    public static final LevelOfDetail[] ORDERING = new LevelOfDetail[] {
        High, Medium, Low
    };

    public static LevelOfDetail fromDistance(int distance) {
        for (LevelOfDetail lod : ORDERING) {
            if (distance < lod.minimumDistance) {
                return lod;
            }
        }

        return DoNotRender;
    }
}
