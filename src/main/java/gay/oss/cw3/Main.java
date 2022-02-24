package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jetbrains.annotations.Nullable;
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
import gay.oss.cw3.simulation.entity.brain.behaviours.EatFoliageBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.FleeBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.HuntBehaviour;
import gay.oss.cw3.simulation.entity.brain.behaviours.WanderAroundBehaviour;
import gay.oss.cw3.simulation.world.DayCycle;
import gay.oss.cw3.simulation.world.EntityLayer;
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
        generator.registerEntity(Rabbit.class, 0.05f);
        generator.registerEntity(Hunter.class, 0.005f);
        generator.generate();
        
        // Setup World renderer
        this.worldRenderer = new WorldRenderer(this.world);
        this.worldRenderer.init();

        // Configure Models
        this.worldRenderer.autoLoadModel(Hunter.class, "hunter.jpg");
        this.worldRenderer.autoLoadModel(Rabbit.class, "cell.jpg");

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


    public static class Rabbit extends AbstractBreedableEntity {
        public Rabbit(World world, Coordinate location) {
            super(world, location, 0, true, EntityLayer.ANIMALS);
            this.getBrain().addBehaviour(new FleeBehaviour(this, 1.0, 10, Hunter.class));
            this.getBrain().addBehaviour(new EatFoliageBehaviour(this, 1.0, 0.7, Grass.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 1.0));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 1.0));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.setFullness(this.getMaxFullness());
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
                this.removeFullness(0.01);
                if (this.getFullness() <= 0) {
                    this.addHealth(-1);
                }
            }
        }

        @Override
        public Entity createChild(Entity otherParent, Coordinate location) {
            var result = new Rabbit(this.getWorld(), location);
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
            super(world, location, 0, true, EntityLayer.ANIMALS);
            this.getBrain().addBehaviour(new HuntBehaviour(this, 1.3, 0.7, Rabbit.class));
            this.getBrain().addBehaviour(new BreedBehaviour<>(this, 0.6));
            this.getBrain().addBehaviour(new WanderAroundBehaviour(this, 0.6));

            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 2);
            this.getAttributes().set(EntityAttribute.MINIMUM_BREEDING_AGE, 100);
            this.getAttributes().set(EntityAttribute.TICKS_BETWEEN_BREEDING_ATTEMPTS, 50);
            this.getAttributes().set(EntityAttribute.FULLNESS_TO_BREED, this.getMaxFullness()/2.0);
            this.setFullness(this.getMaxFullness());
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                this.getBrain().tick();
                this.removeFullness(0.05);
                if (this.getFullness() <= 0) {
                    this.addHealth(-1);
                }
            }
        }

        @Override
        public @Nullable Entity createChild(Entity otherParent, Coordinate coordinate) {
            var result = new Hunter(this.getWorld(), coordinate);
            result.getAttributes().inheritFromParents(this.getAttributes(), otherParent.getAttributes(), 1.0);
            return result;
        }

        @Override
        public boolean isCompatible(Entity entity) {
            return true;
        }
    }

    public static class Grass extends Entity {
        private final Random random = new Random();

        public Grass(World world, Coordinate location) {
            super(world, EntityLayer.FOLIAGE, location, 0, true);
            this.getAttributes().set(EntityAttribute.MAX_HEALTH, 1);
            this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 3.0);
            this.setFullness(1.0);
        }

        @Override
        public void tick() {
            if (this.isAlive()) {
                // photosynthesis
                if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                    this.addFullness(0.2);
                }

                // spreading
                if (this.getFullness() >= 2.0) {
                    List<Coordinate> locations = new ArrayList<>();

                    for (int dX = -1; dX <= 1; dX++) {
                        for (int dZ = -1; dZ <= 1; dZ++) {
                            var coord = this.getLocation().add(dX, dZ);

                            if (this.getWorld().isInBounds(coord) && this.getWorld().getEntity(EntityLayer.FOLIAGE, coord.x, coord.z) != null) {
                                locations.add(coord);
                            }
                        }
                    }

                    if (!locations.isEmpty()) {
                        var coord = locations.get(random.nextInt(locations.size()));
                        new Grass(this.getWorld(), coord);
                        this.removeFullness(1.0);
                    }
                }

            }
        }
    }
}
