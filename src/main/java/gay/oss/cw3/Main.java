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

public class Main {
    private Window window;
    
    private Material terrainMaterial;
    private Model model;
    private Map map;

    private Model amongUsModel;

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

        // Create a spinning among us square
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
        
        var material = new Material(
            ShaderProgram.fromName("texturedObject"),
            new Texture(Main.class.getResourceAsStream("/textures/amogus.png"))
        );

        amongUsModel = new Model(mesh, material);
        amongUsModel.getTransformation()
            .translation(32.0f, 15.0f, 32.0f);
        
        // Generate the terrain
        var terrainProgram = ShaderProgram.fromName("terrain");
        this.terrainMaterial = new Material(terrainProgram);
        this.generateMap();
    }
    
    private void generateMap() {
        this.map = new Map(64, 64);
        map.generate();
        if (model != null) model.destroyMesh();
        var mesh = MeshUtil.generateMeshFromHeightmap(map.getHeightMap());
        model = new Model(mesh, terrainMaterial);
    }

    private void onKeyPress(int key, int modifiers) {
        if (key == GLFW_KEY_ESCAPE) {
            // should quit render loop and clean up
            System.exit(0);
        }
    }

    private static int i = 0;

    private void renderLoop() {
        // Clear the framebuffer.
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();

        // Setup camera projection
        Matrix4f viewProjection = new Matrix4f()
            .perspective((float) Math.toRadians(45.0f), 1.0f, 0.01f, 1000.0f)
            .lookAt(-32.0f, 60.0f, 32.0f,
                    32.0f, 0.0f, 32.0f,
                    0.0f, 1.0f, 0.0f);
        
        // Rotate Among Us
        amongUsModel.getTransformation()
            .rotate(1, 0, 1, 0);
            
        /*i += 1;
        if (i > 10) {
            i = 0;
            this.generateMesh();
        }*/

        // Draw model
        this.model.draw(viewProjection);

        // Draw Among Us
        this.amongUsModel.draw(viewProjection);

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
