package gay.oss.cw3.renderer.interfaces;

/**
 * Scroll callback, this is a partial of GLFWScrollCallbackI.
 */
@FunctionalInterface
public interface IScrollCallback {
    /**
     * Will be called when a scrolling device is used, such as a mouse wheel or scrolling area of a touchpad.
     *
     * @param xOffset The scroll offset along the x-axis
     * @param yOffset The scroll offset along the y-axis
     */
    void invoke(double xOffset, double yOffset);
}
