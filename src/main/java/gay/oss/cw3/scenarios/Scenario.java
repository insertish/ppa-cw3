package gay.oss.cw3.scenarios;

import gay.oss.cw3.renderer.simulation.WorldRenderer;
import gay.oss.cw3.simulation.generation.WorldGenerator;
import gay.oss.cw3.simulation.world.World;

public class Scenario {
    private World world;
    private WorldGenerator generator;

    private boolean isOpenGL;
    private WorldRenderer renderer;

    public Scenario(int width, int depth, boolean isOpenGL) throws Exception {
        this.world = new World(width, depth);
        this.generator = new WorldGenerator(world);

        this.isOpenGL = isOpenGL;

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
