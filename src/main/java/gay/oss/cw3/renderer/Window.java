package gay.oss.cw3.renderer;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL;

import gay.oss.cw3.renderer.interfaces.IKeyCallback;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * Wrapper class around a GLFW Window
 */
public class Window {
    private final long pointer;
    private int width;
    private int height;
    private @NotNull String title;

    /**
     * Construct a new Window
     * @param pointer Pointer to the GLFW Window
     * @param width Width of the window
     * @param height Height of the window
     * @param title Title used for the window
     */
    private Window(long pointer, final int width, int height, @NotNull String title) {
        this.pointer = pointer;
        this.width = width;
        this.height = height;
        this.title = title;
    }

    /**
     * Get the width of this window
     * @return Window width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of this window
     * @return Window height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Get the title shown on this window
     * @return Window title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Use GL context for this window
     */
    public void useContext() {
        glfwMakeContextCurrent(this.pointer);
    }

    /**
     * Make this window visible
     */
    public void makeVisible() {
        glfwShowWindow(this.pointer);
    }

    /**
     * Swap the framebuffers
     */
    public void swap() {
        glfwSwapBuffers(this.pointer);
    }

    /**
     * Check whether the window is requesting close
     * @return Whether we should close.
     */
    public boolean shouldClose() {
        return glfwWindowShouldClose(this.pointer);
    }

    /**
     * Configure an OpenGL context for rendering to
     */
    public void configureGL() {
        this.useContext();

        GL.createCapabilities();
        glfwSwapInterval(1);
        glViewport(0, 0, this.getWidth(), this.getHeight());

        // Set the clear color
        glClearColor(0.6f, 0.5f, 0.8f, 0.0f);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Enable transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    public void setKeyCallback(IKeyCallback cbfun) {
        glfwSetKeyCallback(this.pointer, (win, key, scancode, action, modifiers) -> {
            cbfun.invoke(key, action, modifiers);
        });
    }

    public void setTitle(String title) {
        glfwSetWindowTitle(this.pointer, title);
    }

    /**
     * Tell GLFW what properties the Window should have
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
     * Create a new Window
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
