package gay.oss.cw3.renderer.ui.fonts;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector4f;

import gay.oss.cw3.renderer.Resources;
import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;
import gay.oss.cw3.renderer.ui.UI;

/**
 * Wrapper around a Texture and ShaderProgram for rendering texture atlas based fonts.
 */
public class Font {
    private ShaderProgram shader;
    private Texture fontTexture;
    private int atlas;

    private Map<Character, int[]> mapping;

    /**
     * Construct a new Font for rendering
     * @param fontPath Path to the Texture atlas
     * @param atlas The grid divisions on both axis
     * @param characterMap Character map for resolving characters to coordinates
     * @throws Exception if we fail to load one or more resources
     */
    public Font(String fontPath, int atlas, String characterMap) throws Exception {
        this.shader = ShaderProgram.fromName("ui/text");
        this.fontTexture = Resources.getTexture(fontPath);
        this.atlas = atlas;

        this.mapping = new HashMap<>();
        for (int i=0;i<characterMap.length();i++) {
            char c = characterMap.charAt(i);

            // Pre-compute the mappings from characters to coordinates.
            int x = i % atlas;
            int y = i / atlas;
            mapping.put(c, new int[] { x, y });
        }
    }

    /**
     * Draw text using a given UI helper and text properties.
     * @param ui UI helper
     * @param x X position
     * @param y Y position
     * @param size Size of each character (width and height)
     * @param text Text to draw
     * @param colour RGBA colour space vector
     */
    public void drawText(UI ui, int x, int y, int size, String text, @Nullable Vector4f colour) {
        // Configure the shader, upload atlas divisions.
        this.shader.use();
        this.shader.setUniform("atlas", this.atlas);

        // Upload colour, or white if not specified.
        Vector4f c = colour == null ? new Vector4f(1, 1, 1, 1) : colour;
        this.shader.setUniform("colour", c);
        
        // Bind the texture for rendering.
        this.fontTexture.bind();
        
        // Render each character individually.
        for (int i=0;i<text.length();i++) {
            int[] entry = this.mapping.get(text.charAt(i));
            if (entry != null) {
                this.shader.setUniform("atlasX", entry[0]);
                this.shader.setUniform("atlasY", entry[1]);

                ui.upload(x + size * i, y, size, size);
                ui.getSquareMesh().draw();
            }
        }
    }
}
