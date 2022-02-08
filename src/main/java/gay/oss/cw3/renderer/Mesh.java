package gay.oss.cw3.renderer;

import static org.lwjgl.opengl.GL30.*;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private final int vao;
    private final int indices;
    
    private List<Integer> vbo;

    private Mesh(int vao, int indices) {
        this.vao = vao;
        this.indices = indices;

        this.vbo = new ArrayList<>();
    }

    public void bind() {
        glBindVertexArray(this.vao);
    }

    public static void unbind() {
        glBindVertexArray(0);
    }

    public void draw() {
        this.bind();
        glDrawArrays(GL_TRIANGLES, 0, this.indices);
    }

    private int bindArray(int attribute, int components, float data[]) {
        int vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glEnableVertexAttribArray(attribute);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glVertexAttribPointer(attribute, components, GL_FLOAT, false, 0, 0);

        this.vbo.add(vbo);
        return vbo;
    }

    public static Mesh from(Builder builder) throws Exception {
        if (builder.vertex == null)
            throw new Exception("Must specify vertices.");

        int vao = glGenVertexArrays();
        final var mesh = new Mesh(vao, builder.indices);

        mesh.bind();
        mesh.bindArray(0, 3, builder.vertex);

        if (builder.render != null) {
            mesh.bindArray(1, builder.renderComponents, builder.render);
        }

        Mesh.unbind();
        return mesh;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private float vertex[];
        private int indices;

        private float render[];
        private int renderComponents;

        public Builder vertex(float vertex[]) {
            this.vertex = vertex;
            this.indices = vertex.length / 3;
            return this;
        }

        /**
         * 
         * @param render
         * @param components Components used in this array. UV = 2, RGB = 3, RGBA = 4
         * @return
         */
        public Builder render(float render[], int components) {
            this.render = render;
            this.renderComponents = components;
            return this;
        }

        public Mesh build() throws Exception {
            return Mesh.from(this);
        }
    }
}