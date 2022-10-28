package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyTankEvent extends ExecutableEvent{

    public DestroyTankEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(BusProvider busProvider) {
        TankTile tile =TankList.getTankList().getLocation(ID);

        if (tile == null) {
            return;
        }

        busProvider.getEventBus().post(new TileUpdateEvent(tile.getLocation(), new BlankTile(0, tile.getLocation())));
        TankList.getTankList().remove(ID);
    }
}
