package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.tiles.ObstacleTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DamageWallEvent extends ExecutableEvent {

    public DamageWallEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute(Bus bus) {


        bus.post(new TileUpdateEvent(this.pos, new ObstacleTile(this.ID, this.pos)));
    }

}
