package edu.unh.cs.cs619.bulletzone.events;


import java.util.HashMap;

import edu.unh.cs.cs619.bulletzone.events.GridEvent;

public class ItemEvent extends GridEvent {

    public ItemEvent(int pos, Integer resourceID) {
        this.ID = resourceID;
        this.time = System.currentTimeMillis();
        this.pos = pos;
    }
}
