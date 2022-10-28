package edu.unh.cs.cs619.bulletzone.model.events;


import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class TurnEvent extends GridEvent {

    public TurnEvent(Long tankID, byte direction) {
        this.ID = Math.toIntExact(tankID);
        this.direction = direction;
        this.type = "turn";
        this.time = System.currentTimeMillis();
    }
}
