package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.HistoryUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

public class TurnEvent extends GridEvent {

    public Integer tankID;
    public byte direction;

    public Integer getTankID() {
        return tankID;
    }

    public void setTankID(Integer tankID) {
        this.tankID = tankID;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte direction) {
        this.direction = direction;
    }

    /**
     *
     * @param tankID tank turning
     * @param direction direction it turns to
     */
    TurnEvent(Integer tankID, byte direction) {
        this.tankID = tankID;
        this.direction = direction;
    }


    /**
     * Do the command
     */
    @Override
    public void execute() {
        TankTile tile = TankList.getTankList().getLocation(tankID);
        tile.setOrientation((int) direction);

        busProvider.getEventBus().post(new TileUpdateEvent(tile.location, tile));
    }

}
