package gay.oss.cw3.renderer.ui.fonts;

/**
 * Retro Font.
 * Available in Public Domain from https://opengameart.org/content/8x8-font-chomps-wacky-worlds-beta.
 */
public class FontRetro extends Font {
    /**
     * Construct FontRetro
     * @throws Exception if one or more resources fail to load
     */
    public FontRetro() throws Exception {
        super("ui/font.png", 8, "abcdefghijklmnopqrstuvwxyz0123456789-*!@");
    }
}
