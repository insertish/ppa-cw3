package gay.oss.cw3.renderer.simulation.ui.components;

import java.io.IOException;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.objects.Texture;
import gay.oss.cw3.renderer.simulation.Resources;
import gay.oss.cw3.renderer.simulation.ui.SimulationUI;
import gay.oss.cw3.renderer.ui.UI;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.components.Image;

/**
 * UI element which shows an indicator of whether we are currently playing.
 */
public class PlayStateBox extends Box {
    private SimulationUI simulationUI;

    private Texture pauseTexture;
    private Texture playTexture;

    /**
     * Construct a new PlayStateBox
     * @param simulationUI Simulation UI
     * @throws IOException if it fails to initialise textures
     */
    public PlayStateBox(SimulationUI simulationUI) throws IOException {
        super(new Image(null));
        this.simulationUI = simulationUI;

        pauseTexture = Resources.getTexture("ui/paused.png");
        playTexture = Resources.getTexture("ui/playing.png");

        this.setPadding(8);
        this.setColour(new Vector4f(0, 0, 0, 0.5f));
    }
    
    @Override
    public void draw(UI ui, int x, int y, int w, int h) {
        ((Image) this.child).setTexture(
            this.simulationUI.isPlaying()
                ? playTexture
                : pauseTexture
        );

        super.draw(ui, x, y, w, h);
    }
}
