package gay.oss.cw3.renderer.simulation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.renderer.ui.fonts.Font;

/**
 * Helper class for loading different types of resources.
 */
public class Resources {
    /**
     * Static map of known shader programs.
     */
    private static Map<String, ShaderProgram> shaders = new HashMap<>();

    /**
     * Static map of known textures.
     */
    private static Map<String, Texture> textures = new HashMap<>();

    /**
     * Load a specific shader from resources.
     * @param path Shader name
     * @return Shader Program
     * @throws Exception if we fail to load the shader resource
     */
    public static ShaderProgram getShader(String path) throws Exception {
        if (shaders.containsKey(path)) return shaders.get(path);
        try {
            var program = ShaderProgram.fromName(path);
            shaders.put(path, program);
            return program;
        } catch (Exception e) {
            System.err.println("Failed to load shader: " + path);
            throw e;
        }
    }

    /**
     * Load a specific texture from resources.
     * @param texture Texture name
     * @return Texture
     * @throws IOException if we fail to load the texture resource
     */
    public static Texture getTexture(String path) throws IOException {
        if (textures.containsKey(path)) return textures.get(path);
        try {
            var texture = Texture.fromResource(path);
            textures.put(path, texture);
            return texture;
        } catch (Exception e) {
            System.err.println("Failed to load texture: " + path);
            throw e;
        }
    }
}
