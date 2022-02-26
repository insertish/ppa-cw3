package gay.oss.cw3.renderer.ui;

import java.util.HashMap;
import java.util.Map;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;

public class Font {
    private ShaderProgram shader;
    private Texture fontTexture;
    private int atlas;

    private Map<Character, int[]> mapping;

    public Font(String fontPath, int atlas, String characterMap) throws Exception {
        this.shader = ShaderProgram.fromName("ui/text");
        this.fontTexture = Texture.fromResource(fontPath);
        this.atlas = atlas;

        this.mapping = new HashMap<>();
        for (int i=0;i<characterMap.length();i++) {
            char c = characterMap.charAt(i);
            int x = i % atlas;
            int y = i / atlas;
            mapping.put(c, new int[] { x, y });
        }
    }

    public void drawText(UI ui, int x, int y, int size, String text) {
        this.shader.use();
        this.shader.setUniform("atlas", this.atlas);
        
        this.fontTexture.bind();
        
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
