package gay.oss.cw3.renderer;

import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int id;
    private final int indices;

    private Mesh(int id, int indices) {
        this.id = id;
        this.indices = indices;
    }

    public void bind() {
        glBindVertexArray(this.id);
    }

    public static void unbind() {
        glBindVertexArray(0);
    }

    public void draw() {
        this.bind();
        glDrawArrays(GL_TRIANGLES, 0, this.indices);
    }

    public static Mesh from(Builder builder) throws Exception {
        if (builder.vertex == null)
            throw new Exception("Must specify vertices.");

        int vao = glGenVertexArrays();
        final var mesh = new Mesh(vao, builder.indices);
        mesh.bind();

        int vboVertex = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboVertex);
        glBufferData(GL_ARRAY_BUFFER, builder.vertex, GL_STATIC_DRAW);

        glEnableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, vboVertex);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

        Mesh.unbind();
        return mesh;
    }

    public static Builder build() {
        return new Builder();
    }

    public static class Builder {
        private float vertex[];
        private int indices;

        public Builder vertex(float vertex[]) {
            this.vertex = vertex;
            this.indices = vertex.length / 3;
            return this;
        }

        public Mesh build() throws Exception {
            return Mesh.from(this);
        }
    }
}
