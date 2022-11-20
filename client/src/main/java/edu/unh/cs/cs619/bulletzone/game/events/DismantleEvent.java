package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.rest.RoadUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class DismantleEvent extends ExecutableEvent {

    public DismantleEvent(GridEvent event) {
        super(event);
    }

    @Override
    public void execute(Bus bus) {
        Integer location = pos + 1;
        if (buildType == 1) {
            bus.post(new RoadUpdateEvent(location, new GroundTile(-1, location)));
        } else {
            bus.post(new TileUpdateEvent(location, new GroundTile(-1, location)));
        }
        bus.post(new ResourceEvent(new IntArayWrapper(this.resources)));
    }
}
