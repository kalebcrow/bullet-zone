package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.TankController;
import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class FireEvent extends GridEvent {
    private Long tankID;
    private Long bulletID;

    public FireEvent(Long tankID, Long bulletID) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }

    @Override
    public void execute() {
        TankTile tile = TankList.getTankList().getLocation(Math.toIntExact(tankID));
        Integer orientation = tile.getOrientation();

        Integer location = tile.getLocation();

        if (orientation == 0) {

        }
    }

}
