package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TileFactory;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyWallEvent extends  ExecutableEvent {

    public DestroyWallEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute() {

         busProvider.getEventBus().post(new TileUpdateEvent(pos, TileFactory.getFactory().makeTile(0, pos)));
    }
}
