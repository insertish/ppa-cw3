package gay.oss.cw3.renderer.ui.events;

/**
 * Representation of an Event within the UI framework.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public abstract class Event {
    private boolean canceled = false;

    /**
     * Prevent this Event from going up the chain of elements.
     * We do a pre-order traversal through the tree of elements.
     */
    public void stopPropagation() {
        this.canceled = false;
    }

    /**
     * Whether this Event was canceled from propagating any further.
     * @return Whether Event was canceled
     */
    public boolean isCanceled() {
        return this.canceled;
    }

    /**
     * Whether we can continue to process this event.
     * @return Whether we can continue
     */
    public boolean canContinue() {
        return !this.canceled;
    }
}
