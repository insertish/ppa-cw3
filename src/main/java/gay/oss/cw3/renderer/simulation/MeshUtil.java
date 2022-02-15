package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.simulation.world.Map;

public class MeshUtil {
    public static final float HEIGHT_SCALE = 40.0f;

    public static Mesh generateMeshFromMap(Map map) {
        int width = map.getWidth();
        int depth = map.getDepth();

        float[] vertices = new float[(width) * (depth) * 36];
        float[] colour = new float[(width) * (depth) * 36];
        for (int x=0;x<width-1;x++) {
            for (int z=0;z<depth-1;z++) {
                float x0z0 = map.getHeight(x, z) * HEIGHT_SCALE;
                float x1z0 = map.getHeight(x + 1, z) * HEIGHT_SCALE;
                float x0z1 = map.getHeight(x, z + 1) * HEIGHT_SCALE;
                float x1z1 = map.getHeight(x + 1, z + 1) * HEIGHT_SCALE;

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
