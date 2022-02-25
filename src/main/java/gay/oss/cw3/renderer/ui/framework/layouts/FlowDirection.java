package gay.oss.cw3.renderer.ui.framework.layouts;

public enum FlowDirection {
    Row(true),
    //RowReversed(true),
    Column(false);
    //ColumnReversed(false);

    private boolean horizontal;
    
    private FlowDirection(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public boolean isHorizontal() {
        return this.horizontal;
    }
}
