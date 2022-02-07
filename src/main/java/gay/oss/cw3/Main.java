package gay.oss.cw3;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private Window window;

    private void initGLFW() {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        Window.setWindowHints();
        window = Window.create(400, 200, "League of Legends");

        // Make the OpenGL context current
        glfwMakeContextCurrent(window.getPointer());
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window.getPointer());
    }


    private void initGL() {
        GL.createCapabilities();
        glViewport(0, 0, window.getWidth(), window.getHeight());
        // Set the clear color
        glClearColor(0.2f, 0.5f, 0.8f, 0.0f);
    }

    private void renderLoop() {
        // Clear the framebuffer
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Swap the color buffers
        glfwSwapBuffers(window.getPointer());
    }

    public static void main(String[] args) {
        var instance = new Main();
        instance.initGLFW();
        instance.initGL();

        for (;;) instance.renderLoop();
    }
}
