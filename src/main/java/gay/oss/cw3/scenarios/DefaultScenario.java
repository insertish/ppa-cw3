package gay.oss.cw3.scenarios;

import gay.oss.cw3.scenarios.entities.*;

import gay.oss.cw3.simulation.world.attributes.BiomeType;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class DefaultScenario extends Scenario {
    public DefaultScenario(int width, int depth, boolean isOpenGL) throws Exception {
        super(width, depth, isOpenGL);
    }

    @Override
    public void init() throws Exception {
        super.init();

        this.registerEntity(EntityLayer.ANIMALS, "Rabbit", Rabbit.class, 0.05f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Hunter", Hunter.class, 0.005f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Herbivore Fish", HerbivoreFish.class, 0.005f, true, false, null);
        this.registerEntity(EntityLayer.FOLIAGE, "Tree", Tree.class, 0.01f, false, true, new BiomeType[] { BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Grass", Grass.class, 0.15f, false, true, new BiomeType[] { BiomeType.Plains, BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Kelp", Kelp.class, 0.15f, true, false, null);
        this.registerEntity(EntityLayer.AERIAL_ANIMALS, "Bird", Bird.class, 0.005f, false, true, null);

        if (this.isOpenGL) {
            // Configure models.
            var renderer = this.getRenderer();

            // amogus.png 1.0f
            // bird.png 0.3f
            // ferret.png 0.3f
            // grass.png 0.5f
            // pine.png 10.0f
            // raccoon.png 0.01f
            // snake.png 0.01f

            //renderer.autoLoadModel(Hunter.class, "hunter.jpg");
            renderer.autoLoadModel(Hunter.class, "snake.png", "snake", 0.03f);
            renderer.autoLoadModel(Rabbit.class, "ferret.png", "ferret", 0.5f);
            renderer.autoLoadModel(Grass.class, "grass-transparent.png", "grass", 0.5f);
            renderer.autoLoadModel(Tree.class, "pine.png", "pine", 50.0f);
            renderer.autoLoadModel(Kelp.class, "grass-transparent.png", "grass", 0.5f);
            renderer.autoLoadModel(HerbivoreFish.class, "amogus.png", "amogus", 1.5f);
            renderer.autoLoadModel(Bird.class, "bird.png", "bird", 0.5f);
            //renderer.autoLoadModel(Grass.class, "pine.png", "pine", 10);
        }
    }

}
