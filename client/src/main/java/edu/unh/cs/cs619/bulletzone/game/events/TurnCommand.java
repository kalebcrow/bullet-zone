package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TankList;
import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.HistoryUpdateEvent;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;
import edu.unh.cs.cs619.bulletzone.util.EventWrapper;

public class TurnCommand extends GridEvent {

    public Integer tankID;
    public Integer direction;

    TurnCommand(Integer tankID, Integer direction) {
        this.tankID = tankID;
        this.direction = direction;
    }


    @Override
    public TileUpdateEvent execute() {
        TankTile tile = TankList.getTankList().getLocation(tankID);
        tile.setOrientation(direction);

        return new TileUpdateEvent(tile.location, tile);
    }

}
