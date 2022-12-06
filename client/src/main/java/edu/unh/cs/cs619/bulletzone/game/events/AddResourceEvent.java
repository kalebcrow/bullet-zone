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
}
