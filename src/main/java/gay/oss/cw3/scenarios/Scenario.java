package gay.oss.cw3.scenarios;

import gay.oss.cw3.renderer.simulation.WorldRenderer;
import gay.oss.cw3.simulation.generation.WorldGenerator;
import gay.oss.cw3.simulation.world.World;

public class Scenario {
    private int width;
    private int depth;

    private World world;
    private WorldGenerator generator;

    protected boolean isOpenGL;
    private WorldRenderer renderer;

    public Scenario(int width, int depth, boolean isOpenGL) throws Exception {
        this.width = width;
        this.depth = depth;
        this.isOpenGL = isOpenGL;

        this.init();
    }

    public void init() throws Exception {
        this.world = new World(this.width, this.depth);
        this.generator = new WorldGenerator(world);

        if (isOpenGL) {
            this.renderer = new WorldRenderer(world);
        }
    }

    public void generate() throws Exception {
        this.generator.generate();

        if (this.isOpenGL) {
            this.renderer.init();
        }
    }

    public World getWorld() {
        return this.world;
    }

    protected WorldGenerator getGenerator() {
        return this.generator;
    }

    public WorldRenderer getRenderer() {
        return this.renderer;
    }
}
