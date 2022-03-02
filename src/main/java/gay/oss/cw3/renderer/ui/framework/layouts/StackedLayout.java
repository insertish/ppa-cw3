package gay.oss.cw3.renderer.ui.framework.layouts;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.events.Event;
import gay.oss.cw3.renderer.ui.framework.Node;

/**
 * Stack children on top of each other
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class StackedLayout extends Node {
    protected Node[] children;
    
    /**
     * Create a new StackedLayout
     * @param child Children to render in this Box
     */
    public StackedLayout(Node[] children) {
        this.children = children;
    }
    
    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        for (Node child : children) {
            child.draw(ui, x, y, w, h);
        }
    }

    @Override
    public int getWidth() {
        int width = 0;
        for (Node child : children) {
            width = Math.max(width, child.getWidth());
        }

        return width;
    }

    @Override
    public int getHeight() {
        int height = 0;
        for (Node child : children) {
            height = Math.max(height, child.getHeight());
        }

        return height;
    }

    @Override
    public void handle(Event event) {
        for (Node child : children) {
            if (event.isCanceled()) break;
            child.handle(event);
        }
    }
}
