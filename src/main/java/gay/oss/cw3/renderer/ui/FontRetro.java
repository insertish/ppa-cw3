package gay.oss.cw3.renderer.ui;

public class FontRetro extends Font {
    public FontRetro() throws Exception {
        super("ui/font-512x.png", 8);
    }

    @Override
    protected int[] selectChar(char c) {
        switch ((""+c).toLowerCase().charAt(0)) {
            case 'a': return new int[] { 0, 0 };
            case 'b': return new int[] { 1, 0 };
            case 'c': return new int[] { 2, 0 };
            case 'd': return new int[] { 3, 0 };
            case 'e': return new int[] { 4, 0 };
            case 'f': return new int[] { 5, 0 };
            case 'g': return new int[] { 6, 0 };
            case 'h': return new int[] { 7, 0 };
            case 'i': return new int[] { 0, 1 };
            case 'j': return new int[] { 1, 1 };
            case 'k': return new int[] { 2, 1 };
            case 'l': return new int[] { 3, 1 };
            case 'm': return new int[] { 4, 1 };
            case 'n': return new int[] { 5, 1 };
            case 'o': return new int[] { 6, 1 };
            case 'p': return new int[] { 7, 1 };
            case 'q': return new int[] { 0, 2 };
            case 'r': return new int[] { 1, 2 };
            case 's': return new int[] { 2, 2 };
            case 't': return new int[] { 3, 2 };
            case 'u': return new int[] { 4, 2 };
            case 'v': return new int[] { 5, 2 };
            case 'w': return new int[] { 6, 2 };
            case 'x': return new int[] { 7, 2 };
            case 'y': return new int[] { 0, 3 };
            case 'z': return new int[] { 1, 3 };
            case '0': return new int[] { 2, 3 };
            case '1': return new int[] { 3, 3 };
            case '2': return new int[] { 4, 3 };
            case '3': return new int[] { 5, 3 };
            case '4': return new int[] { 6, 3 };
            case '5': return new int[] { 7, 3 };
            case '6': return new int[] { 0, 4 };
            case '7': return new int[] { 1, 4 };
            case '8': return new int[] { 2, 4 };
            case '9': return new int[] { 3, 4 };
            case '-': return new int[] { 4, 4 };
            case '*': return new int[] { 5, 4 };
            case '!': return new int[] { 6, 4 };
            case '@': return new int[] { 7, 4 };
            default:  return new int[] { 0, 5 };
        }
    }
}
