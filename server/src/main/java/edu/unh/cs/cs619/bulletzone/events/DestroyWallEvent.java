package edu.unh.cs.cs619.bulletzone.events;

public class DestroyWallEvent extends GridEvent{

    public DestroyWallEvent(int pos) {
        this.pos = pos;
        this.type = "destroyWall";
        this.time = System.currentTimeMillis();
    }
}
