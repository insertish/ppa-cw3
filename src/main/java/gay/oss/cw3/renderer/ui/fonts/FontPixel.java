package gay.oss.cw3.renderer.ui.fonts;

/**
 * Sprite Pixel Font
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class FontPixel extends Font {
    /**
     * Construct new FontPixel
     * @throws Exception if we fail to load one or more resources
     */
    public FontPixel() throws Exception {
        super("ui/spritefont-v2.png", 16, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890.,:;?!+-_=*/\\%><'\"()[]{}#$&@~");
    }
}
