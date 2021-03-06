package gay.oss.cw3.renderer.simulation;

import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_FRONT;
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import gay.oss.cw3.renderer.simulation.particle.Particle;
import gay.oss.cw3.renderer.simulation.particle.ParticleManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.MeshUtil;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.shaders.Instanced;
import gay.oss.cw3.renderer.shaders.LevelOfDetail;
import gay.oss.cw3.renderer.shaders.Lighting;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

/**
 * Helper class for rendering the World in 3D space.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class WorldRenderer {
    private final World world;
    private final HashMap<Class<?>, ModelEntity> models;
    private final ParticleManager particleManager;

    private Model terrainModel;
    private Model waterModel;
    private Skybox skybox;

    private CloudRenderer cloudRenderer;

    private Lighting lighting;
    private Instanced instancedRenderer;

    /**
     * Construct a new WorldRenderer.
     * @param world Target World
     */
    public WorldRenderer(World world) {
        this.world = world;
        this.models = new HashMap<>();
        this.lighting = new Lighting();
        this.particleManager = world.getParticleManager();
    }

    /**
     * Initialise the WorldRenderer and all relevant resources.
     * @throws Exception if we fail to load one or more resources
     */
    public void init() throws Exception {
        var map = this.world.getMap();

        // Destroy existing terrain mesh if it exists.
        if (this.terrainModel != null) {
            this.terrainModel.destroyMesh();
        }

        // Generate a new terrain model.
        this.terrainModel = new Model(
            MeshUtil.generateIndexedMeshFromMap(map),
            new Material(Resources.getShader("terrain"), Resources.getTexture("grass/diffuse.jpg"))
        );

        // Destroy existing water mesh if it exists.
        if (this.waterModel != null) {
            this.waterModel.destroyMesh();
        }
        
        // Generate the water model.
        var waterMesh = MeshUtil.makeIndexedPlane(1, 1, false, 5);
        
        int length = (int) Math.pow(2, 5);
        float[] depthData = new float[length * length];

        for (int x=0;x<length;x++) {
            for (int z=0;z<length;z++) {
                int X = Math.round(((float) x / (float) length) * (float) map.getWidth());
                int Z = Math.round(((float) z / (float) length) * (float) map.getDepth());
                depthData[x * length + z] = map.getHeight(Z, X);
            }
        }
        
        waterMesh.bindArray(3, 1, depthData);

        this.waterModel = new Model(
            waterMesh,
            new Material(Resources.getShader("water"), Resources.getTexture("water.jpg"))
        );
        
        // Setup skybox and materials for rendering
        this.skybox = new Skybox();

        // Prepare particle models
        this.particleManager.computeModels();

        // Setup Cloud renderer
        this.cloudRenderer = new CloudRenderer();

        // Setup instanced renderer
        this.instancedRenderer = new Instanced();
    }

    /**
     * Set the model used for an entity type
     * @param clazz Class used for Entity
     * @param model Model to use
     */
    public void setModel(Class<?> clazz, ModelEntity model) {
        this.models.put(clazz, model);
    }

    /**
     * Automatically determine and load the correct model.
     * @param clazz Associated class
     * @param textureName Texture used for rendering this Entity
     * @param modelName Name of the Model, used to look up the obj file
     * @param scale Scale used for rendering this Model
     * @param renderMode Whether this Model has transparency and needs to be done in a separate render pass (and if it needs to have culling disabled)
     * @throws Exception if we fail to initialise one or more resources
     */
    public void autoLoadModel(Class<?> clazz, String textureName, String modelName, float scale, RenderMode renderMode) throws Exception {
        this.setModel(clazz, new ModelEntity("entities/" + textureName, modelName, scale, renderMode));
    }

    /**
     * Draw all entities.
     * @param camera Camera to use for rendering
     */
    private void drawEntities(Camera camera) {
        var map = this.world.getMap();
        Map<Class<?>, List<Vector3f>> layers = new HashMap<>();
        Map<Class<?>, EntityLayer> knownLayer = new HashMap<>();

        for (Class<?> clazz : this.models.keySet()) {
            layers.put(clazz, new ArrayList<>());
        }

        // Pull out all entities to render.
        // We should aim to take as little time here as possible.
        List<Entity> entities = new ArrayList<>(this.world.getEntities());
        for (Entity entity : entities) {
            var clazz = entity.getClass();
            var list = layers.get(clazz);
            if (list != null) {
                var loc = entity.getLocation();
                list.add(new Vector3f(loc.x, entity.yOffset(), loc.z));
                knownLayer.put(clazz, entity.getLayer());
            }
        }

        // Sort models to render non-transparent entities first.
        var keys = this.models.keySet()
            .stream()
            .sorted((a, b) -> {
                var modelA = this.models.get(a);
                var modelB = this.models.get(b);

                if ((modelA instanceof ModelEntity) && (modelB instanceof ModelEntity)) {
                    if (((ModelEntity) modelA).getRenderMode() == RenderMode.Transparent) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return 0;
                }
            })
            .collect(Collectors.toList());

        // Pre-compute the camera's eye position.
        Vector3f eyePosition = camera.getEyePositionVector();

        // Batch render each set of models.
        for (Class<?> clazz : keys) {
            ModelEntity model = this.models.get(clazz);
            var locations = layers.get(clazz);
            var offsets = map.getOffsets(knownLayer.get(clazz));

            // Prepare level of detail map.
            Map<LevelOfDetail, List<Matrix4f>> lodMatrices = new EnumMap<>(LevelOfDetail.class);
            for (LevelOfDetail lod : LevelOfDetail.ORDERING) {
                lodMatrices.put(lod, new ArrayList<>());
            }

            // Cycle through all the known locations of entities.
            for (Vector3f location : locations) {
                float[] cellOffsets = offsets.get((int) location.x, (int) location.z);
                Vector3f worldSpaceLocation = location.add(cellOffsets[0], cellOffsets[1], cellOffsets[2]);

                // Calculate Level of Detail
                float distance = worldSpaceLocation.distance(eyePosition);
                LevelOfDetail lod = LevelOfDetail.fromDistance((int) distance);

                if (lod != LevelOfDetail.DoNotRender) {
                    lodMatrices.get(lod).add(
                        new Matrix4f()
                            .translation(worldSpaceLocation)
                            .rotate(cellOffsets[3], 0, 1, 0)
                    );
                }
            }

            // Scale all model matrices as appropriate.
            var scale = model.getScale();
            for (LevelOfDetail lod : LevelOfDetail.ORDERING) {
                for (Matrix4f matrix : lodMatrices.get(lod)) {
                    matrix.scale(scale, scale * 2, scale);
                }
            }

            // If we need to render transparently, disable culling and
            // prevent any writes to the depth buffer to prevent weird artifacts.
            boolean masked = false;
            if (model.getRenderMode() == RenderMode.Transparent) {
                masked = true;
                glDepthMask(false);
                glDisable(GL_CULL_FACE);
            }

            // Cycle through each LOD and render as appropriate.
            for (LevelOfDetail lod : LevelOfDetail.ORDERING) {
                var transformations = lodMatrices.get(lod);

                if (!transformations.isEmpty()) {
                    model.use();
                    camera.upload();
                    this.instancedRenderer.draw(model.getMesh(lod), transformations);
                }
            }

            // Re-enable culling and writing to depth buffer after we are done.
            if (masked) {
                glDepthMask(true);
                glEnable(GL_CULL_FACE);
            }
        }
    }

    /**
     * Draw particles into the World
     * @param camera Camera
     */
    private void drawParticles(Camera camera) {
        List<Particle> particles = this.particleManager.getParticles();

        Map<Model, List<Matrix4f>> particlesPerModel = particles
            .stream()
            .collect(Collectors
                .groupingBy(
                    particleManager::getModelForParticle,
                    Collectors.mapping(p -> p.getMatrix(camera), Collectors.toList())
                )
            );

        // Batch render each set of models.
        for (Model model : particlesPerModel.keySet()) {
            List<Matrix4f> matrices = particlesPerModel.get(model);

            // Particles need transparency here, disable culling and
            // prevent any writes to the depth buffer to prevent weird artifacts.
            glDepthMask(false);
            glDisable(GL_CULL_FACE);

            if (!matrices.isEmpty()) {
                model.use();
                camera.upload();
                this.instancedRenderer.draw(model.getMesh(), matrices);
            }

            // Re-enable culling and writing to depth buffer after we are done.
            glDepthMask(true);
            glEnable(GL_CULL_FACE);
        }
    }

    private SmoothedRandom random = new SmoothedRandom(1, 0.002f);

    /**
     * Draw the World to the screen
     * @param camera Camera
     */
    public void draw(Camera camera, boolean drawParticles) {
        if (this.terrainModel == null || this.waterModel == null) {
            return;
        }

        // 0. setup global shader variables
        int mod = (world.getTime() % 100);
        float[] skyColour = this.world.getDayCycle().getSkyColour(mod > 80 ? (mod - 80) / 20.0f : 0);

        float[] lightColour = this.world.getDayCycle().getSunColour(mod > 80 ? (mod - 80) / 20.0f : 0);
        this.lighting.setLightDiffuse(new Vector3f(lightColour));

        float offset = (world.getTime() % 400) / 100.0f;
        float pos = 0;
        if (offset > 3) {
            pos = (2 - (offset - 3) * 3);
        } else {
            pos = offset - 1;
        }

        this.lighting.setLightDirection(new Vector4f(pos * 128.0f, 42.0f, pos * 128.0f, 0.0f));
        this.lighting.upload();

        // 1. render skybox
        this.skybox.draw(camera, new Vector3f(skyColour[0], skyColour[1], skyColour[2]));

        // 2. render terrain
        glCullFace(GL_FRONT);
        this.terrainModel.draw(camera);
        glCullFace(GL_BACK);

        // 3. render entities
        this.drawEntities(camera);

        // 4. render particles
        if (drawParticles) this.drawParticles(camera);

        // 5. render water
        var map = this.world.getMap();
        this.waterModel
            .getTransformation()
            .translation(0.0f, map.getWaterLevel(), 0.0f)
            .scale(map.getWidth(), 1, map.getDepth());
        
        this.waterModel.use();
        var program = ShaderProgram.getCurrent();
        program.setUniform("time", (float) this.world.getTime() / 100.0f);
        program.setUniform("waterHeight", map.getWaterLevel());
        program.setUniform("waterFadeUnits", 10.0f);
        program.setUniform("waterTransparency", 0.4f);
        program.setUniform("waterWaveHeight", -1.0f);
        program.setUniform("waterWaveFrequency", 8.0f);
        program.setUniform("waterWaveSpeed", 0.1f);
        program.setUniform("waterDisplacementModifier", 1.4f);
        program.setUniform("waterRandomDisplacement", this.random.next());
        
        glCullFace(GL_FRONT);
        this.waterModel.draw(camera);
        glCullFace(GL_BACK);

        // 6. draw cloud layer
        this.cloudRenderer.draw(camera, this.world.getTime());
    }
}
