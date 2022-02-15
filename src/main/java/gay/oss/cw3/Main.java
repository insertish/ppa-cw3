package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.Random;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.renderer.simulation.MeshUtil;
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

public class Main {
    private Window window;

    private World world;
    
    private Material terrainMaterial;
    private Model model;
    private Map map;

    private Model waterModel;
    private Model amongUsModel;
    private Model entityModel;
    private Model entity2Model;

    private void init() throws Exception {
        // BRAINS TEST
        this.generateWorld();
        // BRAINS END

        Util.initialiseLWJGL();

        // Configure Window
        window = Window.create(1280, 720, "League of Legends");
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

        // Flat plane
        float flatPlaneVertex[] = {
            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f,

            0.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
        };

        var waterMesh = Mesh.builder()
            .vertex(flatPlaneVertex)
            .render(uv, 2)
            .build();
        
        var waterMaterial = new Material(
            textureProgram,
            Texture.fromResource("water-real.jpg")
        );

        waterModel = new Model(waterMesh, waterMaterial);
        waterModel.getTransformation()
            .translation(0.0f, -5.0f, 0.0f)
            .scale(64.0f, 1, 64.0f);

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

        amongUsModel = new Model(mesh, material);
        amongUsModel.getTransformation()
            .translate(32.0f, 20.0f, 32.0f)
            .scale(5.0f, 5.0f, 5.0f);

        entityModel = new Model(mesh, material);

        entity2Model = new Model(waterMesh, waterMaterial);
        
        // Generate the terrain
        var terrainProgram = ShaderProgram.fromName("terrain");
        this.terrainMaterial = new Material(terrainProgram);
        this.generateMap();
    }

    private void generateWorld() {
        world = new World(64, 64);

        for (int x=0;x<64;x++) {
            for (int z=0;z<64;z++) {
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
        this.map = new Map(64, 64);
        map.generate();
        if (model != null) model.destroyMesh();
        var mesh = MeshUtil.generateMeshFromMap(map);
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

    private void renderLoop() {
        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Setup camera projection
        Matrix4f viewProjection = new Matrix4f()
            .perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 1000.0f)
            .lookAt(0.0f, /*80.0f*/120.0f, 0.0f,
                    32.0f, 0.0f, 32.0f,
                    // 32.0f, 2.0f, 32.0f,
                    // 0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f);
        
        // Rotate Among Us
        amongUsModel.getTransformation()
            .rotate(0.2f, 0, 1, 0);

        // Draw model
        this.model.draw(viewProjection);

        // Draw water level
        this.waterModel.draw(viewProjection);

        // Draw Among Us
        this.amongUsModel.draw(viewProjection);

        // Tick
        world.tick();

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
        }

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
