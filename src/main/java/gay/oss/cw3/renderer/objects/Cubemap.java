package gay.oss.cw3.renderer.objects;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP;
import static org.lwjgl.opengl.GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

/**
 * A representation of a texture used for OpenGL.
 */
public class Cubemap {
    private static Cubemap CURRENT_CUBEMAP;

    private final int id;

    private static final String[] FACES = new String[] {
        "front",
        "back",
        "top",
        "bottom",
        "right",
        "left",
    };

    /**
     * Creates a cubemap from a given resource path.
     *
     * @param path Location of the cubemap
     * @throws IOException if there was an error reading the texture data
     */
    public Cubemap(final String path, final String ext) throws IOException {
        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_CUBE_MAP, id);

        STBImage.stbi_set_flip_vertically_on_load(true);

        // Load all cubemap texture faces.
        int i = 0;
        for (String face : FACES) {
            final InputStream dataStream = Texture.class.getResourceAsStream("/cubemaps/" + path + "/" + face + "." + ext);
            final byte[] data = dataStream.readAllBytes(); // throws IOException
            final ByteBuffer nativeDataBuf = MemoryUtil.memAlloc(data.length);
            nativeDataBuf.put(data);
            nativeDataBuf.rewind();

            int[] width = new int[1];
            int[] height = new int[1];
            int[] channels = new int[1];

            ByteBuffer loadedTexture = STBImage.stbi_load_from_memory(nativeDataBuf, width, height, channels, 3);

            MemoryUtil.memFree(nativeDataBuf);

            // We use nearest neighbour scaling in all cases.
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
    
            // Upload texture data to the GPU.
            glTexImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X + (i++), 0, GL_RGB, width[0], height[0], 0, GL_RGB, GL_UNSIGNED_BYTE, loadedTexture);
        }
        
        STBImage.stbi_set_flip_vertically_on_load(false);
    }

    /**
     * Bind the texture.
     */
    public void bind() {
        if (CURRENT_CUBEMAP == this) return;
        glBindTexture(GL_TEXTURE_CUBE_MAP, this.id);
        CURRENT_CUBEMAP = this;
    }
}
