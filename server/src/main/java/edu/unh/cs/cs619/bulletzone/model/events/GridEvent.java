package edu.unh.cs.cs619.bulletzone.model.events;


import org.json.JSONException;
import org.json.JSONObject;

public abstract class GridEvent {

    //Time of event which is needed for getting the right events for the client
    private Long Time;


    public Long getTime(){return this.Time;}


    //gets JSON for client to interpret
    public abstract JSONObject getJSON();

}
