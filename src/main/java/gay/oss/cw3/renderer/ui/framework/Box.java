package gay.oss.cw3.renderer.ui.framework;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.UI;

public class Box extends Node {
    private Node child;

    private Vector4f colour;
    private int padding;
    private int minWidth;
    private int minHeight;
    
    public Box(Node child) {
        this.child = child;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public Box setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public Box setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public int getPadding() {
        return padding;
    }

    public Vector4f getColour() {
        return colour;
    }

    public Box setColour(Vector4f colour) {
        this.colour = colour;
        return this;
    }

    public Box setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        if (colour != null) {
            ui.drawRect(x, y, w, h, colour);
        }

        this.child.draw(ui, x + padding, y + padding, w - 2 * padding, h - 2 * padding);
    }

    @Override
    public int getWidth() {
        int width = this.child.getWidth() + this.padding * 2;
        return Math.max(this.minWidth, width);
    }

    @Override
    public int getHeight() {
        int height = this.child.getHeight() + this.padding * 2;
        return Math.max(this.minHeight, height);
    }
}
