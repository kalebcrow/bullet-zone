package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.BulletList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BulletTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyBulletEvent extends ExecutableEvent {

    public DestroyBulletEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(Bus bus) {
        BulletTile tile = BulletList.getBulletList().getBulletTile(ID);

        Integer jsonValue = getJSONValueFromString(terrain);

        if (tile == null) {
            return;
        }

        bus.post(new TileUpdateEvent(tile.getLocation(), new GroundTile(jsonValue, tile.getLocation())));
        BulletList.getBulletList().removeBullet(ID);
    }

    private Integer getJSONValueFromString(String terrain) {
        // using given son values
        if (terrain.equals("H")) {
            return 2;
        } else if (terrain.equals("R")) {
            return 1;
        } else if (terrain.equals("M")) {
            return 0;
        } else {
            return -1; // something is wrong
        }
    }
}
