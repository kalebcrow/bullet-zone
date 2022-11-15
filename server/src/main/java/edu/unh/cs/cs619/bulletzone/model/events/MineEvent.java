package edu.unh.cs.cs619.bulletzone.model.events;


import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class MineEvent extends GridEvent {

    public MineEvent(Long tankID, int[] resources) {
        this.ID = Math.toIntExact(tankID);
        this.type = "mine";
        this.time = System.currentTimeMillis();
        this.resources = resources;
    }
}
