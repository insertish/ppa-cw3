package gay.oss.cw3.renderer.ui.framework.components;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.Font;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

public class Text extends Node {
    private Font font;
    private String value;
    private int size;
    private Vector4f colour;

    public Text(Font font, String value, int size) {
        this.setFont(font);
        this.setValue(value);
        this.setSize(size);
    }

    public Font getFont() {
        return font;
    }

    public Text setFont(Font font) {
        this.font = font;
        return this;
    }

    public String getValue() {
        return value;
    }

    public Text setValue(String value) {
        this.value = value;
        return this;
    }

    public int getSize() {
        return size;
    }

    public Text setSize(int size) {
        this.size = size;
        return this;
    }

    public Vector4f getColour() {
        return colour;
    }

    public Text setColour(Vector4f colour) {
        this.colour = colour;
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        this.font.drawText(ui, x, y, this.getSize(), this.getValue(), this.colour);
    }

    @Override
    public int getWidth() {
        return this.getSize() * this.getValue().length();
    }

    @Override
    public int getHeight() {
        return this.getSize();
    }
}
