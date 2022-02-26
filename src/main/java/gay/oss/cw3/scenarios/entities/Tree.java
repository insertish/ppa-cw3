package gay.oss.cw3.scenarios.entities;

import gay.oss.cw3.scenarios.DefaultScenario;
import gay.oss.cw3.simulation.Coordinate;
import gay.oss.cw3.simulation.entity.Entity;
import gay.oss.cw3.simulation.entity.EntityAttribute;
import gay.oss.cw3.simulation.world.World;
import gay.oss.cw3.simulation.world.attributes.DayCycle;
import gay.oss.cw3.simulation.world.attributes.EntityLayer;

public class Tree extends Entity {
    public Tree(World world, Coordinate location) {
        super(world, EntityLayer.FOLIAGE, location, 0, true);
        this.getAttributes().set(EntityAttribute.MAX_HEALTH, 10);
        this.getAttributes().set(EntityAttribute.MAX_FULLNESS, 10.0);
        this.setFullness(0.5);
        this.setHealth(this.getMaxHealth());
    }

    @Override
    public void tick() {
        if (this.isAlive()) {
            // photosynthesis
            if (this.getWorld().getDayCycle() != DayCycle.NIGHT) {
                this.addFullness(0.01);
            }

            // spreading
            if (this.getFullness() >= 5.0) {
                var locations = this.getWorld().findFreeLocationsAboveWater(this.getLayer(), this.getLocation(), 1);

                if (!locations.isEmpty()) {
                    var coord = locations.get(this.getWorld().getRandom().nextInt(locations.size()));
                    new Tree(this.getWorld(), coord);
                    this.removeFullness(2.5);
                }
            }

        }
    }
}
