package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class AddTankEvent extends GridEvent {

    private int row;
    private int col;
    private Long tankID;

    public AddTankEvent(int row, int col, Long tankID) {
        this.row = row;
        this.col = col;
        this.tankID = tankID;
        this.type = "addTank";
        this.time = System.currentTimeMillis();
    }

    @Override
    @UiThread
    public void execute() {
        Integer location = col * 16 + row;
        Integer orientation = 0;
        busProvider.getEventBus().post(new TileUpdateEvent(location, new TankTile(Math.toIntExact(tankID), location, orientation)));
    }
}
