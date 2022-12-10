package edu.unh.cs.cs619.bulletzone.events;

public class PortalEvent extends GridEvent {

    public PortalEvent(Long tankID, byte direction, int pos) {
        this.ID = Math.toIntExact(tankID);
        this.direction = direction;
        this.type = "portal";
        this.pos = pos;
        this.time = System.currentTimeMillis();
    }

}