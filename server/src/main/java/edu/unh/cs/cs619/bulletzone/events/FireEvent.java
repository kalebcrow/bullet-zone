package edu.unh.cs.cs619.bulletzone.events;

public class FireEvent extends GridEvent {

    public FireEvent(Long tankID, int bulletID, byte direction, int pos) {
        this.ID = (Math.toIntExact(tankID) * 10) + bulletID;
        this.direction = direction;
        this.type = "fire";
        this.time = System.currentTimeMillis();
        this.pos = pos;
    }
}
