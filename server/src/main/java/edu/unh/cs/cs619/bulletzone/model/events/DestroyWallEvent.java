package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyWallEvent extends GridEvent{

    private int row;
    private int col;
    private Long tankID;
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
