package gay.oss.cw3.renderer.simulation;

import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDepthMask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.shaders.Instanced;
import gay.oss.cw3.renderer.shaders.Lighting;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.World;

public class WorldRenderer {
    private final World world;
    private final HashMap<Class<?>, Model> models;

    private Model terrainModel;
    private Model waterModel;

    private Lighting lighting;
    private Instanced instancedRenderer;

    public WorldRenderer(World world) {
        this.world = world;
        this.models = new HashMap<>();
        this.lighting = new Lighting();
    }

    public void init() throws Exception {
        var map = this.world.getMap();
        if (this.terrainModel != null) {
            this.terrainModel.destroyMesh();
        }

        this.terrainModel = new Model(
            MeshUtil.generateIndexedMeshFromMap(map),
            new Material(Resources.getShader("terrain"), Texture.fromResource("grass/diffuse.jpg"))
        );

        if (this.waterModel != null) {
            this.waterModel.destroyMesh();
        }
        
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
            new Material(Resources.getShader("water"), Texture.fromResource("water.jpg"))
        );

        this.instancedRenderer = new Instanced();
    }

    public void setModel(Class<?> clazz, Model model) {
        this.models.put(clazz, model);
    }

    public void autoLoadModel(Class<?> clazz, String name, String modelName, float scale, boolean transparent) throws Exception {
        this.setModel(clazz, new ModelEntity("entities/" + name, "entities/" + modelName, scale, transparent));
    }

    private void drawEntities(Camera camera) {
        var map = this.world.getMap();
        Map<Class<?>, List<Matrix4f>> layers = new HashMap<>();

        for (Class<?> clazz : this.models.keySet()) {
            layers.put(clazz, new ArrayList<>());
        }

        synchronized (this.world) {
            for (Entity entity : this.world.getEntities()) {
                var list = layers.get(entity.getClass());
                if (list != null) {
                    var loc = entity.getLocation();
                    var offset = map
                        .getOffsets(entity.getLayer())
                        .get(loc);
                    
                    list.add(new Matrix4f()
                        .translation(
                            loc.x + offset[0],
                            entity.yOffset() + offset[1],
                            loc.z + offset[2]
                        )
                        .rotate(offset[3], 0, 1, 0));
                }
            }
        }

        var keys = this.models.keySet()
            .stream()
            .sorted((a, b) -> {
                var modelA = this.models.get(a);
                var modelB = this.models.get(b);

                if ((modelA instanceof ModelEntity) && (modelB instanceof ModelEntity)) {
                    if (((ModelEntity) modelA).isTransparent()) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return 0;
                }
            })
            .collect(Collectors.toList());

        for (Class<?> clazz : keys) {
            Model model = this.models.get(clazz);
            var transformations = layers.get(clazz);

            boolean masked = false;
            if (model instanceof ModelEntity) {
                var ent = (ModelEntity) model;
                var s = ent.getScale();
                for (Matrix4f matrix : transformations) {
                    matrix.scale(s, s * 2, s);
                }

                if (ent.isTransparent()) {
                    masked = true;
                    glDepthMask(false);
                }
            }

            model.use();
            camera.upload();
            this.instancedRenderer.draw(model.getMesh(), transformations);

            if (masked) {
                glDepthMask(true);
            }
        }
    }

    private SmoothedRandom random = new SmoothedRandom(1, 0.002f);

    public void draw(Camera camera) {
        if (this.terrainModel == null || this.waterModel == null) {
            return;
        }

        // 0. setup global shader variables
        int mod = (world.getTime() % 100);
        float[] skyColour = this.world.getDayCycle().getSkyColour(mod > 80 ? (mod - 80) / 20.0f : 0);
        glClearColor(skyColour[0], skyColour[1], skyColour[2], 1f);

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

        // 1. render terrain
        this.terrainModel.draw(camera);

        // 2. render entities
        this.drawEntities(camera);

        // 3. render water
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
        this.waterModel.draw(camera);
    }
}
