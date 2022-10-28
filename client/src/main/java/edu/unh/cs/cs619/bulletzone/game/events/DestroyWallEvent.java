package edu.unh.cs.cs619.bulletzone.game.events;

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
    public void execute(BusProvider busProvider) {

         busProvider.getEventBus().post(new TileUpdateEvent(pos - 1, TileFactory.getFactory().makeTile(0, pos)));
    }
}
