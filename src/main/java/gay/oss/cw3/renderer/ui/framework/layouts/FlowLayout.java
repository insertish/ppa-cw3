package gay.oss.cw3.renderer.ui.framework.layouts;

import java.util.List;

import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.events.Event;
import gay.oss.cw3.renderer.ui.framework.Node;

/**
 * The Flow Layout, similarly to Flex box in CSS3, allows you to
 * order elements in rows or columns with an optional gap or
 * optional alignment of children in the opposite axis.
 * 
 * <p>
 * More information about Flexbox:
 * https://developer.mozilla.org/en-US/docs/Web/CSS/CSS_Flexible_Box_Layout/Basic_Concepts_of_Flexbox
 * </p>
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class FlowLayout extends Node {
    private List<Node> children;
    private int gap;
    private FlowDirection direction;
    private Alignment alignment;

    /**
     * Construct a new FlowLayout
     * @param children List of Node children
     */
    public FlowLayout(List<Node> children) {
        this.children = children;
        this.setDirection(FlowDirection.Row);
        this.setAlignment(Alignment.Start);
    }

    /**
     * Get the direction of the Flow layout
     * @return Direction of elements
     */
    public FlowDirection getDirection() {
        return direction;
    }

    /**
     * Set the direction of the Flow layout
     * @param direction Direction of elements
     */
    public FlowLayout setDirection(FlowDirection direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Get the alignment of the Flow layout
     * @return Alignment of elements
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Set the alignment of the Flow layout
     * @param alignment Alignment of elements
     */
    public FlowLayout setAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    /**
     * Get the gap between elements
     * @return Gap
     */
    public int getGap() {
        return gap;
    }

    /**
     * Set the gap between elements
     * @param gap Gap
     */
    public FlowLayout setGap(int gap) {
        this.gap = gap;
        return this;
    }

    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        int offset = 0;
        int nodeWidth = this.getWidth();
        int nodeHeight = this.getHeight();
        boolean horiz = this.direction.isHorizontal();

        for (Node child : children) {
            int offsetAxis = 0;
            if (horiz) {
                switch (this.getAlignment()) {
                    case Center: offsetAxis = (nodeHeight - child.getHeight()) / 2; break;
                    case End: offsetAxis = nodeHeight - child.getHeight(); break;
                    default:
                }
            } else {
                switch (this.getAlignment()) {
                    case Center: offsetAxis = (nodeWidth - child.getWidth()) / 2; break;
                    case End: offsetAxis = nodeWidth - child.getWidth(); break;
                    default:
                }
            }

            child.draw(
                ui,
                x + ( horiz ? offset : offsetAxis),
                y + (!horiz ? offset : offsetAxis),
                child.getWidth(),
                child.getHeight()
            );

            offset += gap + (horiz ? child.getWidth() : child.getHeight());
        }
    }

    @Override
    public int getWidth() {
        int width = 0;
        if (this.direction.isHorizontal()) {
            for (Node child : children) {
                width += child.getWidth();
            }

            return width + (children.size() - 1) * gap;
        } else {
            for (Node child : children) {
                width = Math.max(width, child.getWidth());
            }

            return width;
        }
    }

    @Override
    public int getHeight() {
        int height = 0;
        if (this.direction.isHorizontal()) {
            for (Node child : children) {
                height = Math.max(height, child.getHeight());
            }

            return height;
        } else {
            for (Node child : children) {
                height += child.getHeight();
            }

            return height + (children.size() - 1) * gap;
        }
    }

    @Override
    public void handle(Event event) {
        for (Node child : this.children) {
            child.handle(event);
        }
    }
}
