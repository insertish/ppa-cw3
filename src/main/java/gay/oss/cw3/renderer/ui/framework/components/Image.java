package gay.oss.cw3.renderer.ui.framework.components;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

/**
 * Image UI component
 */
public class Image extends Node {
    private Texture texture;

    /**
     * Construct a new Image
     * @param texture Texture to use for drawing
     */
    public Image(Texture texture) {
        this.texture = texture;
    }

    /**
     * Get this Image's texture
     * @return Texture
     */
    public Texture getTexture() {
        return this.texture;
    }

    /**
     * Set this Image's texture
     * @param texture Texture
     */
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
