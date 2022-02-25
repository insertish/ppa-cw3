package gay.oss.cw3.renderer.ui.framework.layouts;

import java.util.EnumMap;
import java.util.Map;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Node;

public class AnchorLayout extends Node {
    private Map<Anchor, Node> children;

    public AnchorLayout() {
        this.children = new EnumMap<>(Anchor.class);
    }

    public AnchorLayout add(Anchor anchor, Node child) {
        this.children.put(anchor, child);
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        for (Anchor anchor : Anchor.values()) {
            Node node = this.children.get(anchor);
            if (node != null) {
                int offsetX = x, offsetY = y;
                switch (anchor.getX()) {
                    case 1: offsetX += (w - node.getWidth()) / 2; break;
                    case 2: offsetX += w - node.getWidth();
                    default: break;
                }

                switch (anchor.getY()) {
                    case 1: offsetY += (h - node.getHeight()) / 2; break;
                    case 2: offsetY += h - node.getHeight();
                    default: break;
                }

                node.draw(ui, offsetX, offsetY, node.getWidth(), node.getHeight());
            }
        }
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
