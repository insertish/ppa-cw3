package gay.oss.cw3;

import gay.oss.cw3.renderer.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
    private Window window;
    private Mesh mesh;

    private void init() throws Exception {
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
        Shader vertexShader = Shader.create(GL_VERTEX_SHADER, new String(Main.class.getResourceAsStream("/shaders/vertex.glsl").readAllBytes()));
        Shader fragShader   = Shader.create(GL_FRAGMENT_SHADER, new String(Main.class.getResourceAsStream("/shaders/fragment.glsl").readAllBytes()));

        ShaderProgram program = ShaderProgram.create(new Shader[] { vertexShader, fragShader });

        // Create a square
        float vertex[] = {
            -0.5f, -0.5f, 0.0f, // BL
            0.5f, -0.5f, 0.0f, // BR
            0.5f, 0.5f, 0.0f, // TR

            -0.5f, -0.5f, 0.0f, // BL
            0.5f, 0.5f, 0.0f, // TR
            -0.5f, 0.5f, 0.0f, // TL
        };

        float[] uv = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
        };

        mesh = Mesh.builder().vertex(vertex).material(new Material(program, new Texture(Main.class.getResourceAsStream("/textures/amogus.png")) )).render(uv, 2).build();
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
