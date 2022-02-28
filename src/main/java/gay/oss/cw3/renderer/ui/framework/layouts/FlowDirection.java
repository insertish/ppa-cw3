package gay.oss.cw3.renderer.ui.framework.layouts;

/**
 * Direction of flow of the {@link FlowLayout}.
 */
public enum FlowDirection {
    Row(true),
    //RowReversed(true),
    Column(false);
    //ColumnReversed(false);

    private boolean horizontal;
    
    /**
     * Construct a new FlowDirection
     * @param horizontal Whether the flow is horizontal
     */
    private FlowDirection(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * Check whether the selected flow is horizontal
     * @return Whether it is horizontal
     */
    public boolean isHorizontal() {
        return this.horizontal;
    }
}
