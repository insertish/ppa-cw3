package gay.oss.cw3.renderer.objects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STREAM_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import gay.oss.cw3.renderer.Util;

/**
 * Wrapper class around an OpenGL Vertex Array Object
 * which includes Vertex Buffer Objects and other related information.
 */
public class Mesh {
    private static int CURRENT_VAO = 0;

    private final int vao;
    private final int vertices;

    // ! FIXME: FIXME
    public boolean indexed;
    public int triangles;
    public int indicesCount;
    
    private final List<Integer> vbo;

    /**
     * Construct a new Mesh
     * @param vao Vertex Array Object ID
     * @param vertices Number of vertices in this mesh
     */
    private Mesh(int vao, int vertices) {
        this.vao = vao;
        this.vertices = vertices;
        this.indexed = false;

        this.vbo = new ArrayList<>();
    }

    /**
     * Bind Vertex Array Object for this mesh.
     */
    public void bind() {
        if (CURRENT_VAO == this.vao) return;
        glBindVertexArray(this.vao);
        CURRENT_VAO = this.vao;
    }

    public void destroy() {
        glDeleteVertexArrays(this.vao);
        
        for (int vbo : this.vbo) {
            glDeleteBuffers(vbo);
        }
    }

    /**
     * Unbind Vertex Array Object for this mesh.
     */
    public static void unbind() {
        glBindVertexArray(0);
        CURRENT_VAO = 0;
    }

    /**
     * Draw this mesh using provided attributes.
     */
    public void draw() {
        this.bind();

        if (this.indexed) {
            glDrawElements(GL_TRIANGLES, this.indicesCount, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, this.vertices);
        }
    }

    /**
     * Draw multiple instances of this mesh.
     */
    public void drawInstanced(int count) {
        this.bind();

        if (this.indexed) {
            glDrawElementsInstanced(GL_TRIANGLES, this.indicesCount, GL_UNSIGNED_INT, 0, count);
        } else {
            throw new IllegalStateException("Not indexed!");
        }
    }

    /**
     * Simultaneously bind attribute and associated array
     * @param attribute Attribute ID, used as the layout position in the GLSL shader code.
     * @param components Number of components per element in the array.
     * @param data Float data array.
     * @return Vertex Buffer Object ID
     */
    public int bindArray(int attribute, int components, float[] data) {
        this.bind();

        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glEnableVertexAttribArray(attribute);
        glVertexAttribPointer(attribute, components, GL_FLOAT, false, 0, 0);

        this.vbo.add(vbo);
        return vbo;
    }

    /**
     * Construct a new Mesh using data given by the {@link Builder}
     * @param builder Builder with mesh data
     * @return Newly constructed {@link Mesh}
     * @throws IllegalStateException if no vertices were specified
     */
    public static Mesh from(Builder builder) throws IllegalStateException {
        if (builder.vertex == null)
            throw new IllegalStateException("Must specify vertices.");

        int vao = glGenVertexArrays();
        final var mesh = new Mesh(vao, builder.vertices);

        mesh.bind();
        mesh.bindArray(0, 3, builder.vertex);

        if (builder.render != null) {
            mesh.bindArray(1, builder.renderComponents, builder.render);
        }

        if (builder.normal != null) {
            mesh.bindArray(2, 3, builder.normal);
        }

        if (builder.indices != null) {
            int vbo = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vbo);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, builder.indices, GL_STATIC_DRAW);
            mesh.indexed = true;
            mesh.triangles = builder.indices.length / 3;
            mesh.indicesCount = builder.indices.length;
            // add vbo
        }

        Mesh.unbind();
        return mesh;
    }

    /**
     * Load a mesh from an .obj InputStream into a builder
     * @return Mesh Builder
     */
    public static Builder loadObj(InputStream inputStream) throws IOException {
        Obj obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));
        Builder builder = Mesh.builder();

        builder.indices(ObjData.getFaceVertexIndicesArray(obj));
        builder.vertex(ObjData.getVerticesArray(obj));
        builder.render(ObjData.getTexCoordsArray(obj, 2), 2);
        builder.normal(ObjData.getNormalsArray(obj));

        return builder;
    }

    /**
     * Load a pre-defined .obj mesh into a builder
     * @return Mesh Builder
     */
    public static Builder loadObjFromResource(String resource) throws IOException {
        return Mesh.loadObj(Mesh.class.getResourceAsStream("/models/" + resource + ".obj"));
    }

    /**
     * Create a new {@link Builder} for {@link Mesh}
     * @return Mesh Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing meshes from their constituent parts
     */
    public static class Builder {
        private float[] vertex;
        private int vertices;

        private float[] render;
        private int renderComponents;

        private float[] normal;
        private int[] indices;

        /**
         * Specify a vertex array
         * @param vertex Vertex array
         */
        public Builder vertex(float vertex[]) {
            this.vertex = vertex;
            this.vertices = vertex.length / 3;
            return this;
        }

        /**
         * Specify a "render" array, could be colour or UV data.
         * @param render Render array
         * @param components Components used in this array. UV = 2, RGB = 3, RGBA = 4
         */
        public Builder render(float[] render, int components) {
            this.render = render;
            this.renderComponents = components;
            return this;
        }

        /**
         * Specify a normal array
         * @param normal Normal array
         */
        public Builder normal(float normal[]) {
            this.normal = normal;
            return this;
        }

        /**
         * Specify an element buffer array
         * @param indicies Element buffer array
         */
        public Builder indices(int indices[]) {
            this.indices = indices;
            return this;
        }

        /**
         * Generate normals for the given mesh data.
         */
        public Builder generateNormalsForTriangles() {
            if (this.vertices % 3 != 0)
                throw new IllegalStateException("Not a triangle!");

            float normals[] = new float[this.vertices * 3];
            float vert[] = this.vertex;
            
            for (int triangle=0;triangle<this.vertices/3;triangle++) {
                int offset = triangle * 9;

                float[] n = Util.calculateNormal(
                    vert[offset  ], vert[offset+1], vert[offset+2],
                    vert[offset+3], vert[offset+4], vert[offset+5],
                    vert[offset+6], vert[offset+7], vert[offset+8]
                );

                for (int vertex=0;vertex<3;vertex++) {
                    normals[offset + vertex * 3] = n[0];
                    normals[offset + vertex * 3 + 1] = n[1];
                    normals[offset + vertex * 3 + 2] = n[2];
                }
            }

            return this.normal(normals);
        }

        /**
         * Consturct a new {@link Mesh} from the data provided in this builder
         * @return Newly constructed {@link Mesh}
         * @throws IllegalStateException if no vertices were specified
         */
        public Mesh build() throws IllegalStateException {
            return Mesh.from(this);
        }
    }
}
