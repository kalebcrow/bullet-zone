package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyTankEvent extends GridEvent{

    private Long destroyedTankID;
    private Long destroyerTankID;
    private int bulletID;

    public DestroyTankEvent(Long destroyedTankID, Long destroyerTankID, int bulletID) {
        this.destroyedTankID = destroyedTankID;
        this.destroyerTankID = destroyerTankID;
        this.bulletID = bulletID;
        this.type = "destroyTank";
        this.time = System.currentTimeMillis();
    }

    @Override
    @UiThread
    public void execute() {
        TankTile tile =TankList.getTankList().getLocation(Math.toIntExact(destroyedTankID));
        busProvider.getEventBus().post(new TileUpdateEvent(tile.getLocation(), new BlankTile(0, tile.getLocation() )));
        TankList.getTankList().remove(Math.toIntExact(destroyedTankID));
    }
}
