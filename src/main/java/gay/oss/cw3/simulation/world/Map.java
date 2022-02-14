package gay.oss.cw3.simulation.world;

import java.util.Random;
import java.awt.Color;

import gay.oss.cw3.lib.FastNoiseLite;
import gay.oss.cw3.provided.Field;
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

    public Grid<Entity> getEntities() {
        return this.entities;
    }

    public float getHeight(int x, int z) {
        return this.heightMap.get(x, z);
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
        heightNoise.SetFrequency(0.025f);
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

    public static void main(String[] args) {
        int SIZE=256;

        var map = new Map(SIZE, SIZE);

        var view = new SimulatorViewButCursed(SIZE, SIZE);
        var f = new Field(SIZE, SIZE);

        view.setColor(BiomeType.Plains, new Color(0, 255, 0));
        view.setColor(BiomeType.Forest, new Color(0, 255, 255));
        view.setColor(BiomeType.Jungle, new Color(255, 255, 0));

        for (;;) {
            map.generate();

            for (int x=0;x<SIZE;x++) {
                for (int y=0;y<SIZE;y++) {
                    f.place(map.biomeMap.get(x, y), x, y);
                }
            }

            view.showStatus(0, f);

            try {Thread.sleep(1000);}catch(Exception ex){System.exit(0);}
        }
    }
}