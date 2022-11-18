package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DamageTankEvent extends ExecutableEvent {
    public DamageTankEvent(GridEvent event) {
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

        //Add health parameter and do minus
        tile.health = this.pos;
        bus.post(new TileUpdateEvent(tile.getLocation(),  tile));
    }
}
