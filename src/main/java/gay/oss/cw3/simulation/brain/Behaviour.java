package gay.oss.cw3.simulation.brain;

public interface Behaviour {
    boolean canStart();
    boolean canContinue();
    void start();
    void tick();
}
