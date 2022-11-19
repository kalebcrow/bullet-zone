package edu.unh.cs.cs619.bulletzone.events;

public class DestroyResourceEvent extends GridEvent{

    public DestroyResourceEvent(int pos, String terrain) {
        this.pos = pos;
        this.type = "destroyResource";
        this.time = System.currentTimeMillis();
        this.terrain = terrain;
    }
}
