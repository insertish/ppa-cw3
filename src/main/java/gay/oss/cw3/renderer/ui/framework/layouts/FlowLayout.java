package gay.oss.cw3.renderer.ui.framework.layouts;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

public class FlowLayout extends Node {
    private Node[] children;
    private int gap;

    public FlowLayout(Node[] children) {
        this.children = children;
    }

    public int getGap() {
        return gap;
    }

    public FlowLayout setGap(int gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        int offset = 0;
        for (Node child : children) {
            child.draw(ui, x + offset, y, child.getWidth(), child.getHeight());
            offset += child.getWidth() + gap;
        }
    }

    @Override
    public int getWidth() {
        int width = 0;
        for (Node child : children) {
            width += child.getWidth();
        }

        return width + (children.length - 1) * gap;
    }

    @Override
    public int getHeight() {
        int height = 0;
        for (Node child : children) {
            height = Math.max(height, child.getHeight());
        }
        
        return height;
    }
}
