package edu.unh.cs.cs619.bulletzone.events;


import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.GridEvent;

public class DismantleEvent extends GridEvent {

    public DismantleEvent(Long tankID, int[] resources) {
        this.ID = Math.toIntExact(tankID);
        this.type = "dismantle";
        this.time = System.currentTimeMillis();
        this.resources = resources;
    }
}