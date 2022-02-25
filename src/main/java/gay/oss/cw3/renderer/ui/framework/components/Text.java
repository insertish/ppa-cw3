package gay.oss.cw3.renderer.ui.framework.components;

import gay.oss.cw3.renderer.ui.Font;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

public class Text extends Node {
    private Font font;
    private String value;
    private int size;

    public Text(Font font, String value, int size) {
        this.setFont(font);
        this.setValue(value);
        this.setSize(size);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        this.font.drawText(ui, x, y, this.size, this.value);
    }

    @Override
    public int getWidth() {
        return this.size * this.value.length();
    }

    @Override
    public int getHeight() {
        return this.size;
    }
}
