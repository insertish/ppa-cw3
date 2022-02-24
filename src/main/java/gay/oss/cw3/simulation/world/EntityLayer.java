package gay.oss.cw3.simulation.world;

/**
 * The world holds {@link gay.oss.cw3.simulation.entity.Entity entities} in any of these different layers, allowing
 * multiple entities to live in the same position.
 */
public enum EntityLayer {
    /**
     * Plants live on the foliage layer.
     */
    FOLIAGE,
    /**
     * Animals live on the animals layer.
     */
    ANIMALS,
    /**
     * All other entities live on the objects layer.
     */
    OBJECTS,
}
