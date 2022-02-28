package gay.oss.cw3.renderer.ui.framework;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.events.Event;

/**
 * Abstract representation of a UI node
 */
public abstract class Node {
    /**
     * Draw this node using the provided UI and coordinates
     * @param ui UI helper
     * @param x X position
     * @param y Y position
     * @param w Width of element
     * @param h Height of element
     */
    public abstract void draw(UI ui, int x, int y, int w, int h);

    /**
     * Get the width of this Node
     * @return Width
     */
    public abstract int getWidth();

    /**
     * Get the height of this Node
     * @return Height
     */
    public abstract int getHeight();

    /**
     * Handle incoming Event
     * @param event Event
     */
    public abstract void handle(Event event);
}
