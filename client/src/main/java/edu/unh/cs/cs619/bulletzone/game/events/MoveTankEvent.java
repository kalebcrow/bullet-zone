package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.R;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class MoveTankEvent extends ExecutableEvent {

    public MoveTankEvent(GridEvent event) {
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
        Integer location = tile.getLocation();
        tile.setLocation(this.pos + 1);

        bus.post(new TileUpdateEvent(location, new GroundTile(5, location)));
        bus.post(new TileUpdateEvent(this.pos+1, tile));
    }
}
