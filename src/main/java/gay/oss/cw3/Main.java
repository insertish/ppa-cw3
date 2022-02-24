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
import gay.oss.cw3.scenarios.DefaultScenario;
import gay.oss.cw3.scenarios.Scenario;

public class Main {
    public static int WORLD_SIZE = 64;//256;//128;

    private Window window;
    private Scenario scenario;
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

        // Configure Scenario
        this.scenario = new DefaultScenario(WORLD_SIZE, WORLD_SIZE, true);
        this.generateWorld();
    }

    public void destroy() {
        this.tickThread.interrupt();
    }

    private void generateWorld() throws Exception {
        // Kill existing thread if running
        if (this.tickThread != null) this.tickThread.interrupt();

        // Generate world
        this.scenario.init();
        this.scenario.generate();

        // Off-load World tick to another thread
        tickThread = new Thread() {
            public void run() {
                var world = scenario.getWorld();
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
                    WORLD_SIZE / 5, 40.0f, WORLD_SIZE / 5,
                    WORLD_SIZE / 2, 0.0f, WORLD_SIZE / 2,
                    0.0f, 1.0f, 0.0f);

        // World rendering
        this.scenario.getRenderer().draw(viewProjection);

        // Update title with render time.
        window.setTitle("Deez - Frame: " + (System.currentTimeMillis() - start) + "ms - Tick: " + this.scenario.getWorld().getTime());

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
}
