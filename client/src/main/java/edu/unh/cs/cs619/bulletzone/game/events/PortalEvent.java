package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class PortalEvent extends ExecutableEvent {
    public PortalEvent(GridEvent event) {
        super(event);
    }

    /**
     * Execution of event
     */
    public void execute(Bus bus) {
        TankTile tile = TankList.getTankList().getLocation(ID);
        if (tile == null) {
            return;
        }

        Integer prevlocation = tile.getLocation();
        tile.setLocation(pos);
        tile.setOrientation((int) direction);

        tileUpdateEvent = new TileUpdateEvent(pos, tile);
        bus.post(tileUpdateEvent);
        bus.post(new TileUpdateEvent(prevlocation, new GroundTile(-1, prevlocation)));
    }

}
