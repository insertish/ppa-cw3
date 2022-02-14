package gay.oss.cw3;

import gay.oss.cw3.renderer.Mesh;
import gay.oss.cw3.renderer.Shader;
import gay.oss.cw3.renderer.ShaderProgram;
import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.renderer.Window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Main {
    private Window window;
    private ShaderProgram program;
    private Mesh mesh;

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
        Shader vertexShader = Shader.create(GL_VERTEX_SHADER, new String(Main.class.getResourceAsStream("/shaders/vertex.glsl").readAllBytes()));
        Shader fragShader   = Shader.create(GL_FRAGMENT_SHADER, new String(Main.class.getResourceAsStream("/shaders/fragment.glsl").readAllBytes()));

        program = ShaderProgram.create(new Shader[] { vertexShader, fragShader });

        // Create a triangle
        float vertex[] = {
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
            0.0f, 0.5f, 0.0f,
        };

        float uv[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 1.0f,
        };

        mesh = Mesh.builder().vertex(vertex).render(uv, 2).build();
    }

    private void onKeyPress(int key, int modifiers) {
        if (key == GLFW_KEY_ESCAPE) {
            // should quit render loop and clean up
            System.exit(0);
        }
    }

    private float r = 0;
    private float g = 0;
    private float b = 0;

    private float pos = -10.0f;

    private void renderLoop() {
        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Use shader
        program.use();

        // Setup camera projection
        Matrix4f m = new Matrix4f()
            .perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 100.0f)
            .lookAt(5.0f, 5.0f, pos,
                    0.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f);
        
        program.setUniform("viewProjection", m);

        // Do some simple colour rotation
        r += 0.01f;
        g += 0.02f;
        b += 0.04f;
        pos += 0.1f;
        program.setUniform("deez", new Vector3f(r,g,b));

        if (r >= 1) r = 0;
        if (g >= 1) g = 0;
        if (b >= 1) b = 0;
        if (pos >= 10) pos = -10.0f;

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
