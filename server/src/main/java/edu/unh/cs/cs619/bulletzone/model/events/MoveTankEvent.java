package edu.unh.cs.cs619.bulletzone.model.events;

import org.json.JSONException;
import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class MoveTankEvent extends GridEvent {

    private Long tankID;
    private int forward;


    public MoveTankEvent(Long tankID, int forward) {
        this.tankID = tankID;
        this.forward = forward;
        this.type = "moveTank";
        this.time = System.currentTimeMillis();
    }
}
