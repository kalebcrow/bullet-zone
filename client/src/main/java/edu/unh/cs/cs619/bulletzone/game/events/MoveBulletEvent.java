package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.BulletList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class MoveBulletEvent extends ExecutableEvent {

    public MoveBulletEvent(GridEvent event) {
        super(event);
    }

    /**
     * updates the board
     */
    public void execute(Bus bus){

        BulletTile tile = BulletList.getBulletList().getBulletTile(ID);
        if (tile == null) {
            return;
        }
        Integer location = tile.getLocation();
        tile.setLocation(this.pos + 1);

        bus.post(new TileUpdateEvent(location, new GroundTile(5, location)));
        bus.post(new TileUpdateEvent(this.pos+1, tile));
    }
}
