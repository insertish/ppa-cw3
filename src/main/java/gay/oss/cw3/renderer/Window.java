package gay.oss.cw3.renderer;

import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_DEBUG_CONTEXT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RAW_MOUSE_MOTION;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwRawMouseMotionSupported;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowTitle;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL;

import gay.oss.cw3.renderer.interfaces.ICursorPosCallback;
import gay.oss.cw3.renderer.interfaces.IKeyCallback;
import gay.oss.cw3.renderer.interfaces.IMouseButtonCallback;
import gay.oss.cw3.renderer.interfaces.IScrollCallback;

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

        // Enable GL, Vsync and setup viewport.
        GL.createCapabilities();
        glfwSwapInterval(1);
        glViewport(0, 0, this.getWidth(), this.getHeight());

        // Set the clear color
        glClearColor(0.6f, 0.5f, 0.8f, 0.0f);

        // Enable depth testing
        glEnable(GL_DEPTH_TEST);

        // Enable backface culling
        glEnable(GL_CULL_FACE);

        // Enable transparency
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Set the callback for key press events
     * @param cbfun Key press callback
     */
    public void setKeyCallback(IKeyCallback cbfun) {
        glfwSetKeyCallback(this.pointer, (win, key, scancode, action, modifiers) -> {
            cbfun.invoke(key, action, modifiers);
        });
    }

    /**
     * Set the callback for mouse scroll events
     * @param cbfun Scroll callback
     */
    public void setScrollCallback(IScrollCallback cbfun) {
        glfwSetScrollCallback(this.pointer, (win, x, y) -> {
            cbfun.invoke(x, y);
        });
    }

    /**
     * Set the callback for mouse button events
     * @param cbfun Mouse button callback
     */
    public void setMouseButtonCallback(IMouseButtonCallback cbfun) {
        glfwSetMouseButtonCallback(this.pointer, (win, button, action, modifiers) -> {
            cbfun.invoke(button, action, modifiers);
        });
    }

    /**
     * Set the callback for cursor position events
     * @param cbfun Cursor position callback
     */
    public void setCursorPosCallback(ICursorPosCallback cbfun) {
        glfwSetCursorPosCallback(this.pointer, (win, xPos, yPos) -> {
            cbfun.invoke(xPos, yPos);
        });
    }

    /**
     * Grab the mouse cursor from the user
     */
    public void grabMouse() {
        glfwSetInputMode(this.pointer, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        // If possible, prefer to use raw motion
        if (glfwRawMouseMotionSupported())
            glfwSetInputMode(this.pointer, GLFW_RAW_MOUSE_MOTION, GLFW_TRUE);
    }

    /**
     * Free the mouse cursor and give it back to the user
     */
    public void freeMouse() {
        glfwSetInputMode(this.pointer, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    /**
     * Set the current Window title
     * @param title Title
     */
    public void setTitle(String title) {
        glfwSetWindowTitle(this.pointer, title);
    }

    /**
     * Tell GLFW what properties the Window should have
     */
    public static void setWindowHints() {
        glfwDefaultWindowHints();

        // Configure it to be hidden by default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        // Make the window resizable
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Tell OpenGL to give us errors
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);

        // Configure OpenGL to use version 4.1
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);

        // Use OpenGL core
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

        // Whenever the framebuffer size updates, we need to update the viewport.
        glfwSetFramebufferSizeCallback(pointer, (win, w, h) -> {
            glViewport(0, 0, w, h);
            window.width = w;
            window.height = h;
        });

        return window;
    }
}
