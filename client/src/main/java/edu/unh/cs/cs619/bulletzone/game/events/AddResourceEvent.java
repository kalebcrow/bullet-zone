package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.ResourceTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.ResourceEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.IntArayWrapper;

public class AddResourceEvent extends ExecutableEvent {
    public AddResourceEvent(GridEvent event) {
        super(event);
    }

    @Override
    public void execute(Bus bus) {
        int jsonVal = getJSONValueFromString(resource);

        bus.post(new TileUpdateEvent(pos, new ResourceTile(jsonVal, pos)));
    }

    /**
     * Converts the json value to the string value
     *
     * @param resource resource in string form
     * @return resource in json form
     */
    private Integer getJSONValueFromString(String resource) {
        // using given son values
        if (resource.equals("CB")) {
            return 501;
        } else if (resource.equals("RB")) {
            return 502;
        } else if (resource.equals("IB")) {
            return 503;
        } else if (resource.equals("TB")){
            return 7;
        } else {
            return -1;  // something is wrong
        }
    }
}
