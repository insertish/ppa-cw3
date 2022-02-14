package gay.oss.cw3;

import gay.oss.cw3.renderer.*;
import gay.oss.cw3.renderer.objects.Material;
import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.renderer.objects.Model;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.Shader;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.renderer.simulation.MeshUtil;
import gay.oss.cw3.simulation.world.Map;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Main {
    private Window window;
    private Model model;

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

        var program = ShaderProgram.create(new Shader[] { vertexShader, fragShader });

        // Create a square
        float vertex[] = {
            -0.8f, -0.8f, 0.0f, // BL
            0.8f, -0.8f, 0.0f, // BR
            0.8f, 0.8f, 0.0f, // TR

            -0.8f, -0.8f, 0.0f, // BL
            0.8f, 0.8f, 0.0f, // TR
            -0.8f, 0.8f, 0.0f, // TL
        };

        float[] uv = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
        };

        var mesh = Mesh.builder()
            .vertex(vertex)
            .render(uv, 2)
            .build();
        
        var material = new Material(program, new Texture(Main.class.getResourceAsStream("/textures/amogus.png")));

        model = new Model(mesh, material);

        var map = new Map(64, 64);
        map.generate();
        var mesh2 = MeshUtil.generateMeshFromHeightmap(map.getHeightMap());
        model = new Model(mesh2, material);
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

        // Setup camera projection
        Matrix4f viewProjection = new Matrix4f()
            .perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 1000.0f)
            .lookAt(-32.0f, 80.0f, 32.0f,
                    32.0f, 0.0f, 32.0f,
                    0.0f, 1.0f, 0.0f);
            
        // Draw model
        this.model.draw(viewProjection);

        // Swap framebuffers.
        window.swap();
    }

    public static void main(String[] args) {
        var instance = new Main();

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
