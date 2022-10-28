package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyTankEvent extends ExecutableEvent{

    private Long destroyedTankID;

    public Long getDestroyedTankID() {
        return destroyedTankID;
    }

    public void setDestroyedTankID(Long destroyedTankID) {
        this.destroyedTankID = destroyedTankID;
    }

    public Long getDestroyerTankID() {
        return destroyerTankID;
    }

    public void setDestroyerTankID(Long destroyerTankID) {
        this.destroyerTankID = destroyerTankID;
    }

    public int getBulletID() {
        return bulletID;
    }

    public void setBulletID(int bulletID) {
        this.bulletID = bulletID;
    }

    private Long destroyerTankID;
    private int bulletID;

    public DestroyTankEvent(GridEvent event) {
        super(event);
    }

    /**
     * runs the command updating the board
     */
    public void execute() {
        TankTile tile =TankList.getTankList().getLocation(Math.toIntExact(destroyedTankID));
        busProvider.getEventBus().post(new TileUpdateEvent(tile.getLocation(), new BlankTile(0, tile.getLocation() )));
        TankList.getTankList().remove(Math.toIntExact(destroyedTankID));
    }
}
