package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.BlankTile;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyTankEvent extends GridEvent{

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

    /**
     *
     * @param destroyedTankID the tank being destroyed
     * @param destroyerTankID the tank doing the destroying
     * @param bulletID the bullet that is doing the destroying
     */
    public DestroyTankEvent(Long destroyedTankID, Long destroyerTankID, int bulletID) {
        this.destroyedTankID = destroyedTankID;
        this.destroyerTankID = destroyerTankID;
        this.bulletID = bulletID;
        this.type = "destroyTank";
        this.time = System.currentTimeMillis();
    }

    /**
     * runs the command updating the board
     */
    @Override
    public void execute() {
        TankTile tile =TankList.getTankList().getLocation(Math.toIntExact(destroyedTankID));
        busProvider.getEventBus().post(new TileUpdateEvent(tile.getLocation(), new BlankTile(0, tile.getLocation() )));
        TankList.getTankList().remove(Math.toIntExact(destroyedTankID));
    }
}
