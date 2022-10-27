package edu.unh.cs.cs619.bulletzone.model.events;

import org.json.JSONException;
import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class MoveTankEvent extends GridEvent {

    private Long tankID;
    private int direction;


    public MoveTankEvent(Long tankID, int direction) {
        this.tankID = tankID;
        this.direction = direction;
        this.type = "moveTank";
        this.time = System.currentTimeMillis();
    }
}
