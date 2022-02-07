package gay.oss.cw3;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private Window window;

    private void init() throws IllegalStateException {
        Util.initialiseLWJGL();

        // Configure Window
        window = Window.create(400, 200, "League of Legends");
        window.configureGL();
        window.makeVisible();

        // Handle key events
        window.setKeyCallback((win, key, scancode, action, modifiers) -> {
            if (action == 1) onKeyPress(key, modifiers);
        });
    }

    private void onKeyPress(int key, int modifiers) {
        if (key == GLFW_KEY_ESCAPE) {
            // should quit render loop and clean up
            System.exit(0);
        }
    }

    private void renderLoop() {
        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Swap framebuffers.
        window.swap();
    }

    public static void main(String[] args) {
        var instance = new Main();

        try {
            instance.init();
        } catch (Exception e) {
            // shit got fucked, fall back to other renderer and notify user
        }

        for (;;) instance.renderLoop();
    }
}
