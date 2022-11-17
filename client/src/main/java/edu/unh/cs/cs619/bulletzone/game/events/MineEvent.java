package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class MineEvent extends ExecutableEvent {

    public MineEvent(GridEvent event) {
        super(event);
    }

    @Override
    public void execute(Bus bus) {
        Integer location = pos;
        Integer orientation = 0;
        bus.post(new TileUpdateEvent(location, new TankTile(ID, location, orientation)));
    }
}
