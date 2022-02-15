package gay.oss.cw3.simulation.world;

import java.util.Random;

import gay.oss.cw3.lib.FastNoiseLite;
import gay.oss.cw3.simulation.Grid;
import gay.oss.cw3.simulation.entity.Entity;

public class Map {
    private final int width;
    private final int depth;

    private final Grid<Entity> entities;
    private final Grid<Float> heightMap;
    private final Grid<BiomeType> biomeMap;

    public Map(int width, int depth) {
        this.width = width;
        this.depth = depth;

        this.entities = new Grid<>(width, depth);
        this.heightMap = new Grid<>(width, depth);
        this.biomeMap = new Grid<>(width, depth);
    }

    public boolean isInBounds(int x, int z) {
        return !(x < 0 || z < 0 || x >= width || z >= depth);
    }

    public int getWidth() {
        return this.width;
    }

    public int getDepth() {
        return this.depth;
    }

    public Grid<Entity> getEntities() {
        return this.entities;
    }

    public Grid<Float> getHeightMap() {
        // ! FIXME: deprecate this
        return this.heightMap;
    }

    public Grid<BiomeType> getBiomeMap() {
        // ! FIXME: deprecate this
        return this.biomeMap;
    }

    public float getHeight(int x, int z) {
        return this.heightMap.get(x, z);
    }

    public float[] getBiomeColour(int x, int z) {
        return this.biomeMap.get(x, z).getColour();
    }

    public float[] getAverageBiomeColour(int x, int z) {
        float r = 0, g = 0, b = 0, count = 0;
        final int RADIUS = 5;

        for (int X=x-RADIUS;X<x+RADIUS+1;X++) {
            for (int Z=z-RADIUS;Z<z+RADIUS+1;Z++) {
                BiomeType entry = this.biomeMap.get(X, Z);
                if (entry != null) {
                    count++;
                    float[] colours = entry.getColour();
                    r += colours[0];
                    g += colours[1];
                    b += colours[2];
                }
            }
        }

        return new float[] {
            r / count,
            g / count,
            b / count
        };
    }

    public void generate(int seed) {
        // 1. Generate the biomes
        FastNoiseLite biomeNoise = new FastNoiseLite(seed);
        biomeNoise.SetNoiseType(FastNoiseLite.NoiseType.Cellular);
        biomeNoise.SetFrequency(0.015f);
        biomeNoise.SetCellularDistanceFunction(FastNoiseLite.CellularDistanceFunction.Hybrid);
        biomeNoise.SetCellularReturnType(FastNoiseLite.CellularReturnType.CellValue);

        for (int x=0;x<this.width;x++) {
            for (int z=0;z<this.depth;z++) {
                float value = biomeNoise.GetNoise(x, z);

                BiomeType type = null;
                if (value > 0.6) {
                    type = BiomeType.Jungle;
                } else if (value > -0.4) {
                    type = BiomeType.Plains;
                } else {
                    type = BiomeType.Forest;
                }

                this.biomeMap.set(x, z, type);
            }
        }

        // 2. Generate a heightmap
        FastNoiseLite heightNoise = new FastNoiseLite(seed);
        heightNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2S);
        //heightNoise.SetFrequency(0.025f);
        heightNoise.SetFrequency(0.05f);
        heightNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        heightNoise.SetFractalOctaves(4);
        heightNoise.SetFractalLacunarity(0.60f);
        heightNoise.SetFractalGain(1.80f);
        heightNoise.SetFractalWeightedStrength(-0.40f);

        for (int x=0;x<this.width;x++) {
            for (int z=0;z<this.depth;z++) {
                this.heightMap.set(x, z, heightNoise.GetNoise(x, z));
            }
        }
    }

    public void generate() {
        this.generate(new Random().nextInt());
    }
}
