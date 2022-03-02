package gay.oss.cw3.renderer.ui.framework.components;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.events.Event;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.framework.Node;

/**
 * Text UI component
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Text extends Node {
    private Font font;
    private String value;
    private int size;
    private Vector4f colour;

    /**
     * Construct a new Text component
     * @param font Font used for rendering text
     * @param value Text value to display
     * @param size Size of the text
     */
    public Text(Font font, String value, int size) {
        this.setFont(font);
        this.setValue(value);
        this.setSize(size);
    }

    /**
     * Get the current Font in use
     * @return Font
     */
    public Font getFont() {
        return font;
    }

    /**
     * Set the current Font in use
     * @param font Font
     */
    public Text setFont(Font font) {
        this.font = font;
        return this;
    }

    /**
     * Get text value displayed
     * @return Value displayed
     */
    public String getValue() {
        return value;
    }

    /**
     * Set text value displayed
     * @param value Value displayed
     */
    public Text setValue(String value) {
        this.value = value;
        return this;
    }

    /**
     * Get size of the text displayed
     * @return Size
     */
    public int getSize() {
        return size;
    }

    /**
     * Set size of the text displayed
     * @param size Size
     */
    public Text setSize(int size) {
        this.size = size;
        return this;
    }

    /**
     * Get colour of the text displayed
     * @return RGBA colour space vector
     */
    public Vector4f getColour() {
        return colour;
    }

    /**
     * Set colour of the text displayed
     * @param colour RGBA colour space vector
     */
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

    @Override
    public void handle(Event event) {}
}
