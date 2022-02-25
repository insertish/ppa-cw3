package gay.oss.cw3.renderer.simulation;

import static org.lwjgl.opengl.GL11.glDepthMask;

import java.util.HashMap;
import java.util.Random;

import org.joml.Vector3f;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class WorldRenderer {
    private final World world;
    private final HashMap<Class<?>, Model> models;

    private Model terrainModel;
    private Model waterModel;

    public WorldRenderer(World world) {
        this.world = world;
        this.models = new HashMap<>();
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
    }

    public void setModel(Class<?> clazz, Model model) {
        this.models.put(clazz, model);
    }

    public void autoLoadModel(Class<?> clazz, String name) throws Exception {
        this.setModel(clazz, new ModelEntity(Texture.fromResource("entities/" + name)));
    }

    public void autoLoadModel(Class<?> clazz, String name, String modelName, float scale) throws Exception {
        this.setModel(clazz, new ModelEntity(Texture.fromResource("entities/" + name), modelName, scale));
    }

    private static class SmoothedRandom {
        private float value;
        private final float variation;
        private final Random random = new Random();

        public SmoothedRandom(float initialValue, float variation) {
            this.value = initialValue;
            this.variation = variation;
        }

        public float next() {
            this.value += this.random.nextFloat() * this.variation;
            return this.value;
        }
    }

    private SmoothedRandom random = new SmoothedRandom(1, 0.002f);

    private void drawLayer(EntityLayer layer, Camera camera) {
        var map = this.world.getMap();

        float yOffset = 0;
        if (layer == EntityLayer.ANIMALS) {
            yOffset += 0.5f;
        }

        for (int x=0;x<map.getWidth();x++) {
            for (int z=0;z<map.getDepth();z++) {
                Entity entity = this.world.getEntity(layer, x, z);
                if (entity != null) {
                    Model model = this.models.get(entity.getClass());

                    var translation = model.getTransformation()
                        .translation(
                            x + 0.25f,
                            Math.max(map.getWaterLevel(), map.getHeight(x, z)) + yOffset,
                            z + 0.25f
                        );

                    if (model instanceof ModelEntity) {
                        float s = ((ModelEntity) model).getScale();
                        translation.scale(s, s*2, s);
                    }
                    
                    model.draw(camera);
                }
            }
        }
    }

    public void draw(Camera camera) {
        if (this.terrainModel == null || this.waterModel == null) {
            return;
        }

        // 0. setup global shader variables
        var map = this.world.getMap();
        var offset = (this.world.getTime() * 0.1f) % 64.0f;
        ShaderProgram.setUniform("lightPos", (Object) new Vector3f(offset, 64.0f, offset));

        // 1. render terrain
        this.terrainModel.draw(camera);
        
        // 2. render water
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

        // 3. render entities        
        this.drawLayer(EntityLayer.ANIMALS, camera);
        
        // we enable the depth mask so that we can support transparency here
        glDepthMask(false);
        this.drawLayer(EntityLayer.FOLIAGE, camera);
        glDepthMask(true);
    }
}
