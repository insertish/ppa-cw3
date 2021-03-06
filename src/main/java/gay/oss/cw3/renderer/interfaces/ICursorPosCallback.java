package gay.oss.cw3.renderer.interfaces;

/**
 * Cursor position callback, this is a partial of GLFWCursorPosCallbackI.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
@FunctionalInterface
public interface ICursorPosCallback {
    /**
     * Will be called when the cursor is moved.
     * 
     * <p>The callback function receives the cursor position, measured in screen coordinates but relative to the top-left corner of the window client area. On
     * platforms that provide it, the full sub-pixel cursor position is passed on.</p>
     *
     * @param xpos   The new cursor x-coordinate, relative to the left edge of the content area
     * @param ypos   The new cursor y-coordinate, relative to the top edge of the content area
     */
    void invoke(double xPos, double yPos);
}
