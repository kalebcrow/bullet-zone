package edu.unh.cs.cs619.bulletzone.game.events;

import edu.unh.cs.cs619.bulletzone.game.events.GridEvent;

public class DestroyResourceEvent extends GridEvent {
    public DestroyResourceEvent() {
        //this.ID = (Math.toIntExact(tankID) * 10) + bulletID;
        this.type = "destroyResource";
        this.time = System.currentTimeMillis();
    }
}
