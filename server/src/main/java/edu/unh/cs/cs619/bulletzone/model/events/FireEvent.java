package edu.unh.cs.cs619.bulletzone.model.events;

import org.json.JSONException;
import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class FireEvent extends GridEvent {

    private Long tankID;
    private Long bulletID;

    public FireEvent(Long tankID, Long bulletID) {
        this.tankID = tankID;
        this.bulletID = bulletID;
        this.type = "fire";
        this.time = System.currentTimeMillis();
    }
}
