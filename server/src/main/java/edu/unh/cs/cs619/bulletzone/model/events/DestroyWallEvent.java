package edu.unh.cs.cs619.bulletzone.model.events;

public class DestroyWallEvent extends GridEvent{

    public DestroyWallEvent(int pos, String terrain) {
        this.pos = pos;
        this.type = "destroyWall";
        this.time = System.currentTimeMillis();
        this.terrain = terrain;
    }
}
