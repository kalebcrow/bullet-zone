package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.BulletList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyBulletEvent extends ExecutableEvent {

    public DestroyBulletEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(BusProvider busProvider) {
        BulletTile tile = BulletList.getBulletList().getBulletTile(ID);
        busProvider.getEventBus().post(new TileUpdateEvent(tile.getLocation(), new BlankTile(0, tile.getLocation())));
        BulletList.getBulletList().removeBullet(ID);
    }
}
