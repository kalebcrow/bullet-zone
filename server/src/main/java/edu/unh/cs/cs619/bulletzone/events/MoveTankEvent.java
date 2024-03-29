package edu.unh.cs.cs619.bulletzone.events;


public class MoveTankEvent extends GridEvent {

    public MoveTankEvent(Long tankID, byte direction, int pos) {
        this.ID = Math.toIntExact(tankID);
        this.direction = direction;
        this.type = "moveTank";
        this.time = System.currentTimeMillis();
        this.pos = pos;
    }

}
