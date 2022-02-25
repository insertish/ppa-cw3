package gay.oss.cw3.renderer.ui.framework.layouts;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

public class FlowLayout extends Node {
    private Node[] children;
    private int gap;
    private FlowDirection direction;

    public FlowLayout(Node[] children) {
        this.children = children;
        this.setDirection(FlowDirection.Row);
    }

    public FlowDirection getDirection() {
        return direction;
    }

    public FlowLayout setDirection(FlowDirection direction) {
        this.direction = direction;
        return this;
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
        boolean horiz = this.direction.isHorizontal();
        for (Node child : children) {
            child.draw(
                ui,
                x + ( horiz ? offset : 0),
                y + (!horiz ? offset : 0),
                child.getWidth(),
                child.getHeight()
            );

            offset += gap + (horiz ? child.getWidth() : child.getHeight());
        }
    }

    @Override
    public int getWidth() {
        int width = 0;
        if (this.direction.isHorizontal()) {
            for (Node child : children) {
                width += child.getWidth();
            }

            return width + (children.length - 1) * gap;
        } else {
            for (Node child : children) {
                width = Math.max(width, child.getWidth());
            }

            return width;
        }
    }

    @Override
    public int getHeight() {
        int height = 0;
        if (this.direction.isHorizontal()) {
            for (Node child : children) {
                height = Math.max(height, child.getHeight());
            }

            return height;
        } else {
            for (Node child : children) {
                height += child.getHeight();
            }

            return height + (children.length - 1) * gap;
        }
    }
}
