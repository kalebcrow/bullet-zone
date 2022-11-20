package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.tiles.ObstacleTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.RoadTile;
import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.rest.RoadUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class AddObstacleEvent extends ExecutableEvent {

    public AddObstacleEvent(GridEvent event) {
        super(event);
    }


    /**
     *
     * @param bus the bus sending the event
     */
    @Override
    public void execute(Bus bus) {
        Integer location = pos + 1;
        Integer orientation = 0;
        if (buildType == 1) {
            buildType = 0;
            bus.post(new RoadUpdateEvent(location, new RoadTile(30000001, location)));
        } else if (buildType == 2) {
            buildType = 2000;
            bus.post(new TileUpdateEvent(location, new ObstacleTile(buildType, location)));
        } else {
            buildType = 1000;
            bus.post(new TileUpdateEvent(location, new ObstacleTile(buildType, location)));
        }
        if (TankController.getTankController().containsTankID(ID.longValue())) {
            bus.post(new ResourceEvent(new IntArayWrapper(this.resources)));
        }
    }
}
