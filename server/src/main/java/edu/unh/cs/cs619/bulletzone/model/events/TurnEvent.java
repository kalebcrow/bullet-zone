package edu.unh.cs.cs619.bulletzone.model.events;

import org.json.JSONException;
import org.json.JSONObject;

import edu.unh.cs.cs619.bulletzone.model.events.GridEvent;

public class TurnEvent extends GridEvent {

    private Long tankID;
    private int direction;

    @Override
    public JSONObject getJSON() {

        JSONObject json = new JSONObject();
        try {
            json.put("event", ""); // needs a format
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
