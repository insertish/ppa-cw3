package gay.oss.cw3;

import org.jetbrains.annotations.NotNull;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
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

    public long getPointer() {
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

    public static void setWindowHints() {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    }

    public static Window create(final int width, final int height, final @NotNull String title) {
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
