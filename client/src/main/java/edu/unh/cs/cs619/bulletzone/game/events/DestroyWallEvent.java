package edu.unh.cs.cs619.bulletzone.game.events;

import org.androidannotations.annotations.UiThread;

import edu.unh.cs.cs619.bulletzone.game.TileFactory;
import edu.unh.cs.cs619.bulletzone.rest.TileUpdateEvent;

public class DestroyWallEvent extends  GridEvent {
    private int row;
    private int col;
    private Long tankID;

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

    public int getBulletID() {
        return bulletID;
    }

    public void setBulletID(int bulletID) {
        this.bulletID = bulletID;
    }

    private int bulletID;

    /**
     *
     * @param row the row
     * @param col the colomn
     */
    public DestroyWallEvent(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = "destroy";
        this.time = System.currentTimeMillis();
    }

    /**
     * runs the command updating the board
     */
    @Override
    public void execute() {
        Integer location = row * 16 + col;

         busProvider.getEventBus().post(new TileUpdateEvent(location, TileFactory.getFactory().makeTile(0, 0)));
    }
}
