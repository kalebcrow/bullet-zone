package edu.unh.cs.cs619.bulletzone.events;

public class RestrictionsEvent extends GridEvent {

    public RestrictionsEvent(Long tankID, int[] restrictions) {
        this.ID = Math.toIntExact(tankID);
        this.type = "restriction";
        this.time = System.currentTimeMillis();
        this.restrictions = restrictions;
    }
}
