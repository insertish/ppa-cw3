package gay.oss.cw3.tests;

import gay.oss.cw3.provided.Field;
import gay.oss.cw3.simulation.world.BiomeType;
import gay.oss.cw3.simulation.world.Map;
import gay.oss.cw3.simulation.world.SimulatorViewButCursed;

import java.awt.Color;

public class TestWorldGen {
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
                    f.place(map.getBiomeMap().get(x, y), x, y);
                }
            }

            view.showStatus(0, f);

            try {Thread.sleep(1000);}catch(Exception ex){System.exit(0);}
        }
    }
}
