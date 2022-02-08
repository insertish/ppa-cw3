package gay.oss.cw3.simulation.brain;

import java.util.ArrayList;
import java.util.List;

public final class EntityBrain {
    private final List<Behaviour> behaviours = new ArrayList<>();
    private final List<Behaviour> lastRunBehaviours = new ArrayList<>();

    public void addBehaviour(final Behaviour behaviour) {
        this.behaviours.add(behaviour);
    }

    public void addBehaviour(final int index, final Behaviour behaviour) {
        this.behaviours.add(index, behaviour);
    }

    public void tick() {
        for (Behaviour behaviour : this.behaviours) {
            final boolean wasOnLastTime = this.lastRunBehaviours.contains(behaviour);
            if (wasOnLastTime) {
                if (behaviour.canContinue()) {
                    behaviour.tick();
                    this.lastRunBehaviours.clear();
                    this.lastRunBehaviours.add(behaviour);
                    break;
                }
            } else if (behaviour.canStart()) {
                behaviour.start();
                behaviour.tick();
                this.lastRunBehaviours.clear();
                this.lastRunBehaviours.add(behaviour);
                break;
            }
        }
    }
}
