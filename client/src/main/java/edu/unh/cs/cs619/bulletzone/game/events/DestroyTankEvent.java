package edu.unh.cs.cs619.bulletzone.game.events;

import com.squareup.otto.Bus;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.GroundTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyTankEvent extends ExecutableEvent{

    public DestroyTankEvent(GridEvent event) {
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

        bus.post(new TileUpdateEvent(tile.getLocation(), new GroundTile(5, tile.getLocation())));
        TankList.getTankList().remove(ID);
    }
}
