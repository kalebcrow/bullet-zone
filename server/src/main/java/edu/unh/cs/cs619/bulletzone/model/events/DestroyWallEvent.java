package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyWallEvent extends GridEvent{

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

    public DestroyWallEvent(int row, int col, Long tankID, int bulletID) {
        this.row = row;
        this.col = col;
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.type = "destroyWall";
        this.time = System.currentTimeMillis();
    }
}
