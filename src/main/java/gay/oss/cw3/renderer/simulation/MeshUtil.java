package gay.oss.cw3.renderer.simulation;

import gay.oss.cw3.renderer.objects.Mesh;
import gay.oss.cw3.simulation.Grid;

public class MeshUtil {
    public static Mesh generateMeshFromHeightmap(Grid<Float> heightMap) {
        int width = heightMap.getWidth();
        int depth = heightMap.getDepth();

        float[] vertices = new float[(width) * (depth) * 36];
        for (int x=0;x<width-1;x++) {
            for (int z=0;z<depth-1;z++) {
                float x0z0 = heightMap.get(x, z) * 10.0f;
                float x1z0 = heightMap.get(x + 1, z) * 10.0f;
                float x0z1 = heightMap.get(x, z + 1) * 10.0f;
                float x1z1 = heightMap.get(x + 1, z + 1) * 10.0f;

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

                // Right Triangle
                vertices[offset + 18] = x;
                vertices[offset + 19] = x0z1;
                vertices[offset + 20] = z + 1;
                vertices[offset + 21] = cx;
                vertices[offset + 22] = cy;
                vertices[offset + 23] = cz;
                vertices[offset + 24] = x + 1;
                vertices[offset + 25] = x1z1;
                vertices[offset + 26] = z + 1;

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
            }
        }

        return Mesh.builder()
            .vertex(vertices)
            .build();
    }
}
