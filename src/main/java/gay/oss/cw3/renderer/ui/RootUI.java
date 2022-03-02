package gay.oss.cw3.renderer.ui;

import gay.oss.cw3.renderer.ui.framework.Node;

/**
 * Helper wrapper class that renders the UI using the immediate first child.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class RootUI extends UI {
    protected Node rootNode;

    /**
     * Construct a new RootUI
     * @param rootNode Root Node to render
     * @throws Exception if UI fails to initialise
     */
    public RootUI(Node rootNode) throws Exception {
        super();
        this.rootNode = rootNode;
    }

    @Override
    protected void drawUI(int width, int height) {
        this.rootNode.draw(this, 0, 0, width, height);
    }
}
