package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.simulation.ui.SimulationUI;
import gay.oss.cw3.renderer.ui.events.KeyEvent;
import gay.oss.cw3.scenarios.DefaultScenario;
import gay.oss.cw3.scenarios.Scenario;

/**
 * This is the main entrypoint for the simulation
 */
public class Main {
    public static int WORLD_SIZE = 128;//64;//256;//128;

    private Window window;
    private Camera camera;
    private SimulationUI ui;
    private Scenario scenario;
    private Thread tickThread;

    private boolean renderParticles = true;

    /**
     * Prepare the simulation and configure LWJGL / OpenGL for rendering
     * @throws Exception if something fails to initialise
     */
    private void init() throws Exception {
        Util.initialiseLWJGL();

        // Configure Window
        window = Window.create(1280, 720, "Deez");
        window.configureGL();
        window.makeVisible();

        // Setup Camera
        camera = new Camera();
        camera.registerEvents(window);

        // Handle key events
        window.setKeyCallback((key, action, modifiers) -> {
            this.ui.emit(new KeyEvent(key, action, modifiers));
            if (action == GLFW_PRESS) onKeyPress(key, modifiers);
        });

        // Configure Scenario
        this.scenario = new DefaultScenario(WORLD_SIZE, WORLD_SIZE, true);
        this.ui = new SimulationUI(this.scenario);
        this.generateWorld();
    }

    /**
     * Pause the tick thread
     */
    public void pause() {
        if (this.tickThread != null) {
            this.tickThread.interrupt();
            this.tickThread = null;
            this.ui.setFlagPlaying(false);
        }
    }

    /**
     * Resume the tick thread
     */
    public void resume() {
        if (this.tickThread == null) {
            this.tickThread = new Thread() {
                public void run() {
                    try {
                        var world = scenario.getWorld();
                        while (!Thread.currentThread().isInterrupted()) {
                            synchronized (world) {
                                world.tick();
                            }
    
                            Thread.sleep(16);
                        }
                    } catch (InterruptedException e) {
                        // Close out of thread.
                    }
                }
            };
            
            this.tickThread.start();
            this.ui.setFlagPlaying(true);
        }
    }

    /**
     * Clean up everything before shutting down
     */
    public void destroy() {
        this.pause();
    }

    /**
     * Generate a new World
     * @throws Exception if the Scenario fails to generate
     */
    private void generateWorld() throws Exception {
        // Kill existing thread if running
        this.pause();

        // Generate world
        this.scenario.init();
        this.scenario.generate();
        this.ui.setScenario(this.scenario);

        // Move the camera accordingly
        var map = this.scenario.getWorld().getMap();
        float center = WORLD_SIZE / 2;
        camera.setX(center);
        camera.setZ(center);
        camera.setY(
            Math.max(
                map.getHeight((int) center, (int) center),
                map.getWaterLevel()
            )
        );

        // Off-load World tick to another thread
        this.resume();
    }

    /**
     * Handle user input
     * @param key Key code
     * @param modifiers Key modifiers
     */
    private void onKeyPress(int key, int modifiers) {
        switch (key) {
            case GLFW_KEY_ESCAPE:
                System.exit(0);
                break;
            case GLFW_KEY_N:
                try {
                    this.generateWorld();
                } catch (Exception e) {}
                break;
            case GLFW_KEY_SPACE:
                if (this.tickThread == null) {
                    this.resume();
                } else {
                    this.pause();
                }
                break;
            case GLFW_KEY_P:
                this.renderParticles = !this.renderParticles;
                this.ui.setParticleState(this.renderParticles);
                break;
        }
    }

    /**
     * Graphics render loop
     */
    private void renderLoop() {
        long start = System.currentTimeMillis();

        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Update camera projection.
        this.camera.calculate(window.getWidth() / window.getHeight());

        // World rendering.
        this.scenario.getRenderer().draw(this.camera, this.renderParticles);

        // Draw the UI.
        this.ui.draw(window.getWidth(), window.getHeight());

        // Update title with render time.
        this.window.setTitle("Deez - Frame: " + (System.currentTimeMillis() - start) + "ms - Tick: " + this.scenario.getWorld().getTime() + " - Zoom: " + this.camera.getZoom());

        // Swap framebuffers.
        this.window.swap();
    }

    /**
     * Entrypoint into the application
     * @param args Arguments
     */
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
