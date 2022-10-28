package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class AddTankEvent extends ExecutableEvent {

    public AddTankEvent(GridEvent event) {
        super(event);
    }


    /**
     * runs the command updating the board
     */
    @Override
    public void execute(BusProvider busProvider) {
        Integer location = pos - 1;
        Integer orientation = 0;
        busProvider.getEventBus().post(new TileUpdateEvent(location, new TankTile(ID, location, orientation)));
    }
}
