package gay.oss.cw3.renderer.ui.framework.components;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

public class Image extends Node {
    private Texture texture;

    public Image(Texture texture) {
        this.texture = texture;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Image setTexture(Texture texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        ui.drawRect(x, y, w, h, texture);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
