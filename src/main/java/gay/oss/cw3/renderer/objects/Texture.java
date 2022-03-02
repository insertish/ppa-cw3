package gay.oss.cw3.renderer.objects;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNPACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

/**
 * A representation of a texture used for OpenGL.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class Texture {
    private static Texture CURRENT_TEXTURE0;

    private final int id;
    private final int width;
    private final int height;

    /**
     * Creates a texture from data in a given input stream.
     *
     * @param dataStream the input stream of the texture data
     * @throws IOException if there was an error reading the texture data
     */
    public Texture(final InputStream dataStream) throws IOException {
        // Use STBImage to load the raw image data in.
        final byte[] data = dataStream.readAllBytes(); // throws IOException
        final ByteBuffer nativeDataBuf = MemoryUtil.memAlloc(data.length);
        nativeDataBuf.put(data);
        nativeDataBuf.rewind();

        int[] width = new int[1];
        int[] height = new int[1];
        int[] channels = new int[1];

        ByteBuffer loadedTexture = STBImage.stbi_load_from_memory(nativeDataBuf, width, height, channels, 4);

        MemoryUtil.memFree(nativeDataBuf);

        // Prepare the OpenGL texture.
        this.width = width[0];
        this.height = height[0];

        this.id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, id);

        // We use nearest neighbour scaling in all cases.
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Upload texture data to the GPU.
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, loadedTexture);
    }

    /**
     * Bind the texture.
     */
    public void bind() {
        if (CURRENT_TEXTURE0 == this) return;
        glBindTexture(GL_TEXTURE_2D, this.id);
        CURRENT_TEXTURE0 = this;
    }

    /**
     * Load a texture from a given resource name.
     * This will prefix /textures/ automatically.
     * @param resource Path to the resource
     * @returns Newly constructed {@link Texture}
     */
    public static Texture fromResource(String resource) throws IOException {
        return new Texture(Texture.class.getResourceAsStream("/textures/" + resource));
    }
}
