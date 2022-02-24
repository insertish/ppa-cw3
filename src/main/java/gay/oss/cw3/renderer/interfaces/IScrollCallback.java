package gay.oss.cw3.renderer.interfaces;

/**
 * Key press callback
 */
public interface IScrollCallback {
    /**
     * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
     *
     * @param xOffset the scroll offset along the x-axis
     * @param yOffset the scroll offset along the y-axis
     */
    void invoke(double xOffset, double yOffset);
}
