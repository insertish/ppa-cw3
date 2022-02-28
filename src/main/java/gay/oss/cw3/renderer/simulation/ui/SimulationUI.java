package gay.oss.cw3.renderer.simulation.ui;

import java.util.Arrays;

import org.joml.Vector4f;

import gay.oss.cw3.renderer.simulation.ui.components.DayCycleBox;
import gay.oss.cw3.renderer.simulation.ui.components.EntityList;
import gay.oss.cw3.renderer.simulation.ui.components.HelpMenuBox;
import gay.oss.cw3.renderer.simulation.ui.components.TickText;
import gay.oss.cw3.renderer.ui.RootUI;
import gay.oss.cw3.renderer.ui.fonts.Font;
import gay.oss.cw3.renderer.ui.fonts.FontPixel;
import gay.oss.cw3.renderer.ui.framework.Box;
import gay.oss.cw3.renderer.ui.framework.Node;
import gay.oss.cw3.renderer.ui.framework.components.Text;
import gay.oss.cw3.renderer.ui.framework.layouts.Anchor;
import gay.oss.cw3.renderer.ui.framework.layouts.AnchorLayout;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowDirection;
import gay.oss.cw3.renderer.ui.framework.layouts.FlowLayout;
import gay.oss.cw3.scenarios.Scenario;
import gay.oss.cw3.simulation.world.World;

/**
 * Root node of the Simulation UI.
 */
public class SimulationUI extends RootUI {
    private Scenario scenario;

    /**
     * Construct a new SimulationUI
     * @param scenario Scenario we should gather data from
     * @throws Exception if we fail to initialise the UI
     */
    public SimulationUI(Scenario scenario) throws Exception {
        super(null);
        this.scenario = scenario;
        this.init();
    }

    /**
     * Initialise all the materials required to render the UI
     * @throws Exception if we fail to initialise the UI
     */
    private void init() throws Exception {
        Font font = new FontPixel();
        this.rootNode = new Box(
            new AnchorLayout()
                // Display the day cycle and tick counter in the top left.
                .add(
                    Anchor.TopLeft,
                    new FlowLayout(Arrays.asList(
                        new Node[] {
                            new DayCycleBox(this)
                                .setMinSize(64),
                            new Box(new TickText(font, 24, this))
                                .setPadding(8)
                                .setColour(new Vector4f(0, 0, 0, 0.5f))
                        }
                    ))
                    .setGap(8)
                )
                // Display an entity list in the bottom right.
                .add(
                    Anchor.BottomRight,
                    new EntityList(this.scenario, font)
                )
                // Display a mini help menu in the bottom left.
                .add(
                    Anchor.BottomLeft,
                    new FlowLayout(Arrays.asList(
                        new Node[] {
                            new Text(font, "press H for help menu", 16)
                        }
                    ))
                    .setDirection(FlowDirection.Column)
                )
                // Display a help menu in the centre.
                /*.add(
                    Anchor.CenterMiddle,
                    new HelpMenuBox(font)
                )*/
        )
        .setPadding(24);
    }

    /**
     * Get the current World in use by the Scenario.
     * @return World
     */
    public World getWorld() {
        return this.scenario.getWorld();
    }

    /**
     * Get the current Scenario we are gathering data from.
     * @return Scenario
     */
    public Scenario getScenario() {
        return this.scenario;
    }

    /**
     * Change Scenario are re-initialise the UI.
     * @param scenario Scenario
     */
    public void setScenario(Scenario scenario) {
        this.scenario = scenario;

        try {
            this.init();
        } catch (Exception e) {
            System.err.println("Failed to reinitialise simulation UI!");
            e.printStackTrace();
        }
    }
}
