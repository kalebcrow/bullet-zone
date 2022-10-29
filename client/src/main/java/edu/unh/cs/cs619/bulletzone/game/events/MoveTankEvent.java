package edu.unh.cs.cs619.bulletzone.game.events;

import android.util.Log;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
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

        tileUpdateEvent = new TileUpdateEvent(location, tile);
        bus.post(tileUpdateEvent);
        bus.post(new TileUpdateEvent(prevlocation, new BlankTile(0, prevlocation)));
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingUp(Integer location) {
        if (location <= 15) {
            location = 240 + location;
        } else {
            location = location - 16;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingDown(Integer location) {
        if (location >= 240) {
            location = location - 240;
        } else {
            location = location + 16;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingRight(Integer location) {
        if ((location + 1) % 16 == 0) {
            location = location - 15;
        } else {
            location = location + 1;
        }
        return location;
    }

    /**
     *
     * @param location starting location
     * @return ending location
     */
    private Integer goingLeft(Integer location) {
        if (location % 16 == 0) {
            location = location + 15;
        } else {
            location = location - 1;
        }
        return location;
    }

}
