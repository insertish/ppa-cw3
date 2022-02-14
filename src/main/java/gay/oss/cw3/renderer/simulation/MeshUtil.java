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

                int offset = 36 * ((z * width) + x);
                vertices[offset + 0] = x;
                vertices[offset + 1] = x0z0;
                vertices[offset + 2] = 0.0f + z;
                vertices[offset + 3] = x;
                vertices[offset + 4] = x1z0;
                vertices[offset + 5] = z + 1;
                vertices[offset + 6] = x + 0.5f;
                vertices[offset + 7] = (x0z0 + x1z0) / 2;
                vertices[offset + 8] = z + 0.5f;
            }
        }

        return Mesh.builder()
            .vertex(vertices)
            .build();
    }
}
