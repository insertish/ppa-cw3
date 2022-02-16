package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import java.util.List;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
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
    public static int WORLD_SIZE = 128;

    private Window window;

    private World world;
    private WorldRenderer worldRenderer;

    private Model pieChartModel;

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

    private void genPieChart() throws Exception {
        if (pieChartModel != null) {
            pieChartModel.destroyMesh();
        }

        int splits = 360;
        float angleDiff = (1.0f / 360.0f) * 2 * (float) Math.PI;

        float[] vertex = new float[splits * 3 * 3];
        float[] color = new float[splits * 3 * 3];


        // count
        List<Entity> entities = world.getEntities();
        int hunt = 0;
        int cell = 0;
        for (Entity entity : entities) {
            if (entity instanceof EntityCell) cell++;
            else hunt ++;
        }

        int total = hunt + cell;
        int v = Math.round(((float) hunt / (float) total) * (float) splits);




        for (int sect=0;sect<splits;sect++) {
            int offset = sect * 3 * 3;

            float angle = sect * angleDiff;
            float nextAngle = (sect + 1) * angleDiff;

            vertex[offset    ] = 0;
            vertex[offset + 1] = 0;
            vertex[offset + 2] = 0;

            vertex[offset + 3] = (float) Math.cos(angle);
            vertex[offset + 4] = (float) Math.sin(angle);
            vertex[offset + 5] = 0;

            vertex[offset + 6] = (float) Math.cos(nextAngle);
            vertex[offset + 7] = (float) Math.sin(nextAngle);
            vertex[offset + 8] = 0;

            int r = 0, g = 0, b = 0;
            if (sect > v) {
                r = 1;
            } else {
                g = 1;
            }

            color[offset    ] = r;
            color[offset + 1] = g;
            color[offset + 2] = b;
            color[offset + 3] = r;
            color[offset + 4] = g;
            color[offset + 5] = b;
            color[offset + 6] = r;
            color[offset + 7] = g;
            color[offset + 8] = b;
        }

        pieChartModel = new Model(
            Mesh.builder().vertex(vertex).render(color, 3).build(),
            new Material(ShaderProgram.fromName("PIECHARTOMEGALUL"))
        );
    }

    private void generateWorld() throws Exception {
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
        Thread thread = new Thread(){
            public void run() {
                for (;;) {
                    synchronized (world) {
                        world.tick();
                    }

                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {}
                }
            }
        };
        
        thread.start();
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
                    WORLD_SIZE / 5, 100.0f, WORLD_SIZE / 5,
                    WORLD_SIZE / 2, 0.0f, WORLD_SIZE / 2,
                    0.0f, 1.0f, 0.0f);

        // World rendering
        this.worldRenderer.draw(viewProjection);

        // draw pie chart
        glClear(GL_DEPTH_BUFFER_BIT);
        try { this.genPieChart(); } catch (Exception e) { }
        this.pieChartModel.draw(viewProjection);

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
