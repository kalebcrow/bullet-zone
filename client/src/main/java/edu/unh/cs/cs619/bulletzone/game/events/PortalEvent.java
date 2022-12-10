package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.BulletList;
import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.BalenceUpdate;
import edu.unh.cs.cs619.bulletzone.rest.BoardUpdate;
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
        BulletTile bulletTile = BulletList.getBulletList().getBulletTile(ID);
        if (tile == null && bulletTile == null) {
            return;
        }

        if (tile != null) {
            if (TankController.getTankController().containsTankID(Long.valueOf(ID))) {
                bus.post(new BoardUpdate( pos/256));
            }
            Integer prevlocation = tile.getLocation();
            tile.setLocation(pos);
            tile.setOrientation((int) direction);


            tileUpdateEvent = new TileUpdateEvent(pos, tile);
            bus.post(tileUpdateEvent);
            bus.post(new TileUpdateEvent(prevlocation, new GroundTile(-1, prevlocation)));
        } else {
            Integer prevlocation = bulletTile.getLocation();
            bulletTile.setLocation(pos);
            bulletTile.setOrientation((int) direction);

            tileUpdateEvent = new TileUpdateEvent(pos, bulletTile);
            bus.post(tileUpdateEvent);
            bus.post(new TileUpdateEvent(prevlocation, new GroundTile(-1, prevlocation)));
        }

    }

}
