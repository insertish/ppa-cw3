package gay.oss.cw3.renderer.ui.framework.layouts;

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

    private Anchor(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
