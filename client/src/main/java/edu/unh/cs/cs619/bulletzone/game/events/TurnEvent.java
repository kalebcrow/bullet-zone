package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.HistoryUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

public class TurnEvent extends ExecutableEvent {

    public TurnEvent(GridEvent event) {
        super(event);
    }

    /**
     * Do the command
     */
    public void execute(BusProvider busProvider) {
        TankTile tile = TankList.getTankList().getLocation(ID);
        tile.setOrientation((int) direction);

        busProvider.getEventBus().post(new TileUpdateEvent(tile.location, tile));
    }

}
