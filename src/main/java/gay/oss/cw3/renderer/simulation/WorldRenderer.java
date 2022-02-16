package gay.oss.cw3.renderer.simulation;

import java.util.HashMap;
import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.simulation.World;

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
            new Material(Resources.getShader("terrain"))
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

    public void draw(Matrix4f viewProjection) {
        var map = this.world.getMap();

        this.terrainModel.use();
        var program = ShaderProgram.getCurrent();
        var offset = (this.world.getTime() * 0.1f) % 64.0f;
        program.setUniform("lightPos", new Vector3f(offset, 64.0f, offset));
        this.terrainModel.draw(viewProjection);
        
        this.waterModel
            .getTransformation()
            .translation(0.0f, map.getWaterLevel(), 0.0f)
            .scale(map.getWidth(), 1, map.getDepth());
        
        this.waterModel.use();
        program = ShaderProgram.getCurrent();
        program.setUniform("time", (float) this.world.getTime() / 100.0f);
        program.setUniform("waterHeight", map.getWaterLevel());
        program.setUniform("waterFadeUnits", 10.0f);
        program.setUniform("waterTransparency", 0.4f);
        program.setUniform("waterWaveHeight", 1.0f);
        program.setUniform("waterWaveFrequency", 8.0f);
        program.setUniform("waterWaveSpeed", 0.1f);
        program.setUniform("waterDisplacementModifier", 1.4f);
        program.setUniform("waterRandomDisplacement", this.random.next());
        this.waterModel.draw(viewProjection);
    }
}
