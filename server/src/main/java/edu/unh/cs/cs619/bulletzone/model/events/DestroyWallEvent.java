package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyWallEvent extends GridEvent{

    public DestroyWallEvent(int row, int col) {
        this.pos = 16*row + col;
        this.type = "destroyWall";
        this.time = System.currentTimeMillis();
    }
}
