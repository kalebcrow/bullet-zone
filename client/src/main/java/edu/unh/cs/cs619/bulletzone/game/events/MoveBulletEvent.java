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
        int row = location / 16;
        int col = location % 16;

        Integer prevlocation = location;


        if (direction == 0) {
            location = goingUp(location);
        } else if (direction == 2) {
            location = goingRight(location);
        } else if (direction == 4) {
            location = goingDown(location);
        } else if (direction == 6) {
            location = goingLeft(location);
        }

        tile.setLocation(location);

        bus.post(new TileUpdateEvent(prevlocation, new GroundTile(5, prevlocation)));
        bus.post(new TileUpdateEvent(location, tile));
    }

    private Integer goingUp(Integer location) {
        if (location <= 15) {
            location = 240 + location;
        } else {
            location = location - 16;
        }
        return location;
    }

    private Integer goingDown(Integer location) {
        if (location >= 240) {
            location = location - 240;
        } else {
            location = location + 16;
        }
        return location;
    }

    private Integer goingRight(Integer location) {
        if ((location + 1) % 16 == 0) {
            location = location - 15;
        } else {
            location = location + 1;
        }
        return location;
    }

    private Integer goingLeft(Integer location) {
        if (location % 16 == 0) {
            location = location + 15;
        } else {
            location = location - 1;
        }
        return location;
    }
}
