package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.ItemTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class AddItemEvent extends ExecutableEvent {

    public AddItemEvent(GridEvent event) {
        super(event);
    }

    /**
     *
     */
    @Override
    public void execute(Bus bus) {
        Integer location = pos + 1;
        bus.post(new TileUpdateEvent(location, new ItemTile(ID, location)));
    }
}
