package gay.oss.cw3.simulation.brain;

import java.util.ArrayList;
import java.util.List;

public final class EntityBrain {
    private final List<Behaviour> behaviours = new ArrayList<>();
    private Behaviour lastRunBehaviour = null;

    public void addBehaviour(final Behaviour behaviour) {
        this.behaviours.add(behaviour);
    }

    public void addBehaviour(final int index, final Behaviour behaviour) {
        this.behaviours.add(index, behaviour);
    }

    public void tick() {
        for (Behaviour behaviour : this.behaviours) {
            final boolean wasOnLastTime = behaviour == this.lastRunBehaviour;
            if (wasOnLastTime) {
                if (behaviour.canContinue()) {
                    behaviour.tick();
                    this.lastRunBehaviour = behaviour;
                    break;
                }
            } else if (behaviour.canStart()) {
                behaviour.start();
                behaviour.tick();
                this.lastRunBehaviour = behaviour;
                break;
            }
        }
    }
}
