package gay.oss.cw3;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
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
    public static int WORLD_SIZE = 256;//64;//256;//128;

    private Window window;
    private Scenario scenario;
    private Thread tickThread;

    private double zoom = 40.0;
    private double viewAngle = 0.0;
    private double groundAngle = Math.PI / 3;

    private boolean lmbHeld = false;

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

        // Handle scroll events
        window.setScrollCallback((x, y) -> {
            zoom = (float) Math.max(zoom - y, 1.0);
        });

        // Handle mouse click events
        window.setMouseButtonCallback((button, action, modifiers) -> {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                if (action == GLFW_PRESS) {
                    lmbHeld = true;
                } else {
                    lmbHeld = false;
                }
            }

            if (lmbHeld) {
                window.grabMouse();
            } else {
                window.freeMouse();
            }
        });

        // Handle mouse move events
        window.setCursorPosCallback((xPos, yPos) -> this.onCursorPos(xPos, yPos));

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

    private double lastX = 0;
    private double lastY = 0;

    private void onCursorPos(double x, double y) {
        double dx = lastX - x, dy = lastY - y;
        if (lmbHeld) {
            this.viewAngle -= dx * 0.01;
            this.groundAngle = Math.max(Math.min(this.groundAngle - dy * 0.01, Math.PI / 2 - 0.01), 0);
        }

        lastX = x;
        lastY = y;
    }

    private void renderLoop() {
        long start = System.currentTimeMillis();

        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // * Camera Code (can move into class)
        // Setup camera projection
        // 1. Find our current center position.
        float lookAtX = WORLD_SIZE / 2;
        float lookAtZ = WORLD_SIZE / 2;
        float centerHeight = this.scenario.getWorld().getMap().getHeight((int) lookAtX, (int) lookAtZ);

        // 2. Calculate the zoom modifier and find the distance and height from this point.
        double zoomModifier = 5 + Math.pow(1.1, this.zoom);
        double distance = zoomModifier * Math.cos(this.groundAngle);
        double height = zoomModifier * Math.sin(this.groundAngle);

        // 3. Find the camera position by taking an offset from the center and
        //    finding the distance in each X and Z axis according to the view angle.
        float cameraX = lookAtX + (float) (distance * Math.cos(this.viewAngle));
        float cameraY = centerHeight + (float) height;
        float cameraZ = lookAtZ + (float) (distance * Math.sin(this.viewAngle));

        // 4. Create view projection.
        Matrix4f viewProjection = new Matrix4f()
            .perspective((float) Math.toRadians(45.0f), window.getWidth() / window.getHeight(), 0.01f, 1000.0f)
            .lookAt(
                    cameraX, cameraY, cameraZ,
                    lookAtX, centerHeight, lookAtZ,
                    0.0f, 1.0f, 0.0f);

        // World rendering
        this.scenario.getRenderer().draw(viewProjection);

        // Update title with render time.
        window.setTitle("Deez - Frame: " + (System.currentTimeMillis() - start) + "ms - Tick: " + this.scenario.getWorld().getTime() + " - Zoom: " + this.zoom);

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
