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
}
