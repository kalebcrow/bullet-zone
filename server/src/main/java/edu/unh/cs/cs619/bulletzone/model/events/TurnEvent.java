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

    public Long getTankID() {
        return tankID;
    }

    public void setTankID(Long tankID) {
        this.tankID = tankID;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
