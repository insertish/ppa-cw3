package gay.oss.cw3.renderer;

import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * A representation of a texture used for OpenGL.
 */
public class Texture {
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
        final byte[] data = dataStream.readAllBytes(); // throws IOException
        final ByteBuffer nativeDataBuf = MemoryUtil.memAlloc(data.length);
        nativeDataBuf.put(data);
        nativeDataBuf.rewind();

        int[] width = new int[1];
        int[] height = new int[1];
        int[] channels = new int[1];

        STBImage.stbi_load_from_memory(nativeDataBuf, width, height, channels, 4);

        MemoryUtil.memFree(nativeDataBuf);

        this.width = width[0];
        this.height = height[0];

        this.id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, this.width, this.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, this.id);
    }

    /**
     * Bind the texture.
     */
    void bind() {
        glBindTexture(GL_TEXTURE_2D, this.id);
    }
}
