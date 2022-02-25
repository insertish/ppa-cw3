package gay.oss.cw3.tests;

import java.awt.Color;

import gay.oss.cw3.provided.Field;
import gay.oss.cw3.provided.SimulatorView;
import gay.oss.cw3.scenarios.DefaultScenario;
import gay.oss.cw3.scenarios.Scenario;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;

public class TestBrains {
    public static void main(String[] args) throws Exception {
        Scenario scenario = new DefaultScenario(128, 128, false);
        scenario.generate();

        var view = new SimulatorView(128, 128);
        var field = new Field(128, 128);
        var world = scenario.getWorld();

        view.setColor(DefaultScenario.Hunter.class, Color.ORANGE);
        view.setColor(DefaultScenario.Rabbit.class, Color.LIGHT_GRAY);
        view.setColor(DefaultScenario.Grass.class, new Color(0x00dd00));

        int lastIter;
        while (true) {
            lastIter = world.getTime();
            world.tick();

            field.clear();

            for (Entity entity : world.getEntities()) {
                if (entity instanceof DefaultScenario.Grass) continue;

                Coordinate loc = entity.getLocation();
                field.place(entity, loc.x, loc.z);
            }

            view.showStatus(lastIter, field);

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
