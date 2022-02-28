package gay.oss.cw3.renderer.ui.framework.layouts;

/**
 * Anchored position within a container.
 */
public enum Anchor {
    TopLeft(0, 0),
    TopMiddle(1, 0),
    TopRight(2, 0),
    CenterLeft(0, 1),
    CenterMiddle(1, 1),
    CenterRight(2, 1),
    BottomLeft(0, 2),
    BottomMiddle(1, 2),
    BottomRight(2, 2);

    private int x;
    private int y;

    /**
     * Construct a new Anchor
     * @param x Relative X position
     * @param y Relative Y position
     */
    private Anchor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get relative X position
     * @return relative X position
     */
    public int getX() {
        return this.x;
    }

    /**
     * Get relative Y position
     * @return relative Y position
     */
    public int getY() {
        return this.y;
    }
}
