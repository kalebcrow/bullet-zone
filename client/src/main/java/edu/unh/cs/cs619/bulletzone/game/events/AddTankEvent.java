package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.tiles.TankTile;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class AddTankEvent extends ExecutableEvent {

    private int row;
    private int col;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public Long getTankID() {
        return tankID;
    }

    public void setTankID(Long tankID) {
        this.tankID = tankID;
    }

    private Long tankID;

    public AddTankEvent(GridEvent event) {
        super(event);
    }


    /**
     * runs the command updating the board
     */
    @Override
    public void execute() {
        Integer location = col * 16 + row;
        Integer orientation = 0;
        busProvider.getEventBus().post(new TileUpdateEvent(location, new TankTile(Math.toIntExact(tankID), location, orientation)));
    }
}
