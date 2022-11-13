package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.ObstacleTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

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
        Integer location = pos;
        Integer orientation = 0;
        bus.post(new TileUpdateEvent(location, new ObstacleTile(ID, location)));
    }
}
