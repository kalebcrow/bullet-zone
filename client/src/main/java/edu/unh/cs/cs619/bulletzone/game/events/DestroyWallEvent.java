package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.events.BusProvider;
import edu.unh.cs.cs619.bulletzone.game.TileFactory;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyWallEvent extends  ExecutableEvent {

    public DestroyWallEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(Bus bus) {
        int jsonValue = getJSONValueFromString(terrain);

        bus.post(new TileUpdateEvent(pos, TileFactory.getFactory().makeTile(-1, pos)));
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
