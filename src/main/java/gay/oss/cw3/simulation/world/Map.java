package gay.oss.cw3.simulation.world;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import gay.oss.cw3.lib.FastNoiseLite;
import gay.oss.cw3.renderer.ColorAverageCollector;
import gay.oss.cw3.renderer.Util;
import gay.oss.cw3.simulation.world.grid.BasicGrid;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;
import gay.oss.cw3.simulation.world.grid.Grid;

/**
 * The map holds things which exist in the world.
 */
public class Map {
    final static float SEA_FLOOR_HEIGHT = -20f;
    final float BEACH_DISTANCE_FRACTION = 0.6f; // set to 1f to disable islandification
    final float SEA_DISTANCE_FRACTION = 0.99f;

    private final int width;
    private final int depth;
    
    private final float waterLevel = -8.0f;

    private final java.util.Map<EntityLayer, Grid<Entity>> entities;
    private final java.util.Map<EntityLayer, Grid<float[]>> offsets;

    private final Grid<Float> heightMap;
    private final Grid<BiomeType> biomeMap;

    /**
     * Create a new Map
     * @param width the width
     * @param depth the depth
     */
    public Map(int width, int depth) {
        this.width = width;
        this.depth = depth;

        this.entities = new EnumMap<>(EntityLayer.class);
        this.offsets = new EnumMap<>(EntityLayer.class);

        for (EntityLayer layer : EntityLayer.values()) {
            this.entities.put(layer, new BasicGrid<>(width, depth));
            this.offsets.put(layer, new BasicGrid<>(width, depth));
        }

        this.heightMap = new BasicGrid<>(width, depth);
        this.biomeMap = new BasicGrid<>(width, depth);
    }

    /**
     * Determines if a coordinate is within bounds for the map.
     *
     * @param x  the x coordinate
     * @param z  the z coordinate
     *
     * @return   whether the coordinate is in bounds
     *
     * @see World#isInBounds(gay.oss.cw3.simulation.Coordinate)
     */
    public boolean isInBounds(int x, int z) {
        return !(x < 0 || z < 0 || x >= width || z >= depth);
    }

    /**
     * @return the width of the world
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * @return the depth of the world
     */
    public int getDepth() {
        return this.depth;
    }

    /**
     * @return the water level of the map
     */
    public float getWaterLevel() {
        return this.waterLevel;
    }

    /**
     * Get the grid of entities for a certain layer.
     *
     * @param layer the layer
     * @return      the grid of entities
     */
    public Grid<Entity> getEntities(EntityLayer layer) {
        return this.entities.get(layer);
    }

    /**
     * Get the terrain height at a certain position.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return  the terrain height
     */
    public float getHeight(int x, int z) {
        return this.heightMap.get(x, z);
    }

    /**
     * Get the grid of position offset values for a certain layer.
     *
     * @param layer the layer
     * @return      the grid of position offsets for this layer
     */
    public Grid<float[]> getOffsets(EntityLayer layer) {
        return this.offsets.get(layer);
    }

    /**
     * Get the biome at a certain position.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return  the biome type
     */
    public BiomeType getBiome(int x, int z) {
        return this.biomeMap.get(x, z);
    }

    /**
     * Get the biome colour at a certain position.
     *
     * @param x the x coordinate
     * @param z the z coordinate
     * @return  the biome colour
     */
    public float[] getBiomeColour(int x, int z) {
        return this.biomeMap.get(x, z).getColour();
    }

    /**
     * Gets the average biome colour around a certain position.
     *
     * @param xCentre the x coordinate
     * @param zCentre the z coordinate
     * @return        the average colour
     */
    public float[] getAverageBiomeColour(int xCentre, int zCentre) {
        final int radius = 5;
        final List<float[]> colours = new ArrayList<>();

        for (int x=xCentre-radius;x<=xCentre+radius;x++) {
            for (int dZ=zCentre-radius;dZ<=zCentre+radius;dZ++) {
                BiomeType entry = this.biomeMap.get(x, dZ);
                if (entry != null) {
                    float[] colour = entry.getColour();
                    colours.add(colour);
                }
            }
        }

        float[] avgResults = colours.stream()
                .map(Util::rgbToOklab)
                .collect(new ColorAverageCollector());

        return Util.oklabToRgb(avgResults);
    }

    /**
     * Generate the terrain.
     *
     * @param seed the random seed
     */
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
                    type = BiomeType.AridPlains;
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
                float height = heightNoise.GetNoise(x, z) * 40.0f;
                this.heightMap.set(x, z,
                    Util.adjustHeightToPlane(height, x, z, width, depth, BEACH_DISTANCE_FRACTION, SEA_DISTANCE_FRACTION, SEA_FLOOR_HEIGHT));
            }
        }

        // 3. Populate random rotation and position offset values
        Random random = new Random(seed);
        for (EntityLayer layer : EntityLayer.values()) {
            var offsets = this.offsets.get(layer);
            for (int x=0;x<this.width;x++) {
                for (int z=0;z<this.depth;z++) {
                    float X = 0.25f - random.nextFloat() * 0.5f;
                    float Z = 0.25f - random.nextFloat() * 0.5f;

                    offsets.set(x, z,
                        new float[] {
                            X,
                            layer.yOffset + this.getHeight(x, z),
                            Z,
                            random.nextFloat() * 2 * (float) Math.PI
                        }
                    );
                }
            }
        }
    }

    /**
     * Generate the map with a random seed.
     */
    public void generate() {
        this.generate(new Random().nextInt());
    }
}
