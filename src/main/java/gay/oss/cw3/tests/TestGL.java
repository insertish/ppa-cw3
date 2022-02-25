package gay.oss.cw3.tests;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.shaders.Camera;
import gay.oss.cw3.renderer.simulation.ModelEntity;

public class TestGL {
    private Window window;
    private Camera camera;

    private Model model;

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
            if (action == GLFW_PRESS) onKeyPress(key, modifiers);
        });

        // Init model for preview
        model = new ModelEntity("entities/bird.png", "entities/bird", 1);
    }

    private void onKeyPress(int key, int modifiers) {
        if (key == GLFW_KEY_ESCAPE) {
            // should quit render loop and clean up
            System.exit(0);
        }
    }

    private void renderLoop() {
        long start = System.currentTimeMillis();

        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Calculate camera position
        camera.calculate(window.getWidth() / window.getHeight());

        // Draw model
        model.draw(camera);

        // Update title with render time.
        window.setTitle("Deez - Frame: " + (System.currentTimeMillis() - start) + "ms");

        // Swap framebuffers.
        window.swap();
    }

    public static void main(String[] args) {
        var instance = new TestGL();

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
}
