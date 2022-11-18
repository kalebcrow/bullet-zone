package edu.unh.cs.cs619.bulletzone.events;


public class TurnEvent extends GridEvent {

    public TurnEvent(Long tankID, byte direction) {
        this.ID = Math.toIntExact(tankID);
        this.direction = direction;
        this.type = "turn";
        this.time = System.currentTimeMillis();
    }
}
