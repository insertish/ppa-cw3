package gay.oss.cw3.renderer.ui;

import gay.oss.cw3.renderer.ui.framework.Node;

public class RootUI extends UI {
    protected Node rootNode;

    public RootUI(Node rootNode) throws Exception {
        super();
        this.rootNode = rootNode;
    }

    @Override
    protected void drawUI(int width, int height) {
        this.rootNode.draw(this, 0, 0, width, height);
    }
}
