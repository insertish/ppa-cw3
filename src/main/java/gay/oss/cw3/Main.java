package gay.oss.cw3;

import gay.oss.cw3.renderer.Mesh;
import gay.oss.cw3.renderer.Shader;
import gay.oss.cw3.renderer.ShaderProgram;
import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
    private Window window;
    private ShaderProgram program;
    private Mesh mesh;

    private static final String V_SHADER = "#version 330 core\n" +
    "layout (location = 0) in vec3 vPos;\n" +
    "void main() {\n" +
    "    gl_Position = vec4(vPos, 1.0);\n" +
    "}";

    private static final String F_SHADER = "#version 330 core\n" +
    "out vec4 color;\n" +
    "void main() {\n" +
    "    color = vec4(1.0, 0.0, 0.0, 1.0);\n" +
    "}";

    private void init() throws IllegalStateException, Exception {
        Util.initialiseLWJGL();

        // Configure Window
        window = Window.create(400, 200, "League of Legends");
        window.configureGL();
        window.makeVisible();

        // Handle key events
        window.setKeyCallback((key, action, modifiers) -> {
            if (action == GLFW_PRESS) onKeyPress(key, modifiers);
        });

        // Create a shader program
        Shader vertexShader = Shader.create(GL_VERTEX_SHADER, V_SHADER);
        Shader fragShader   = Shader.create(GL_FRAGMENT_SHADER, F_SHADER);

        program = ShaderProgram.create(new Shader[] { vertexShader, fragShader });

        // Create a triangle
        float data[] = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f
        };

        mesh = Mesh.build().vertex(data).build();
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

        // Use shader
        program.use();

        // Draw mesh
        mesh.draw();

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

        while (!instance.window.shouldClose())
            instance.renderLoop();
    }
}
