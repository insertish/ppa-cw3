package gay.oss.cw3.scenarios;

import org.joml.Vector3f;

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

        this.registerEntity(EntityLayer.ANIMALS, "Rabbit", new Vector3f(1, 1, 1), Rabbit.class, 0.05f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Hunter", new Vector3f(1, 1, 0), Hunter.class, 0.005f, false, true, null);
        this.registerEntity(EntityLayer.ANIMALS, "Herbivore Fish", new Vector3f(1, 0, 1), HerbivoreFish.class, 0.005f, true, false, null);
        this.registerEntity(EntityLayer.FOLIAGE, "Tree", new Vector3f(0, 1, 1), Tree.class, 0.01f, false, true, new BiomeType[] { BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Grass", new Vector3f(0, 1, 0), Grass.class, 0.15f, false, true, new BiomeType[] { BiomeType.Plains, BiomeType.Forest });
        this.registerEntity(EntityLayer.FOLIAGE, "Kelp", new Vector3f(0, 0, 1), Kelp.class, 0.15f, true, false, null);
        this.registerEntity(EntityLayer.AERIAL_ANIMALS, "Bird", new Vector3f(1, 0, 0), Bird.class, 0.005f, false, true, null);

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
            renderer.autoLoadModel(Hunter.class, "snake.png", "snake", 0.03f, false);
            renderer.autoLoadModel(Rabbit.class, "ferret.png", "ferret", 0.5f, false);
            renderer.autoLoadModel(Grass.class, "grass-transparent.png", "grass", 0.5f, true);
            renderer.autoLoadModel(Tree.class, "pine.png", "pine", 50.0f, false);
            renderer.autoLoadModel(Kelp.class, "grass-transparent.png", "grass", 0.5f, true);
            renderer.autoLoadModel(HerbivoreFish.class, "amogus.png", "amogus", 1.5f, false);
            renderer.autoLoadModel(Bird.class, "bird.png", "bird", 0.5f, false);
            //renderer.autoLoadModel(Grass.class, "pine.png", "pine", 10);
        }
    }

}
