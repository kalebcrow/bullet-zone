package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyTankEvent extends ExecutableEvent{

    public DestroyTankEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(Bus bus) {
        TankTile tile = TankList.getTankList().getLocation(ID);

        if (tile == null) {
            return;
        }

        bus.post(new TileUpdateEvent(tile.getLocation(), new GroundTile(5, tile.getLocation())));
        TankList.getTankList().remove(ID);
    }

    /**
     * Converts the json value to the string value
     *
     * @param terrain terrain in string form
     * @return terrain in json form
     */
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
