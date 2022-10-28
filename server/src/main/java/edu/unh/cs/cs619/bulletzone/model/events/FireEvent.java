package edu.unh.cs.cs619.bulletzone.model.events;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class FireEvent extends GridEvent {

    public FireEvent(Long tankID, int bulletID, byte direction) {
        this.ID = (Math.toIntExact(tankID) * 10) + bulletID;
        this.direction = direction;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }
}
