package gay.oss.cw3.renderer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    private final long pointer;
    private int width;
    private int height;
    private @NotNull String title;

    private Window(long pointer, final int width, int height, @NotNull String title) {
        this.pointer = pointer;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    private long getPointer() {
        return pointer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTitle() {
        return title;
    }

    public void useContext() {
        glfwMakeContextCurrent(this.getPointer());
    }

    public void makeVisible() {
        glfwShowWindow(this.getPointer());
    }

    /**
     * Swap the framebuffers.
     */
    public void swap() {
        glfwSwapBuffers(this.getPointer());
    }

    /**
     * Configure an OpenGL context for rendering to.
     */
    public void configureGL() {
        this.useContext();

        GL.createCapabilities();
        glfwSwapInterval(1);
        glViewport(0, 0, this.getWidth(), this.getHeight());

        // Set the clear color
        glClearColor(0.6f, 0.5f, 0.8f, 0.0f);
    }

    public void setKeyCallback(GLFWKeyCallbackI cbfun) {
        glfwSetKeyCallback(this.pointer, cbfun);
    }

    /**
     * Tell GLFW what properties the Window should have.
     */
    public static void setWindowHints() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }

    /**
     * Create a new Window.
     * @param width Width of the Window
     * @param height Height of the Window
     * @param title Title used for the Window
     * @return Window
     */
    public static Window create(final int width, final int height, final @NotNull String title) {
        Window.setWindowHints();

        final long pointer = glfwCreateWindow(width, height, title, NULL, NULL);
        if (pointer == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        final var window = new Window(pointer, width, height, title);

        glfwSetFramebufferSizeCallback(pointer, (win, w, h) -> {
            glViewport(0, 0, w, h);
            window.width = w;
            window.height = h;
        });

        return window;
    }
}
