package gay.oss.cw3.scenarios;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gay.oss.cw3.simulation.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import gay.oss.cw3.renderer.simulation.WorldRenderer;
import gay.oss.cw3.simulation.generation.WorldGenerator;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * Class representing a particular instance of the
 * World and the information required to render it.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public abstract class Scenario {
    private int width;
    private int depth;

    private World world;
    private WorldGenerator generator;

    protected boolean isOpenGL;
    private WorldRenderer renderer;

    private Map<Class<?>, String> entityNames;
    private Map<Class<?>, Vector3f> entityColours;

    /**
     * Construct a new Scenario
     * @param width Width of the simulation
     * @param depth Depth of the simulation
     * @param isOpenGL Whether we are running in OpenGL mode
     * @throws Exception if one or more resources fail to initialise
     */
    public Scenario(int width, int depth, boolean isOpenGL) throws Exception {
        this.width = width;
        this.depth = depth;
        this.isOpenGL = isOpenGL;
        this.entityNames = new HashMap<>();
        this.entityColours = new HashMap<>();

        this.init();
    }

    /**
     * Register a new Entity in the Scenario
     * @param layer Layer at which this Entity will spawn
     * @param name Name used to identify this entity type
     * @param colour Colour used for marking this entity
     * @param entityClass The relevant Class used to create new instances of this Entity
     * @param spawnChance Spawn chance
     * @param canSpawnOnWater Whether the entity can spawn on water
     * @param canSpawnOnLand Whether the entity can spawn on land
     * @param biome Array of biomes the entity is permitted to spawn in
     */
    protected void registerEntity(EntityLayer layer, String name, Vector3f colour, Class<? extends Entity> entityClass, float spawnChance, boolean canSpawnOnWater, boolean canSpawnOnLand, @Nullable BiomeType[] biome) {
        this.entityNames.put(entityClass, name);
        this.entityColours.put(entityClass, colour);
        this.generator.registerEntity(layer, entityClass, spawnChance, canSpawnOnWater, canSpawnOnLand, biome);
    }

    /**
     * Get the name of an certain entity type
     * @param entityClass Entity class
     * @return Name
     */
    public String getEntityName(Class<?> entityClass) {
        return this.entityNames.get(entityClass);
    }

    /**
     * Get the colour of a certain entity type
     * @param entityClass Entity class
     * @return RGB colour space vector
     */
    public Vector3f getEntityColour(Class<?> entityClass) {
        return this.entityColours.get(entityClass);
    }

    /**
     * Initialise the World, Generator and Renderer
     * @throws Exception if one or more resources fail to initialise
     */
    public void init() throws Exception {
        this.world = new World(this.width, this.depth);
        this.generator = new WorldGenerator(world);

        if (isOpenGL) {
            this.renderer = new WorldRenderer(world);
        }
    }

    /**
     * Generate the World and update the Renderer
     * @throws Exception if we fail to load one or more resources
     */
    public void generate() throws Exception {
        this.generator.generate();

        if (this.isOpenGL) {
            this.renderer.init();
        }
    }

    /**
     * Get the World associated with this Scenario
     * @return World
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Get the WorldGenerator associated with this Scenario
     * @return WorldGenerator
     */
    protected WorldGenerator getGenerator() {
        return this.generator;
    }

    /**
     * Get the WorldRenderer associated with the Scenario
     * @return WorldRenderer
     */
    public WorldRenderer getRenderer() {
        return this.renderer;
    }

    /**
     * Get the set of all entities in this Scenario
     * @return Set of all entities
     */
    public Set<Class<?>> getEntities() {
        return this.entityNames.keySet();
    }
}
