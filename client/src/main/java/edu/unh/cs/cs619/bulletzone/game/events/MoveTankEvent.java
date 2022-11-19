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

        Log.d("Moving: ", "TankID: " + tile.ID + " location: " + tile.location);


        tileUpdateEvent = new TileUpdateEvent(location, tile);
        bus.post(tileUpdateEvent);
        bus.post(new TileUpdateEvent(prevlocation, new GroundTile(5, location)));
    }

    /**
     * Converts the json value to the string value
     *
     * @param terrain terrain in string form
     * @return terrain in json form
     */
    private Integer getJSONValueFromString(String terrain) {
        // using given json values
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
