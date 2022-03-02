package gay.oss.cw3.renderer.objects;

import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.simulation.world.Map;

/**
 * Class with a set of utilities for generating different types of meshes.
 * 
 * @author Pawel Makles (K21002534)
 * @author William Bradford Larcombe (K21003008)
 */
public class MeshUtil {
    /**
     * Generate an indexed subdivided plane.
     * @param width Width Units
     * @param depth Depth Units
     * @param center Whether the plane should be centred
     * @param subdivide Number of subdivisions to perform
     * @return New Mesh
     */
    public static Mesh makeIndexedPlane(float width, float depth, boolean center, int subdivide) {
        float x0 = center ? - width / 2 : 0;
        float z0 = center ? - depth / 2 : 0;

        int length = (int) Math.pow(2, subdivide);
        float w = width / length;
        float d = depth / length;
        float u = 1.0f / length;

        int verticesCount = length * length;

        // 1st pass. generate vertex data
        float[] vertex = new float[verticesCount * 3];
        float[] uv = new float[verticesCount * 2];

        for (int x=0;x<length;x++) {
            for (int z=0;z<length;z++) {
                int offset = (z * length + x) * 3;
                vertex[offset    ] = x0 + w * x;
                vertex[offset + 1] = 0;
                vertex[offset + 2] = z0 + d * z;

                int offsetUV = (z * length + x) * 2;
                uv[offsetUV    ] = u * x;
                uv[offsetUV + 1] = u * z;
            }
        }

        // 2nd pass. generate element array buffer
        int[] indices = new int[verticesCount * 6];
        for (int x=0;x<length-1;x++) {
            for (int z=0;z<length-1;z++) {
                int offset = (z * length + x) * 6;                
                indices[offset    ] = z * length + x;
                indices[offset + 1] = z * length + x + 1;
                indices[offset + 2] = (z + 1) * length + x + 1;
                indices[offset + 3] = z * length + x;
                indices[offset + 4] = (z + 1) * length + x + 1;
                indices[offset + 5] = (z + 1) * length + x;
            }
        }

        return Mesh.builder()
            .vertex(vertex)
            .render(uv, 2)
            .indices(indices)
            .build();
    }

    /**
     * Generate a new non-indexed subdivided plane.
     * Prefer to use {@link MeshUtil#makeIndexedPlane}.
     * @param width Width Units
     * @param depth Depth Units
     * @param center Whether the plane should be centred
     * @param subdivide Number of subdivisions to perform
     * @return New Mesh
     */
    public static Mesh makePlane(float width, float depth, boolean center, int subdivide) {
        float x0 = center ? - width / 2 : 0;
        float z0 = center ? - depth / 2 : 0;

        int length = (int) Math.pow(2, subdivide);
        float w = width / length;
        float d = depth / length;
        float u = 1.0f / length;

        int squares = (int) Math.pow(4, subdivide);
        int triangles = squares * 2;

        float[] vertices = new float[triangles * 3 * 3];
        float[] uv = new float[triangles * 3 * 2];
        for (int x=0;x<length;x++) {
            for (int z=0;z<length;z++) {
                int offset = (x * length + z) * 18;
                int offsetUV = (x * length + z) * 12;

                float minUVx = u * x;
                float maxUVx = u * (x + 1);
                float minUVy = u * z;
                float maxUVy = u * (z + 1);

                vertices[offset + 0 ] = x0 + w * x;
                vertices[offset + 1 ] = 0;
                vertices[offset + 2 ] = z0 + d * z;
                vertices[offset + 3 ] = x0 + w * (x + 1);
                vertices[offset + 4 ] = 0;
                vertices[offset + 5 ] = z0 + d * z;
                vertices[offset + 6 ] = x0 + w * (x + 1);
                vertices[offset + 7 ] = 0;
                vertices[offset + 8 ] = z0 + d * (z + 1);

                uv[offsetUV + 0 ] = minUVx;
                uv[offsetUV + 1 ] = minUVy;
                uv[offsetUV + 2 ] = maxUVx;
                uv[offsetUV + 3 ] = minUVy;
                uv[offsetUV + 4 ] = maxUVx;
                uv[offsetUV + 5 ] = maxUVy;

                vertices[offset + 9 ] = x0 + w * x;
                vertices[offset + 10] = 0;
                vertices[offset + 11] = z0 + d * z;
                vertices[offset + 12] = x0 + w * (x + 1);
                vertices[offset + 13] = 0;
                vertices[offset + 14] = z0 + d * (z + 1);
                vertices[offset + 15] = x0 + w * x;
                vertices[offset + 16] = 0;
                vertices[offset + 17] = z0 + d * (z + 1);

                uv[offsetUV + 6 ] = minUVx;
                uv[offsetUV + 7 ] = minUVy;
                uv[offsetUV + 8 ] = maxUVx;
                uv[offsetUV + 9 ] = maxUVy;
                uv[offsetUV + 10] = minUVx;
                uv[offsetUV + 11] = maxUVy;
            }
        }

        return Mesh.builder()
            .vertex(vertices)
            .render(uv, 2)
            .build();
    }

    /**
     * Generate a new non-indexed cube mesh.
     * @param width Width Units
     * @param height Height Units
     * @param depth Depth Units
     * @param center Whether the cube should be centred
     * @return New Mesh
     */
    public static Mesh makeCube(float width, float height, float depth, boolean center) {
        float x0 = center ? (- width / 2) : 0;
        float y0 = center ? (- height / 2) : 0;
        float z0 = center ? (- depth / 2) : 0;
        float x1 = center ? (width / 2) : width;
        float y1 = center ? (height / 2) : height;
        float z1 = center ? (depth / 2) : depth;

        float[] vertices = new float[] {
            // Z positive
            x1, y0, z1,
            x0, y1, z1,
            x0, y0, z1,
            x1, y0, z1,
            x1, y1, z1,
            x0, y1, z1,

            // Z negative
            x1, y0, z0,
            x0, y0, z0,
            x0, y1, z0,
            x1, y0, z0,
            x0, y1, z0,
            x1, y1, z0,

            // X positive
            x1, y0, z1,
            x1, y0, z0,
            x1, y1, z0,
            x1, y0, z1,
            x1, y1, z0,
            x1, y1, z1,

            // X negative
            x0, y0, z1,
            x0, y1, z0,
            x0, y0, z0,
            x0, y0, z1,
            x0, y1, z1,
            x0, y1, z0,

            // Y positive
            x1, y1, z0,
            x0, y1, z0,
            x0, y1, z1,
            x1, y1, z0,
            x0, y1, z1,
            x1, y1, z1,

            // Y negative
            x1, y0, z0,
            x0, y0, z1,
            x0, y0, z0,
            x1, y0, z0,
            x1, y0, z1,
            x0, y0, z1,
        };

        float[] uv = new float[] {
            // Z positive
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,

            // Z negative
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,

            // X positive
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,

            // X negative
            1.0f, 0.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,

            // Y positive
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,

            // Y negative
            0.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f
        };

        return Mesh.builder()
            .vertex(vertices)
            .render(uv, 2)
            .generateNormalsForTriangles()
            .build();
    }

    /**
     * Generate an indexed mesh with normals from a {@link Map}.
     * @param map Given Map
     * @return New Mesh
     */
    public static Mesh generateIndexedMeshFromMap(Map map) {
        int width = map.getWidth();
        int depth = map.getDepth();

        // 1st pass. generate position and colour vertex information
        float[] vertices = new float[(width) * (depth) * 3];
        float[] colour = new float[(width) * (depth) * 3];
        float[] uv = new float[(width) * (depth) * 2];
        for (int x=0;x<width;x++) {
            for (int z=0;z<depth;z++) {
                int offset = 3 * (z * width + x);
                vertices[offset    ] = x;
                vertices[offset + 1] = map.getHeight(x, z);
                vertices[offset + 2] = z;

                float[] c = map.getAverageBiomeColour(x, z);
                colour[offset    ] = c[0];
                colour[offset + 1] = c[1];
                colour[offset + 2] = c[2];

                int offsetUV = 2 * (z * width + x);
                uv[offsetUV    ] = (float) x / (float) width;
                uv[offsetUV + 1] = (float) z / (float) depth;
            }
        }

        // 2nd pass. generate normal vertex information
        float[] normal = new float[(width) * (depth) * 3];
        for (int x=0;x<width;x++) {
            for (int z=0;z<depth;z++) {
                int offset = 3 * (z * width + x);

                try {
                    int x0 = 3 * (z * width + x - 1);
                    int x1 = 3 * (z * width + x);
                    int z1 = 3 * ((z + 1) * width + x);

                    float[] n = Util.calculateNormal(
                        vertices[x1], vertices[x1 + 1], vertices[x1 + 2],
                        vertices[x0], vertices[x0 + 1], vertices[x0 + 2],
                        vertices[z1], vertices[z1 + 1], vertices[z1 + 2]
                    );

                    normal[offset    ] = n[0];
                    normal[offset + 1] = n[1];
                    normal[offset + 2] = n[2];
                } catch (Exception e) {}
            }
        }

        // 3rd pass. generate element buffer object
        int[] indices = new int[(width) * (depth) * 6];
        for (int x=0;x<width-1;x++) {
            for (int z=0;z<depth-1;z++) {
                int offset = 6 * (z * width + x);
                indices[offset    ] = z * width + x;
                indices[offset + 1] = z * width + x + 1;
                indices[offset + 2] = (z + 1) * width + x + 1;
                indices[offset + 3] = z * width + x;
                indices[offset + 4] = (z + 1) * width + x + 1;
                indices[offset + 5] = (z + 1) * width + x;
            }
        }

        var mesh = Mesh.builder()
            .vertex(vertices)
            .render(colour, 3)
            .normal(normal)
            .indices(indices)
            .build();

        mesh.bindArray(3, 2, uv);
        return mesh;
    }

    /**
     * Generate a non-indexed mesh with per-face normals for a given {@link Map}.
     * Prefer to use {@link MeshUtil#generateIndexedMeshFromMap}.
     * @param map Given Map
     * @return New Mesh
     */
    public static Mesh generateMeshFromMap(Map map) {
        int width = map.getWidth();
        int depth = map.getDepth();

        float[] vertices = new float[(width) * (depth) * 36];
        float[] colour = new float[(width) * (depth) * 36];
        for (int x=0;x<width-1;x++) {
            for (int z=0;z<depth-1;z++) {
                float x0z0 = map.getHeight(x, z);
                float x1z0 = map.getHeight(x + 1, z);
                float x0z1 = map.getHeight(x, z + 1);
                float x1z1 = map.getHeight(x + 1, z + 1);

                float[] c_x0z0 = map.getAverageBiomeColour(x, z);
                float[] c_x1z0 = map.getAverageBiomeColour(x + 1, z);
                float[] c_x0z1 = map.getAverageBiomeColour(x, z + 1);
                float[] c_x1z1 = map.getAverageBiomeColour(x + 1, z + 1);
                float[] c_blend = new float[] {
                    (c_x0z0[0] + c_x1z0[0] + c_x0z1[0] + c_x1z1[0]) / 4,
                    (c_x0z0[1] + c_x1z0[1] + c_x0z1[1] + c_x1z1[1]) / 4,
                    (c_x0z0[2] + c_x1z0[2] + c_x0z1[2] + c_x1z1[2]) / 4
                };

                int offset = 36 * ((z * width) + x);

                float cx = x + 0.5f;
                float cy = (x0z0 + x1z0 + x0z1 + x1z1) / 4;
                float cz = z + 0.5f;

                // Bottom Triangle
                vertices[offset + 0 ] = x;
                vertices[offset + 1 ] = x0z0;
                vertices[offset + 2 ] = z;
                vertices[offset + 3 ] = x;
                vertices[offset + 4 ] = x0z1;
                vertices[offset + 5 ] = z + 1;
                vertices[offset + 6 ] = cx;
                vertices[offset + 7 ] = cy;
                vertices[offset + 8 ] = cz;

                colour[offset + 0 ] = c_x0z0[0];
                colour[offset + 1 ] = c_x0z0[1];
                colour[offset + 2 ] = c_x0z0[2];
                colour[offset + 3 ] = c_x0z1[0];
                colour[offset + 4 ] = c_x0z1[1];
                colour[offset + 5 ] = c_x0z1[2];
                colour[offset + 6 ] = c_blend[0];
                colour[offset + 7 ] = c_blend[1];
                colour[offset + 8 ] = c_blend[2];

                // Left Triangle
                vertices[offset + 9 ] = x;
                vertices[offset + 10] = x0z0;
                vertices[offset + 11] = z;
                vertices[offset + 12] = cx;
                vertices[offset + 13] = cy;
                vertices[offset + 14] = cz;
                vertices[offset + 15] = x + 1;
                vertices[offset + 16] = x1z0;
                vertices[offset + 17] = z;

                colour[offset + 9 ] = c_x0z0[0];
                colour[offset + 10] = c_x0z0[1];
                colour[offset + 11] = c_x0z0[2];
                colour[offset + 12] = c_blend[0];
                colour[offset + 13] = c_blend[1];
                colour[offset + 14] = c_blend[2];
                colour[offset + 15] = c_x1z0[0];
                colour[offset + 16] = c_x1z0[1];
                colour[offset + 17] = c_x1z0[2];

                // Right Triangle
                vertices[offset + 18] = x;
                vertices[offset + 19] = x0z1;
                vertices[offset + 20] = z + 1;
                vertices[offset + 21] = x + 1;
                vertices[offset + 22] = x1z1;
                vertices[offset + 23] = z + 1;
                vertices[offset + 24] = cx;
                vertices[offset + 25] = cy;
                vertices[offset + 26] = cz;

                colour[offset + 18] = c_x0z1[0];
                colour[offset + 19] = c_x0z1[1];
                colour[offset + 20] = c_x0z1[2];
                colour[offset + 21] = c_x1z1[0];
                colour[offset + 22] = c_x1z1[1];
                colour[offset + 23] = c_x1z1[2];
                colour[offset + 24] = c_blend[0];
                colour[offset + 25] = c_blend[1];
                colour[offset + 26] = c_blend[2];

                // Top Triangle
                vertices[offset + 27] = cx;
                vertices[offset + 28] = cy;
                vertices[offset + 29] = cz;
                vertices[offset + 30] = x + 1;
                vertices[offset + 31] = x1z1;
                vertices[offset + 32] = z + 1;
                vertices[offset + 33] = x + 1;
                vertices[offset + 34] = x1z0;
                vertices[offset + 35] = z;

                colour[offset + 27] = c_blend[0];
                colour[offset + 28] = c_blend[1];
                colour[offset + 29] = c_blend[2];
                colour[offset + 30] = c_x1z1[0];
                colour[offset + 31] = c_x1z1[1];
                colour[offset + 32] = c_x1z1[2];
                colour[offset + 33] = c_x1z0[0];
                colour[offset + 34] = c_x1z0[1];
                colour[offset + 35] = c_x1z0[2];
            }
        }

        return Mesh.builder()
            .vertex(vertices)
            .render(colour, 3)
            .generateNormalsForTriangles()
            .build();
    }
}
