package gay.oss.cw3.renderer.ui.events;

/**
 * Representation of an Event within the UI framework
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
     * Whether we can continue to process this event.
     * @return Whether we can continue
     */
    public boolean canContinue() {
        return !this.canceled;
    }
}
