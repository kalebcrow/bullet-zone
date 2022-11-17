package edu.unh.cs.cs619.bulletzone.events;


import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.GridEvent;

public class BuildEvent extends GridEvent {

    public BuildEvent(Long tankID, int[] resources) {
        this.ID = Math.toIntExact(tankID);
        this.type = "build";
        this.time = System.currentTimeMillis();
        this.resources = resources;
    }
}