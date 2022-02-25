package gay.oss.cw3.renderer.ui;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.shaders.ShaderProgram;

public abstract class Font {
    private ShaderProgram shader;
    private Texture fontTexture;
    private int atlas;

    public Font(String fontPath, int atlas) throws Exception {
        this.shader = ShaderProgram.fromName("ui/text");
        this.fontTexture = Texture.fromResource(fontPath);
        this.atlas = atlas;
    }
    
    protected abstract int[] selectChar(char c);

    public void drawText(UI ui, int x, int y, int size, String text) {
        this.shader.use();
        this.shader.setUniform("atlas", this.atlas);
        
        this.fontTexture.bind();
        
        for (int i=0;i<text.length();i++) {
            int[] entry = this.selectChar(text.charAt(i));
            this.shader.setUniform("atlasX", entry[0]);
            this.shader.setUniform("atlasY", entry[1]);

            ui.upload(x + size * i, y, size, size);
            ui.getSquareMesh().draw();
        }
    }
}
