package gay.oss.cw3.renderer.ui.framework;

import gay.oss.cw3.renderer.ui.UI;

public abstract class Node {
    public abstract void draw(UI ui, int x, int y, int w, int h);
    public abstract int getWidth();
    public abstract int getHeight();
}
