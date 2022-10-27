package edu.unh.cs.cs619.bulletzone.model.events;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class FireEvent extends GridEvent {

    private Long tankID;
    private int bulletID;
    private int direction;

    public FireEvent(Long tankID, int bulletID, int direction) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.direction = direction;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }
}
