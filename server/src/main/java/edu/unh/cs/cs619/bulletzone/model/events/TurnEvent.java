package edu.unh.cs.cs619.bulletzone.model.events;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class TurnEvent extends GridEvent {

    private Long tankID;
    private int direction;

    public TurnEvent(Long tankID, int direction) {
        this.tankID = tankID;
        this.direction = direction;
        this.type = "turn";
        this.time = System.currentTimeMillis();
    }
}
