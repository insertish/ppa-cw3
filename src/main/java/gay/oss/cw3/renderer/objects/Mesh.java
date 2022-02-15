package gay.oss.cw3.renderer.objects;

import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.joml.Vector3f;

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
     * This does not support indexed rendering.
     */
    public void draw() {
        this.bind();

        if (this.indexed) {
            glDrawElements(GL_TRIANGLES, this.vertices * 6, GL_UNSIGNED_INT, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, this.vertices);
        }
    }

    /**
     * Simultaneously bind attribute and associated array
     * @param attribute Attribute ID, used as the layout position in the GLSL shader code.
     * @param components Number of components per element in the array.
     * @param data Float data array.
     * @return Vertex Buffer Object ID
     */
    private int bindArray(int attribute, int components, float[] data) {
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glEnableVertexAttribArray(attribute);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
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
            // add vbo
        }

        Mesh.unbind();
        return mesh;
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
