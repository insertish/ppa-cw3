package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.simulation.WorldRenderer;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.World;
import gay.oss.cw3.simulation.entity.AbstractBreedableEntity;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.entity.brain.behaviours.BreedBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.FleeBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.WorldGenerator;

public class Main {
    public static int WORLD_SIZE = 256;//128;

    private Window window;

    private World world;
    private WorldRenderer worldRenderer;

    private Thread tickThread;

    private void init() throws Exception {
        Util.initialiseLWJGL();

        // Configure Window
        window = Window.create(1280, 720, "Deez");
        window.configureGL();
        window.makeVisible();

        // Handle key events
        window.setKeyCallback((key, action, modifiers) -> {
            if (action == GLFW_PRESS) onKeyPress(key, modifiers);
        });

        // Configure World
        this.generateWorld();
    }

    public void destroy() {
        this.tickThread.interrupt();
    }

    private void generateWorld() throws Exception {
        // Kill existing thread if running
        if (this.tickThread != null) this.tickThread.interrupt();

        // Create World
        world = new World(WORLD_SIZE, WORLD_SIZE);

        // Generate World
        var generator = new WorldGenerator(world);
        generator.registerEntity(EntityCell.class, 0.05f);
        generator.registerEntity(Hunter.class, 0.005f);
        generator.generate();
        
        // Setup World renderer
        this.worldRenderer = new WorldRenderer(this.world);
        this.worldRenderer.init();

        // Configure Models
        this.worldRenderer.autoLoadModel(Hunter.class, "hunter.jpg");
        this.worldRenderer.autoLoadModel(EntityCell.class, "cell.jpg");

        // Off-load World tick to another thread
        tickThread = new Thread(){
            public void run() {
                while (!Thread.currentThread().isInterrupted()) {
                    synchronized (world) {
                        world.tick();
                    }
                }
            }
        };
        
        tickThread.start();
    }

    private void onKeyPress(int key, int modifiers) {
        if (key == GLFW_KEY_ESCAPE) {
            // should quit render loop and clean up
            System.exit(0);
        } else if (key == GLFW.GLFW_KEY_N) {
            try {
                this.generateWorld();
            } catch (Exception e) {}
        }
    }

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
                    //-20, 20, -20,
                    //0, 0, 0,
                    //WORLD_SIZE / 5, 100.0f, WORLD_SIZE / 5,
                    WORLD_SIZE / 3, 120.0f, WORLD_SIZE / 3,
                    WORLD_SIZE / 2, 0.0f, WORLD_SIZE / 2,
                    0.0f, 1.0f, 0.0f);

        // World rendering
        this.worldRenderer.draw(viewProjection);

        // Update title with render time.
        window.setTitle("Deez - Frame: " + (System.currentTimeMillis() - start) + "ms - Tick: " + world.getTime());

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
        
        instance.destroy();
    }

    public static class EntityCell extends AbstractBreedableEntity {
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

    public static class Hunter extends AbstractBreedableEntity {
        public Hunter(World world, Coordinate location) {
            super(world, location, 0, true);
            this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, EntityCell.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 500);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 80);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
            }
        }

        @Override
        public Entity createChild(Entity otherParent, Coordinate location) {
            return new Hunter(this.getWorld(), location);
        }

        @Override
        public boolean isCompatible(Entity entity) {
            return entity.isAlive();
        }
    }
}
