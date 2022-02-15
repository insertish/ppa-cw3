package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.Random;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.renderer.simulation.MeshUtil;
import gay.oss.cw3.renderer.simulation.ModelEntity;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.FleeBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.Map;

import static org.lwjgl.opengl.GL11.*;

public class Main {
    public static int WORLD_SIZE = 256;

    private Window window;

    private World world;
    
    private Material terrainMaterial;
    private Model model;
    private Map map;

    private Model waterModel;
    private Model amongUsModel;
    private Model entityModel;
    private Model entity2Model;
    
    private Model cubeModel;

    private void init() throws Exception {
        // BRAINS TEST
        this.generateWorld();
        // BRAINS END

        Util.initialiseLWJGL();

        // Configure Window
        window = Window.create(1280, 720, "Genshin Impact");
        window.configureGL();
        window.makeVisible();

        // Handle key events
        window.setKeyCallback((key, action, modifiers) -> {
            if (action == GLFW_PRESS) onKeyPress(key, modifiers);
        });

        // Init texture program
        var textureProgram = ShaderProgram.fromName("texturedObject");

        // Generic UV coordinates
        float[] uv = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
        };

        // Water
        var waterMesh = MeshUtil.makePlane(1, 1, false, 5);
        
        var waterMaterial = new Material(
            ShaderProgram.fromName("water"),
            Texture.fromResource("water.jpg")
        );

        waterModel = new Model(waterMesh, waterMaterial);
        waterModel.getTransformation()
            .translation(0.0f, -8.0f, 0.0f)
            .scale((float) WORLD_SIZE, 1, (float) WORLD_SIZE);

        // Create a spinning among us square
        float vertex[] = {
            -0.8f, -0.8f, 0.0f, // BL
            0.8f, -0.8f, 0.0f, // BR
            0.8f, 0.8f, 0.0f, // TR

            -0.8f, -0.8f, 0.0f, // BL
            0.8f, 0.8f, 0.0f, // TR
            -0.8f, 0.8f, 0.0f, // TL
        };

        var mesh = Mesh.builder()
            .vertex(vertex)
            .render(uv, 2)
            .build();
        
        var material = new Material(
            textureProgram,
            Texture.fromResource("amogus.png")
        );
        
        // Generate the terrain
        var terrainProgram = ShaderProgram.fromName("terrain");
        this.terrainMaterial = new Material(terrainProgram);
        this.generateMap();

        // Make Among Us
        amongUsModel = new Model(mesh, material);
        amongUsModel.getTransformation()
            .translate(WORLD_SIZE / 2, WORLD_SIZE/2 + map.getHeight(WORLD_SIZE / 2, WORLD_SIZE / 2) * 2, WORLD_SIZE / 2)
            .scale(WORLD_SIZE / 10, WORLD_SIZE / 5, WORLD_SIZE / 10);

        entityModel = new Model(mesh, material);

        entity2Model = new Model(waterMesh, waterMaterial);

        // Cube
        cubeModel = new ModelEntity(Texture.fromResource("amogus.png"));
    }

    private void generateWorld() {
        world = new World(WORLD_SIZE, WORLD_SIZE);

        for (int x=0;x<WORLD_SIZE;x++) {
            for (int z=0;z<WORLD_SIZE;z++) {
                if (new Random().nextFloat() < 0.05) {
                    new EntityCell(world, new Coordinate(x, z));
                } else if (new Random().nextFloat() < 0.005) {
                    new Hunter(world, new Coordinate(x, z));
                }
            }
        }

        world.tick();
    }
    
    private void generateMap() {
        this.map = new Map(WORLD_SIZE, WORLD_SIZE);
        map.generate();
        if (model != null) model.destroyMesh();
        var mesh = MeshUtil.generateIndexedMeshFromMap(map);
        model = new Model(mesh, terrainMaterial);
    }

    private void onKeyPress(int key, int modifiers) {
        if (key == GLFW_KEY_ESCAPE) {
            // should quit render loop and clean up
            System.exit(0);
        } else if (key == GLFW.GLFW_KEY_N) {
            this.generateMap();
        } else if (key == GLFW.GLFW_KEY_R) {
            this.generateWorld();
        }
    }

    private static float Z_POS = -WORLD_SIZE;
    private static float i = 0;

    private void renderLoop() {
        long start = System.currentTimeMillis();

        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Setup camera projection
        Matrix4f viewProjection = new Matrix4f()
            .perspective((float) Math.toRadians(45.0f), window.getWidth() / window.getHeight(), 0.01f, 1000.0f)
            .lookAt(
                    //2, 2, 2,
                    //0, 0, 0,
                    //0, 80.0f, 0,
                    WORLD_SIZE / 2.8f, 120.0f, WORLD_SIZE / 2.8f,
                    WORLD_SIZE / 2, 0.0f, WORLD_SIZE / 2,
                    0.0f, 1.0f, 0.0f);

        // testing
        //cubeModel.getTransformation()
            //.rotate(0.05f, 1, 1, 0);

        //cubeModel.draw(viewProjection);
        
        // Rotate Among Us
        amongUsModel.getTransformation()
            .rotate(0.2f, 0, 1, 0);

        // Draw model
        this.model.use();
        var program = ShaderProgram.getCurrent();
        program.setUniform("lightPos", new Vector3f(Z_POS, 64.0f, Z_POS));

        Z_POS += 0.3f;
        if (Z_POS > WORLD_SIZE) Z_POS = -WORLD_SIZE;

        this.model.draw(viewProjection);

        // Draw water level
        this.waterModel.use();
        ShaderProgram.getCurrent().setUniform("time", i);
        this.waterModel.draw(viewProjection);

        i += 0.01f;

        // Draw Among Us
        // this.amongUsModel.draw(viewProjection);

        // Tick
        /*world.tick();

        // Draw all entities
        for (int x=0;x<this.map.getWidth();x++) {
            for (int z=0;z<this.map.getDepth();z++) {
                Entity entity = this.world.getEntity(x, z);
                if (entity != null) {
                    Model model;
                    if (entity instanceof EntityCell) {
                        model = this.entity2Model;
                    } else {
                        model = this.entityModel;
                    }

                    model.getTransformation()
                        .translation(x, this.map.getHeight(x, z) * 40.0f + 0.2f, z);
                    
                    model.draw(viewProjection);
                }
            }
        }*/

        // Update title with render time.
        window.setTitle("Genshin Impact - Frame took " + (System.currentTimeMillis() - start) + "ms");

        // Swap framebuffers.
        window.swap();
    }

    public static void main(String[] args) {
        var instance = new Main();

        try {
            instance.init();
        } catch (Exception e) {
            // shit got fucked, fall back to other renderer and notify user
            e.printStackTrace();
            System.exit(1);
        }

        while (!instance.window.shouldClose())
            instance.renderLoop();
    }

    static class EntityCell extends AbstractBreedableEntity {
        public EntityCell(World world, Coordinate location) {
            super(world, location, 0, true);
            this.getBrain().addBehaviour(new FleeBehaviour(this, 1.0, 10, Hunter.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 1.0));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
            }
        }

        @Override
        public Entity createChild(Entity otherParent, Coordinate location) {
            var result = new EntityCell(this.getWorld(), location);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }

        @Override
        public boolean isCompatible(Entity entity) {
            return entity.isAlive();
        }
    }

    static class Hunter extends Entity {
        public Hunter(World world, Coordinate location) {
            super(world, location, 0, true);
            this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, EntityCell.class));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
            }
        }
    }
}
