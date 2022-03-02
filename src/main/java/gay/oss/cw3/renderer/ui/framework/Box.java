package gay.oss.cw3.renderer.ui.framework;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.events.Event;

/**
 * Simple container UI element
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Box extends Node {
    protected Node child;

    private Vector4f colour;
    private int padding;
    private int minWidth;
    private int minHeight;
    
    /**
     * Create a new Box element
     * @param child Child Node to render in this Box
     */
    public Box(Node child) {
        this.child = child;
    }

    /**
     * Get the minimum height of this Box
     * @return Minimum height
     */
    public int getMinHeight() {
        return minHeight;
    }

    /**
     * Set the minimum height of this Box
     * @param minHeight Minimum height
     */
    public Box setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    /**
     * Get the minimum width of this Box
     * @return Minimum width
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * Set the minimum width of this Box
     * @param minWidth Minimum width
     */
    public Box setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    /**
     * Set the minimum size of this Box,
     * this will set both the minimum width and height.
     * @param size Minimum size
     */
    public Box setMinSize(int size) {
        this.minWidth = size;
        this.minHeight = size;
        return this;
    }

    /**
     * Get the padding used for this Box
     * @return Padding
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Set the padding used for this Box
     * @param padding Padding
     */
    public Box setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    /**
     * Get the background colour of this Box
     * @return RGBA colour space vector
     */
    public Vector4f getColour() {
        return colour;
    }

    /**
     * Set the background colour of this Box
     * @param colour RGBA colour space vector
     */
    public Box setColour(Vector4f colour) {
        this.colour = colour;
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        if (this.colour != null) {
            ui.drawRect(x, y, w, h, this.colour);
        }

        if (this.child != null) {
            this.child.draw(ui, x + padding, y + padding, w - 2 * padding, h - 2 * padding);
        }
    }

    @Override
    public int getWidth() {
        if (this.child == null) return this.minWidth;

        int width = this.child.getWidth() + this.padding * 2;
        return Math.max(this.minWidth, width);
    }

    @Override
    public int getHeight() {
        if (this.child == null) return this.minHeight;

        int height = this.child.getHeight() + this.padding * 2;
        return Math.max(this.minHeight, height);
    }

    @Override
    public void handle(Event event) {
        this.child.handle(event);
    }
}
